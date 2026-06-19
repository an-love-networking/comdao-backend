package com.comdao.api.payment;

import com.comdao.api.order.OrderRepository;
import com.comdao.api.order.entities.Order;
import com.comdao.api.order.entities.enums.State;
import com.comdao.api.payment.exceptions.PaymentViolationException;
import com.comdao.api.user.UserChecker;
import com.comdao.api.user.entities.User;
import com.comdao.api.user.repositories.UserRepository;
import com.comdao.api.websocket.WebSocketController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.payos.PayOS;
import vn.payos.model.webhooks.Webhook;
import vn.payos.model.webhooks.WebhookData;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
public class PaymentControllerV1 {
    private final PaymentService paymentService;
    private final OrderRepository orderRepository;
    private final PayOS payOS;
    private final WebSocketController webSocketController;
    private final UserChecker userChecker;
    private final UserRepository userRepository;

    @Value("${app.reward-to-pay-ratio}")
    private Double rewardToPayRatio;

    @PostMapping("webhook")
    @Transactional
    public ResponseEntity<String> handlePayOSNotification(@RequestBody Webhook webhookBody)
            throws PaymentViolationException {
        log.info("Enter webhook");
        try {
            WebhookData verifiedData = payOS.webhooks().verify(webhookBody);
            log.info("Webhook data: {}", verifiedData);

            if ("confirm".equalsIgnoreCase(verifiedData.getDesc()) || "VQRIO123".equals(verifiedData.getDescription())) {
                log.info("Handshake initiated. Updating webhook");
                return ResponseEntity.ok("Success");
            }

            long orderId = verifiedData.getOrderCode();
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

            User user = order.getCustomer();

            if ("00".equals(verifiedData.getCode())) {
                order.addPaidAmount(Double.valueOf(verifiedData.getAmount()));
                if (order.getPaidAmount() >= order.getTotal()) {
                    order.setState(State.CONFIRMED);

                    // send success status and order notification
                    webSocketController.sendTransactStatus(order);
                    webSocketController.sendOrderPaid(order);

                    // if the user spend more than the total, add the change to the reward
                    user.addRewardPoint(order.getPaidAmount() - order.getTotal());

                    // add reward
                    user.addRewardPoint(order.getTotal() * rewardToPayRatio);
                } else {
                    webSocketController.sendTransactStatus(order);
                }
            } else {
                order.setState(State.CANCELLED);
            }

            orderRepository.save(order);
            userRepository.save(user);

            return ResponseEntity.ok("Success");

        } catch (Exception e) {
            System.err.println("Webhook validation failed: " + e.getMessage());
            throw new PaymentViolationException("Invalid signature or processing error");
        }
    }
}
