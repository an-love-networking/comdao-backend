package com.comdao.api.email;

import com.comdao.api.order.entities.Order;
import com.comdao.api.order_items.entities.OrderItem;
import com.comdao.api.user.entities.User;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class InvoiceGenerator {
    private final DecimalFormat currencyFormat = new DecimalFormat("#,000");
    private String template = """
            <!DOCTYPE html>
                   <html>
                   <head>
                       <meta charset="UTF-8">
                       <title>Invoice #%d</title>
                   </head>
                   <body style="margin:0; padding:20px; background-color:#f4f4f4; font-family: Arial, Helvetica, sans-serif;">
                       <table width="100%%" cellpadding="0" cellspacing="0" border="0">
                           <tr><td align="center">
                               <table width="600" cellpadding="0" cellspacing="0" border="0" style="background-color:#ffffff; border-radius:8px;">
                                   <!-- Header -->
                                   <tr>
                                       <td style="padding:30px 30px 20px 30px; border-bottom:2px solid #eeeeee;">
                                           <table width="100%%" cellpadding="0" cellspacing="0">
                                               <tr>
                                                   <td style="font-size:28px; font-weight:bold; color:#333333;">INVOICE</td>
                                                   <td align="right" style="font-size:14px; color:#777777;">
                                                       #%d<br>%s
                                                   </td>
                                               </tr>
                                           </table>
                                       </td>
                                   </tr>
                                   <!-- Company & Customer -->
                                   <tr>
                                       <td style="padding:20px 30px;">
                                           <table width="100%%" cellpadding="0" cellspacing="0">
                                               <tr>
                                                   <td width="50%%" style="vertical-align:top;">
                                                       <strong>From:</strong><br>
                                                       Comdao Business Corp.<br>
                                                       153, No. 7, Binh Tri Dong, Binh Tan, Ho Chi Minh City, Vietnam<br>
                                                       dont.email.here@not.exist.com<br>
                                                       +94 62691394
                                                   </td>
                                                   <td width="50%%" style="vertical-align:top;">
                                                       <strong>Bill To:</strong><br>
                                                       Full Name: %s<br>
                                                       Address: %s<br>
                                                       Email: %s
                                                   </td>
                                               </tr>
                                           </table>
                                       </td>
                                   </tr>
                                   <!-- Line Items -->
                                   <tr>
                                       <td style="padding:0 30px;">
                                           <table width="100%%" cellpadding="8" cellspacing="0" style="border-collapse:collapse;">
                                               <thead>
                                                   <tr style="background-color:#f8f8f8; border-bottom:1px solid #dddddd;">
                                                       <th align="left">Item</th>
                                                       <th align="right">Qty</th>
                                                       <th align="right">Unit Price</th>
                                                       <th align="right">Total</th>
                                                   </tr>
                                               </thead>
                                               <tbody>
                                                   %s
                                               </tbody>
                                               <tfoot>
                                                   <tr style="border-top:2px solid #dddddd;">
                                                       <td colspan="3" align="right"><strong>Subtotal</strong></td>
                                                       <td align="right">%s</td>
                                                   </tr>
                                                   <tr>
                                                       <td colspan="3" align="right">Discount%s</td>
                                                       <td align="right">%s</td>
                                                   </tr>
                                                   <tr>
                                                       <td colspan="3" align="right">Tax (0%%)</td>
                                                       <td align="right">%s</td>
                                                   </tr>
                                                   <tr style="background-color:#f9f9f9;">
                                                       <td colspan="3" align="right" style="font-size:18px; font-weight:bold;">Total Due</td>
                                                       <td align="right" style="font-size:18px; font-weight:bold;">%s</td>
                                                   </tr>
                                               </tfoot>
                                           </table>
                                       </td>
                                   </tr>
                                   <!-- Payment -->
                                   <tr>
                                       <td style="padding:20px 30px; background-color:#fafafa; border-top:1px solid #eeeeee;">
                                           <strong>If you want to help us</strong><br>
                                           Please donate to:<br>
                                           Bank: MBBank<br>
                                           Account: 0963326814<br>
                                           <span style="font-size:13px; color:#666;">Include the invoice number in your payment reference.</span>
                                       </td>
                                   </tr>
                                   <!-- Footer -->
                                   <tr>
                                       <td style="padding:20px 30px; text-align:center; font-size:12px; color:#999999;">
                                           Thank you for your business!<br>
                                           Questions? Contact dont.email.here@not.exist.com
                                       </td>
                                   </tr>
                               </table>
                           </td></tr>
                       </table>
                   </body>
                   </html> 
            """;

    public String generate(Order order) {
        User customer = order.getCustomer();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        // Build items rows
        StringBuilder itemsRows = new StringBuilder();
        for (OrderItem item : order.getOrderItems()) {
            double itemTotal = item.getQuantity() * item.getProduct().getPrice(); // assuming Product has price
            itemsRows.append("""
                    <tr style="border-bottom:1px solid #eeeeee;">
                        <td style="padding:10px 8px;">%s</td>
                        <td align="right" style="padding:10px 8px;">%d</td>
                        <td align="right" style="padding:10px 8px;">%s</td>
                        <td align="right" style="padding:10px 8px;">%s</td>
                    </tr>
                    """.formatted(
                    item.getProduct().getLabel(),
                    item.getQuantity(),
                    currencyFormat.format(item.getProduct().getPrice()),
                    currencyFormat.format(itemTotal)
            ));
        }

        // Format totals
        String subtotalStr = currencyFormat.format(order.getSubtotal());
        String discountStr = currencyFormat.format(order.getDiscount());
        double taxAmount = 0.0; // no tax field in Order, adjust if needed
        String taxAmountStr = currencyFormat.format(taxAmount);
        String totalStr = currencyFormat.format(order.getTotal());

        String discountPercent = "";
        if (order.getDiscount() != null && order.getSubtotal() != null && order.getSubtotal() > 0) {
            double percent = (order.getDiscount() / order.getSubtotal()) * 100;
            discountPercent = String.format(" (%.0f%%)", percent);
        }

        return template.formatted(
                order.getId(),
                order.getId(),
                order.getCreated() != null ? dateFormatter.format(order.getCreated()) : "",
                customer.getFullName(),
                customer.getAddress() != null ? customer.getAddress() : "",
                customer.getEmail(),
                itemsRows.toString(),
                subtotalStr,
                discountPercent,
                discountStr,
                taxAmountStr,
                totalStr,
                order.getId()
        );
    }
}
