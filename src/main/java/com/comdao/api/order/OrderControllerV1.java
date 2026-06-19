package com.comdao.api.order;

import com.comdao.api.cart.exceptions.CartItemNotFoundException;
import com.comdao.api.order.dto.OrderCreationRequestDto;
import com.comdao.api.order.dto.OrderResponseDto;
import com.comdao.api.order.dto.ShortOrderResponseDto;
import com.comdao.api.order.exceptions.OrderAlreadyPaidException;
import com.comdao.api.order.exceptions.OrderCreateViolationException;
import com.comdao.api.order.exceptions.OrderNotExistException;
import com.comdao.api.order.exceptions.OrderNotPaidException;
import com.comdao.api.payment.dto.PaymentInfo;
import com.comdao.api.payment.exceptions.PaymentViolationException;
import com.comdao.api.product.exceptions.ProductNotExistException;
import com.comdao.api.user.exceptions.UserDisabledException;
import com.comdao.api.user.exceptions.UserNotFoundException;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * <p>A RESTful controller for order related request</p>
 * <p>Base URI: /api/v1/order</p>
 *
 * @author an-love-networking
 * @see OrderService
 * @since 1.0
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/order")
public class OrderControllerV1 {
    private final OrderService orderService;


    /**
     * <p>Request all orders in short form</p>
     * <pre>
     *     GET /api/v1/order/view/all
     * </pre>
     * <p>Require: JWT token in header</p>
     *
     * @param userId the user's id extracted in the JWT
     * @param page   the page index
     * @param size   the page size
     * @return <ul>
     * <li>Code {@code 200 OK} with at most {@code size} orders in short form</li>
     * <li>Code {@code 401 UNAUTHORIZED} if no JWT is provided</li>
     * <li>Code {@code 400 BAD_REQUEST} if {@code page} is negative or {@code size} if less than 1</li>
     * </ul>
     * @author an-love-networking
     * @see ShortOrderResponseDto
     * @since 1.0
     */
    @GetMapping("view/unfinished")
    public ResponseEntity<Page<ShortOrderResponseDto>> getUnfinished(
            @AuthenticationPrincipal Long userId,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size
    ) throws UserNotFoundException, UserDisabledException {
        log.info("User #{} is fetching {} unfinished orders from the {}-th page", userId, size, page);
        return ResponseEntity.status(HttpStatus.OK)
                .body(orderService.getUnfinishedOrder(userId, page, size));
    }


    @GetMapping("view/finished")
    public ResponseEntity<Page<ShortOrderResponseDto>> getFinishedOrders(
            @AuthenticationPrincipal Long userId,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size
    ) throws UserNotFoundException, UserDisabledException {
        log.info("User #{} is fetching {} finished orders from the {}-th page", userId, size, page);
        return ResponseEntity.status(HttpStatus.OK)
                .body(orderService.getFinishedOrders(userId, page, size));
    }


    /**
     * <p>Create an order from request's body info</p>
     * <pre>
     *     POST /api/v1/order/create
     *     body {
     *          "discount": 0.1,
     *          "order_items": [
     *              {
     *                  "product_id": 2,
     *                  "quantity": 4,
     *                  "note": "note here"
     *              },
     *              {
     *                  "order_item_id": 2,
     *                  "note": "note here too"
     *              },
     *              ...
     *          ]
     *     }
     * </pre>
     *
     * @param userId   the user's id
     * @param newOrder the new order's info
     * @return <ul>
     * <li>Code {@code 202 CREATED} if success</li>
     * <li>Code {@code 401 UNAUTHORIZED} if no JWT is provided</li>
     * <li>Code {@code 400 BAD_REQUEST} if order creation info is invalid</li>
     * </ul>
     * @throws UserDisabledException         response with code {@code 409 CONFLICT}
     * @throws OrderCreateViolationException response with code {@code 400 BAD_REQUEST}
     * @throws UserNotFoundException         response with code {@code 400 BAD_REQUEST}
     * @throws ProductNotExistException      response with code {@code 400 BAD_REQUEST}
     * @author an-love-networking
     * @see OrderCreationRequestDto
     * @since 1.0
     */
    @PostMapping("create")
    public ResponseEntity<Void> createOrder(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody OrderCreationRequestDto newOrder
    ) throws UserDisabledException, OrderCreateViolationException,
            UserNotFoundException, ProductNotExistException,
            PaymentViolationException, CartItemNotFoundException {
        log.info("Order creation {} from user #{}", newOrder, userId);
        orderService.createOrder(userId, newOrder);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    /**
     * <p>Return the detailed order's info</p>
     * <pre>
     *     GET /api/v1/order/view?id=1
     * </pre>
     *
     * @param userId  the user's id extracted through JWT
     * @param orderId the order's id
     * @return Code {@code 200 OK} with the detailed order object after update
     * @throws OrderNotExistException if the order does not exist in database (code {@code 400 BAD_REQUEST})
     * @author an-love-networking
     * @since 1.0
     */
    @GetMapping("view")
    public ResponseEntity<OrderResponseDto> getOrder(
            @AuthenticationPrincipal Long userId,
            @RequestParam(name = "id") Long orderId
    ) throws OrderNotExistException {
        log.info("User #{} view order #{}", userId, orderId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(orderService.getOrder(orderId, userId));
    }


    /**
     * <p>Return the payment's info (qrCode or link) to user</p>
     * <pre>
     *     GET /api/v1/order/pay?id=1
     *     -> paying for order id#1
     *     ! require jwt token
     * </pre>
     *
     * @param userId  user's id
     * @param orderId order's id
     * @return code 200 OK with JSON
     * <pre>
     *     {
     *         "redirect": link for redirecting to paying site,
     *         "qr_code": a qrcode string to render on the client
     *     }
     * </pre>
     * @throws OrderAlreadyPaidException if order already paid
     * @throws PaymentViolationException
     * @throws OrderNotExistException
     */
    @GetMapping("pay")
    public ResponseEntity<PaymentInfo> getPaymentInfo(
            @AuthenticationPrincipal Long userId,
            @RequestParam(name = "id") Long orderId
    ) throws OrderAlreadyPaidException, PaymentViolationException, OrderNotExistException {
        log.info("User #{} getting payment info of order #{}", userId, orderId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(orderService.getPaymentInfo(orderId, userId));
    }


    @GetMapping("invoice")
    public ResponseEntity<Void> getInvoiceInMail(
            @AuthenticationPrincipal Long userId,
            @RequestParam(name = "id") Long orderId
    ) throws OrderNotPaidException, OrderNotExistException, MessagingException {
        log.info("User #{} is getting invoice of order #{}", userId, orderId);
        orderService.getInvoice(userId, orderId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @DeleteMapping("cancel")
    public ResponseEntity<Void> cancelOrder(
            @AuthenticationPrincipal Long userId,
            @RequestParam(name = "id") Long orderId,
            @RequestParam(name = "confirm") Boolean confirm
    ) throws OrderNotExistException {
        log.info("User #{} is {}cancelling order #{}",
                userId, confirm ? "" : "not ", orderId);
        if (!confirm) return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        orderService.cancelOrder(orderId, userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
