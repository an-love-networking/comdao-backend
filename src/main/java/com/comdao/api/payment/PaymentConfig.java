package com.comdao.api.payment;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import vn.payos.PayOS;

@Configuration
@Data
public class PaymentConfig {
    @Value("${app.api-base-url}")
    private String apiBaseUrl;

    @EventListener(ApplicationReadyEvent.class)
    public void registerWebhookOnStartup() {
        // Replace this with your current public domain or ngrok URL
        String webhookUrl = apiBaseUrl + "/api/v1/payment/webhook";

        PayOS payOS = payOS();

        try {
            System.out.println("Registering webhook URL with payOS: " + webhookUrl);

            // This registers or updates the target URL on the payOS servers
            payOS.webhooks().confirm(webhookUrl);

            System.out.println("Successfully registered webhook with payOS!");
        } catch (Exception e) {
            System.out.println("Failed to register webhook with payOS: " + e.getMessage());
        }
    }


    @Bean
    public PayOS payOS() {
        Boolean useDocker = true;

        if (useDocker) {
            return PayOS.fromEnv();
        } else {
            return null;
//            return new PayOS(
//                    "b6becb14-6001-43ed-9109-d332739b0c66",
//                    "70b6113b-26dd-47e7-a120-bc697c21d7b1",
//                    "ef9f2d0b380724e8d89f4af778e751c2d435b4aeb0c5a31deb46b7192051fa84"
//            );
        }
    }
}
