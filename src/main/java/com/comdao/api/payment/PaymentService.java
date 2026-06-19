package com.comdao.api.payment;

import com.comdao.api.order.entities.Order;
import com.comdao.api.order.entities.enums.State;
import com.comdao.api.payment.dto.PaymentInfo;
import com.comdao.api.payment.exceptions.PaymentViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.payos.PayOS;
import vn.payos.exception.PayOSException;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PayOS payOS;

    public PaymentInfo createPayment(Order order)
            throws PaymentViolationException {
        Boolean mockup = false;

        if (!mockup) {
            if (!order.getState().equals(State.PAYING))
                throw new PaymentViolationException("Cannot pay an already paid order");

//            try {
            CreatePaymentLinkRequest request = CreatePaymentLinkRequest.builder()
                    .orderCode(order.getId())
                    .amount(order.getTotal().longValue())
//                        .amount(2000L)
                    .description("THANH TOAN DON HANG " + order.getId())
                    .returnUrl("https://google.com")
                    .cancelUrl("https://youtube.com")
                    .build();

            CreatePaymentLinkResponse response = payOS.paymentRequests().create(request);
            if (response.getQrCode() == null)
                throw new PayOSException("Empty response");
            return new PaymentInfo(
                    response.getQrCode()
            );
        } else {
            return new PaymentInfo(
                    "00020101021238570010A000000727012700069704220113113366668888020899998888530370454061000005802VN62230819THANH TOAN DON HANG6304BE36"
            );
        }
    }
}
