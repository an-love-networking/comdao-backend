package com.comdao.api.order;

import com.comdao.api.cart.CartItemRepository;
import com.comdao.api.cart.entities.CartItem;
import com.comdao.api.cart.exceptions.CartItemNotFoundException;
import com.comdao.api.email.EmailService;
import com.comdao.api.email.InvoiceGenerator;
import com.comdao.api.order.dto.OrderCreationRequestDto;
import com.comdao.api.order.dto.OrderResponseDto;
import com.comdao.api.order.dto.ShortOrderResponseDto;
import com.comdao.api.order.entities.Order;
import com.comdao.api.order.entities.enums.State;
import com.comdao.api.order.exceptions.OrderAlreadyPaidException;
import com.comdao.api.order.exceptions.OrderCreateViolationException;
import com.comdao.api.order.exceptions.OrderNotExistException;
import com.comdao.api.order.exceptions.OrderNotPaidException;
import com.comdao.api.order_items.OrderItemRepository;
import com.comdao.api.order_items.dto.SimpleOrderItemDto;
import com.comdao.api.order_items.entities.OrderItem;
import com.comdao.api.payment.PaymentService;
import com.comdao.api.payment.dto.PaymentInfo;
import com.comdao.api.payment.exceptions.PaymentViolationException;
import com.comdao.api.product.ProductRepository;
import com.comdao.api.product.entities.Product;
import com.comdao.api.product.exceptions.ProductNotExistException;
import com.comdao.api.user.UserChecker;
import com.comdao.api.user.entities.User;
import com.comdao.api.user.exceptions.UserDisabledException;
import com.comdao.api.user.exceptions.UserNotFoundException;
import com.comdao.api.user.repositories.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final UserChecker userChecker;
    private final PaymentService paymentService;
    private final EmailService emailService;
    private final InvoiceGenerator invoiceGenerator;
    private final CartItemRepository cartItemRepository;


    /**
     * <p>Return a page of orders with sufficient details to display on frontend</p>
     *
     * @param userId the userId extracted from JWT
     * @param page   the page index (start from 0)
     * @param size   the size of the page (greater than 0)
     * @return page contains {@code size} items offset by {@code page} page
     * @author an-love-networking
     * @see ShortOrderResponseDto
     * @since 1.0
     */
    @Transactional(readOnly = true)
    public Page<ShortOrderResponseDto> getUnfinishedOrder(Long userId, Integer page, Integer size)
            throws UserNotFoundException, UserDisabledException {
        User user = userChecker.checkExistAndActiveById(userId);

        return orderRepository.findByCustomer_IdAndStateInOrderByStatePriority(
                        userId,
                        List.of(State.PAYING, State.DELIVERING),
                        PageRequest.of(page, size)
                )
                .map(orderMapper::toShortOrderResponse);
    }


    @Transactional(readOnly = true)
    public Page<ShortOrderResponseDto> getFinishedOrders(Long userId, Integer page, Integer size)
            throws UserNotFoundException, UserDisabledException {
        User user = userChecker.checkExistAndActiveById(userId);

        return orderRepository.findByCustomer_IdAndStateInOrderByStatePriority(
                        userId,
                        List.of(State.FINISHED, State.CANCELLED),
                        PageRequest.of(page, size)
                )
                .map(orderMapper::toShortOrderResponse);
    }


    /**
     * <p>Check if the order item list is valid to create an order</p>
     *
     * @param list the list of items to process
     * @return <ul>
     * <li>{@code 0} if all item is valid</li>
     * <li>{@code 1} if a product does not exist or is not available</li>
     * <li>{@code 2} if an order item does not belong to the cart of the user</li>
     * </ul>
     * @author an-love-networking
     * @see SimpleOrderItemDto
     * @since 1.0
     */
    @Transactional(readOnly = true)
    public Integer checkProductExistAndOrderItemPending(Set<SimpleOrderItemDto> list, Long userId) {
        for (SimpleOrderItemDto orderItem : list) {
            if (orderItem.getProductId() != null && !productRepository.existsByIdAndRetrievableTrue(orderItem.getProductId()))
                return 1;

            // if order item exists but is in shopping cart of the specified user
            if (orderItem.getCartItemId() != null) {
                CartItem cartItem = cartItemRepository.findById(orderItem.getCartItemId()).orElse(null);
                if (cartItem == null || !cartItem.getUser().getId().equals(userId))
                    return 2;
                if (!cartItem.getProduct().getRetrievable())
                    return 3;
            }
        }
        return 0;
    }

    /**
     * <p>Create a new order given a set of order items and a discount</p>
     *
     * @param userId   the customer's id
     * @param newOrder the order creation info, consist of a list of items and a discount
     * @throws UserDisabledException         if the user is disabled in the database
     * @throws UserNotFoundException         if the user does not exist in the database
     * @throws OrderCreateViolationException if the discount is invalid
     * @throws ProductNotExistException      if the product in the item list is unexisted or unavailable
     * @author an-love-networking
     * @see OrderCreationRequestDto
     * @since 1.0
     */
    @Transactional
    public void createOrder(Long userId, OrderCreationRequestDto newOrder)
            throws UserDisabledException, UserNotFoundException,
            OrderCreateViolationException, ProductNotExistException,
            CartItemNotFoundException {
        if (newOrder.getDiscount() == null) newOrder.setDiscount(0.0);
        if (newOrder.getDiscount() < 0 || newOrder.getDiscount() > 1)
            throw new OrderCreateViolationException("Discount cannot be negative or larger than 100%");

        User user = userChecker.checkExistAndActiveById(userId);

        // check if the product exist or the order item is in the cart
        switch (checkProductExistAndOrderItemPending(newOrder.getOrderItems(), userId)) {
            case 1:
            case 3:
                throw new ProductNotExistException("Creating an order with an unexisted or unavailable product");
            case 2:
                throw new CartItemNotFoundException("Cart item does not exist");
            default:
                break;
        }

        // save an instance of Order with sufficient data
        Order order = new Order();
        order.setCustomer(user);
        order.setState(State.PAYING);
        order.setCreated(LocalDateTime.now());
        order.setDiscount(newOrder.getDiscount());
        order.setCurrency("VND");
        order = orderRepository.save(order);

        // then convert from the list to OrderItem and save the order items
        Set<OrderItem> orderItems = order.getOrderItems();
        for (SimpleOrderItemDto orderItem_ : newOrder.getOrderItems()) {
            System.out.println(orderItem_);
            OrderItem orderItem = new OrderItem();
            Product product;
            CartItem cartItem = null;

            // if cart_item_id is given
            if (orderItem_.getCartItemId() != null) {
                cartItem = cartItemRepository.findById(orderItem_.getCartItemId()).get();

                orderItem.setProduct(cartItem.getProduct());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setNote(orderItem_.getNote());
            }
            // if product_id is given
            if (orderItem_.getProductId() != null) {
                product = productRepository.findById(orderItem_.getProductId()).get();
                orderItem.setProduct(product);
                orderItem.setNote(orderItem_.getNote());
                orderItem.setQuantity(orderItem_.getQuantity());
            }

            orderItem.setOrder(order);
            orderItem = orderItemRepository.save(orderItem);
            orderItems.add(orderItem);

            if (cartItem != null)
                cartItemRepository.delete(cartItem);
        }
        System.out.println("Finish adding order items to order" + order);
        // finally calculate the total of the order and save
        Double subtotal = order.getOrderItems().stream().mapToDouble(
                orderItem -> orderItem.getProduct().getPrice() * orderItem.getQuantity()
        ).sum();
        System.out.println("Subtotal = " + subtotal);
        order.setSubtotal(subtotal);
        order.setTotal(order.getSubtotal() * (1 - order.getDiscount()));
        System.out.println("Total = " + (order.getSubtotal() * (1 - order.getDiscount())));

        order = orderRepository.save(order);
        System.out.println("finish creating order" + order);
    }


    /**
     * <p>return the detailed order</p>
     *
     * @param orderId the order's id
     * @return an order object with all the neccessary details
     * @throws OrderNotExistException if the order does not exist in the database
     * @author an-love-networking
     * @see OrderResponseDto
     * @since 1.0
     */
    @Transactional(readOnly = true)
    public OrderResponseDto getOrder(Long orderId, Long userId) throws OrderNotExistException {
        Order order = orderRepository.findByIdAndCustomer_Id(orderId, userId).orElseThrow(
                () -> new OrderNotExistException("This order does not exist")
        );
        return orderMapper.toOrderResponse(order);
    }

    @Transactional
    public PaymentInfo getPaymentInfo(Long orderId, Long userId)
            throws OrderNotExistException, PaymentViolationException, OrderAlreadyPaidException {
        Order order = orderRepository.findByIdAndCustomer_Id(orderId, userId).orElseThrow(
                () -> new OrderNotExistException("This order does not exist")
        );
        if (!order.getState().equals(State.PAYING)) throw new OrderAlreadyPaidException("This order has been paid");

        if (order.getPaymentQrCode() == null) {
            PaymentInfo payInfo = paymentService.createPayment(order);
            order.setPaymentQrCode(payInfo.getQrCode());
            orderRepository.save(order);
        }

        return new PaymentInfo(order.getPaymentQrCode());
    }

    public void cancelOrder(Long orderId, Long userId)
            throws OrderNotExistException {
        Order order = orderRepository.findByIdAndCustomer_Id(orderId, userId).orElseThrow(
                () -> new OrderNotExistException("This order does not exist")
        );
        order.setState(State.CANCELLED);
        order.setCancelled(LocalDateTime.now());
        orderRepository.save(order);
    }

    public void getInvoice(Long userId, Long orderId)
            throws OrderNotPaidException, OrderNotExistException, MessagingException {
        Order order = orderRepository.findByIdAndCustomer_Id(orderId, userId).orElseThrow(
                () -> new OrderNotExistException("This order does not exist")
        );

        if (order.getState().equals(State.PAYING) || order.getState().equals(State.CANCELLED))
            throw new OrderNotPaidException("Cannot generate an invoice for unpaid or cancelled orders");
        if (order.getCustomer().getEmail() == null || order.getCustomer().getEmail().isBlank())
            throw new IllegalStateException("Cannot send invoice to user without an email");

        emailService.sendHtmlMail(
                order.getCustomer().getEmail(),
                "Order #%d's invoice".formatted(order.getId()),
                invoiceGenerator.generate(order)
        );
        System.out.println("finish sending invoice");
    }

//    @Bean
//    public CommandLineRunner customMailInvoice() {
//        Order order = new Order(
//                36,
//                new User(12, "thanhan09", "thanhan09@gmail.com", null, "12345678", null, null, new Date(), Tier.PLASTIC, Role.USER, true, null, null, 0),
//                State.CONFIRMED,
//                new Date(),
//                null,
//                null,
//        )
//    }
}
