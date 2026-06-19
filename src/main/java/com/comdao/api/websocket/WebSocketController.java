package com.comdao.api.websocket;

import com.comdao.api.notices.NoticesRepository;
import com.comdao.api.notices.entities.Notice;
import com.comdao.api.notices.entities.enums.Type;
import com.comdao.api.notices.exceptions.NoticeCreationViolationException;
import com.comdao.api.order.entities.Order;
import com.comdao.api.order_items.entities.OrderItem;
import com.comdao.api.user.entities.User;
import com.comdao.api.user.entities.enums.Role;
import com.comdao.api.websocket.dto.GlobalNoticeCreation;
import com.comdao.api.websocket.dto.TransactionInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Date;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final NoticesRepository noticesRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/global-message")
    @SendTo("/topic/global")
    @Transactional
    public Notice sendGlobalMessage(
            @Payload GlobalNoticeCreation message,
            Principal principal
    ) throws NoticeCreationViolationException {
        if (!isAdmin(principal)) {
            log.error("User {} is not an admin to make notices",
                    ((User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUsername());
            return null;
        }

        log.info("Admin sending global message: {}", message);
        if (message.getType() == null) {
            log.error("Cannot make a global notice without a type");
            return null;
        }

        log.info("Creation dto {}", message);
        Notice newNotice = new Notice();
        newNotice.setTitle(message.getTitle());
        newNotice.setContent(message.getContent());
        newNotice.setSummary(message.getSummary());
        newNotice.setCreated(new Date());
//        newNotice.setRead(false);
        newNotice.setType(message.getType());

        System.out.println(noticesRepository.save(newNotice));

        return newNotice;
    }

//    @MessageMapping("/private-message/{username}")
//    public void sendPrivateMessage(@Payload String message, @DestinationVariable String username, Principal principal) {
//        messagingTemplate.convertAndSendToUser(
//                username,
//                "/queue/private",
//                "Hello from admin " + message
//        );
//    }


    public void sendOrderPaid(Order order) {
        Notice newNotice = new Notice();
        newNotice.setTitle("Order #" + order.getId() + " has been paid");

        StringBuilder content = new StringBuilder();
        content.append("Hello ")
                .append(order.getCustomer().getFullName())
                .append("\n")
                .append("Your order of id #")
                .append(order.getId())
                .append(" has been paid\n")
                .append("Your order consists of:\n");
        for (OrderItem orderItem : order.getOrderItems()) {
            content.append("\t- ")
                    .append(orderItem.getQuantity())
                    .append(" ")
                    .append(orderItem.getProduct().getLabel())
                    .append("\n");
        }
        content.append("Discount: ").append(order.getDiscount() * 100).append("% => Total: ").append(order.getTotal());
        newNotice.setContent(content.toString());

        newNotice.setSummary("Your order has been paid");
        newNotice.setCreated(new Date());
//        newNotice.setRead(false);
        newNotice.setUser(order.getCustomer());
        newNotice.setType(Type.ORDER);

        log.info("Sending new paid order notices to {} with id #{}",
                order.getCustomer().getFullName(), order.getId());

        messagingTemplate.convertAndSendToUser(
                order.getCustomer().getUsername(),
                "/topic/order",
                newNotice);
    }

    // called when order is short in paid
    public void sendTransactStatus(Order order) {
        User user = order.getCustomer();

        TransactionInfo info = new TransactionInfo();
        info.setIsEnough(order.getPaidAmount() >= order.getTotal());
        info.setUnpaidAmount(order.getTotal() - order.getPaidAmount());

        messagingTemplate.convertAndSendToUser(
                user.getUsername(),
                "/topic/payment",
                info
        );
    }

    private boolean isAdmin(Principal principal) {
        User user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        return user.getRole().equals(Role.ADMIN);
    }
}