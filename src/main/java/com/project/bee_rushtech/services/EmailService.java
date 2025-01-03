package com.project.bee_rushtech.services;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.bee_rushtech.models.Email;
import com.project.bee_rushtech.models.Order;
import com.project.bee_rushtech.models.OrderDetail;
import com.project.bee_rushtech.models.User;
import com.project.bee_rushtech.utils.SecurityUtil;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderDetailService orderDetailService;

    public void sendEmail(Email email) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("beerushtech@gmail.com"); // tài khoản gửi mail
            helper.setTo(email.getToEmail()); // tài khoản nhận mail
            helper.setSubject(email.getSubject()); // tiêu đề mail
            helper.setText(email.getBody(), true); // nội dung mail
            message.setContent(email.getBody(), "text/html; charset=UTF-8");

            javaMailSender.send(message); // gửi mail
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleSendMailShip(Order order) throws Exception {
        Long userId = order.getUser().getId();
        User user = userService.findById(userId);
        String toEmail = user.getEmail();
        List<OrderDetail> orderDetails = orderDetailService.findByOrderId(order.getId());
        Float collateral = 0f;
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < orderDetails.size(); i++) {
            OrderDetail detail = orderDetails.get(i);

            Long days = detail.getTimeRenting() / 24;
            Long hours = detail.getTimeRenting() % 24;
            if (detail.getProduct().getImportPrice() < 10000000) {
                collateral += 1000000;
            } else if (detail.getProduct().getImportPrice() < 20000000) {
                collateral += 2000000;
            } else {
                collateral += 3000000;
            }

            // Định dạng chuỗi kết quả
            String timeRenting = String.format("%d Days, %d hours", days, hours);

            result.append(String.format("<p> %d. Product: %s - Quantity: %d - Time Renting: %s </p>",
                    i + 1,
                    detail.getProduct().getName(),
                    detail.getNumberOfProducts(),
                    timeRenting));
        }

        String subject = "[BeeRushTech] Order Confirmation";
        // String body = "Dear "
        // + user.getFullName() + ",\n\n"
        // + "Thank you for trusting and using our services. Your order has been
        // confirmed and is being prepared. You can review the order details below:\n\n"
        // + "INFORMATION ABOUT ORDER " + order.getId() + "\n"
        // + result.toString()
        // + "Total Price: " + order.getTotalMoney().longValue() + " VND" + "\n"
        // + "Payment Method: " + order.getPaymentMethod() + "\n"
        // + "Shipping Number: " + order.getTrackingNumber() + "\n\n"
        // + "Delivery Address: " + order.getAddress() + "\n\n"
        // + "To invoke your right to change any information, please contact us at least
        // 24 hours in advance.\n"
        // + "Best,\n" + "Customer Service at Bee RushTech\n\n"
        // + "Attention:\n"
        // + " 1. Before picking up your package, please check that it has not been
        // damaged or tampered with. If the package is damaged, and you are afraid that
        // the parcel may have been opened before it was delivered to you, remember to
        // create a ticket with the courier. Only this way can we determine the guilty
        // party and, if applicable, return your money or arrange another shipment.\n"
        // + " 2. During the rental period, if any damage occurs to the equipment, the
        // renter is responsible for covering the entire repair cost. In the event that
        // the equipment cannot be repaired, the renter must compensate for the cost of
        // purchasing the equipment. Additionally, the renter must compensate for any
        // economic loss resulting from the equipment being unavailable for rental.\n\n"
        // + "Please contact us in the following ways:\n"
        // + "Email:beerushtech@gmail.com\n"
        // + "Phone: 0123456789\n"
        // + "Showroom: 268, Ly Thuong Kiet, Ward 14, District 10, HCM City.\n";
        String body = "<html>"
                + "<body>"
                + "<p style='font-weight: bold;'>Dear " + user.getFullName() + ",</p>"
                + "<p>Thank you for trusting and using our services. Your order has been confirmed and is being prepared. You can review the order details below:</p>"
                + "<p><strong>INFORMATION ABOUT ORDER " + order.getId() + "</strong></p>"
                + "<p><strong>The rented product:" + "</strong></p>"
                + "<p>" + result.toString() + "</p>"
                + "<p><strong>Total Price: </strong>" + order.getTotalMoney().longValue() + " VND</p>"
                + "<p><strong>Payment Method: </strong>" + order.getPaymentMethod() + "</p>"
                + "<p><strong>Shipping Number: </strong>" + order.getTrackingNumber() + "</p>"
                + "<p><strong>Delivery Address: </strong>" + order.getAddress() + "</p>"
                + "<br>"
                + "<p>To invoke your right to change any information, please contact us at least 24 hours in advance.</p>"
                + "<p>Because the value of your product is too high, please provide a collateral of "
                + collateral.longValue() + " VND when you receive the item from shipper.</p>"
                + "<p>Best,<br> Customer Service at Bee RushTech</p>"
                + "<br>"
                + "<p><strong>Attention:</strong></p>"
                + "<ul>"
                + "<li style='color: red;'>Before picking up your package, please check that it has not been damaged or tampered with. If the package is damaged, and you are afraid that the parcel may have been opened before it was delivered to you, remember to create a ticket with the courier. Only this way can we determine the guilty party and, if applicable, return your money or arrange another shipment.</li>"
                + "<li style='color: red;'>During the rental period, if any damage occurs to the equipment, the renter is responsible for covering the entire repair cost. In the event that the equipment cannot be repaired, the renter must compensate for the cost of purchasing the equipment. Additionally, the renter must compensate for any economic loss resulting from the equipment being unavailable for rental.</li>"
                + "</ul>"
                + "<br>"
                + "<p>Please contact us in the following ways:</p>"
                + "<p>Email: hien.nguyenhophuoc@hcmut.edu.vn</p>"
                + "<p>Phone: 0869018053</p>"
                + "<p>Showroom: 268, Ly Thuong Kiet, Ward 14, District 10, HCM City.</p>"
                + "</body>"
                + "</html>";

        Email email = new Email();
        email.setToEmail(toEmail);
        email.setSubject(subject);
        email.setBody(body);
        sendEmail(email);
    }

    public void handleSendMailStore(Order order) throws Exception {
        Long userId = order.getUser().getId();
        User user = userService.findById(userId);
        String toEmail = user.getEmail();
        List<OrderDetail> orderDetails = orderDetailService.findByOrderId(order.getId());
        Float collateral = 0f;

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < orderDetails.size(); i++) {
            OrderDetail detail = orderDetails.get(i);

            Long days = detail.getTimeRenting() / 24;
            Long hours = detail.getTimeRenting() % 24;
            if (detail.getProduct().getImportPrice() < 10000000) {
                collateral += 1000000;
            } else if (detail.getProduct().getImportPrice() < 20000000) {
                collateral += 2000000;
            } else {
                collateral += 3000000;
            }

            // Định dạng chuỗi kết quả
            String timeRenting = String.format("%d Days, %d hours", days, hours);

            result.append(String.format("<p> %d. Product: %s - Quantity: %d - Time Renting: %s </p>",
                    i + 1,
                    detail.getProduct().getName(),
                    detail.getNumberOfProducts(),
                    timeRenting));
        }

        String subject = "[BeeRushTech] Order Confirmation";
        String body = "<html>"
                + "<body>"
                + "<p style='font-weight: bold;'>Dear " + user.getFullName() + ",</p>"
                + "<p>Thank you for trusting and using our services. Your order has been confirmed and is ready for pickup at our showroom. Please visit our store to collect your order. You can review the order details below:</p>"
                + "<p><strong>INFORMATION ABOUT ORDER " + order.getId() + "</strong></p>"
                + "<p><strong>The rented product:</strong></p>"
                + "<p>" + result.toString() + "</p>"
                + "<p><strong>Total Price: </strong>" + order.getTotalMoney().longValue() + " VND</p>"
                + "<p><strong>Pickup Address: </strong>" + "268, Ly Thuong Kiet, Ward 14, District 10, HCM City"
                + "</p>"
                + "<br>"
                + "<p>To invoke your right to change any information, please contact us at least 24 hours in advance.</p>"
                + "<p>Because the value of your product is too high, please provide a collateral of "
                + collateral.longValue() + " VND when you come to the store to pick up the item.</p>"
                + "<p>Best,<br> Customer Service at Bee RushTech</p>"
                + "<br>"
                + "<p><strong>Attention:</strong></p>"
                + "<ul>"
                + "<li style='color: red;'>Before picking up your package, please check that it has not been damaged or tampered with. If the package is damaged, and you are afraid that the parcel may have been opened before it was delivered to you, remember to create a ticket with the courier. Only this way can we determine the guilty party and, if applicable, return your money or arrange another shipment.</li>"
                + "<li style='color: red;'>During the rental period, if any damage occurs to the equipment, the renter is responsible for covering the entire repair cost. In the event that the equipment cannot be repaired, the renter must compensate for the cost of purchasing the equipment. Additionally, the renter must compensate for any economic loss resulting from the equipment being unavailable for rental.</li>"
                + "</ul>"
                + "<br>"
                + "<p>Please contact us in the following ways:</p>"
                + "<p>Email: hien.nguyenhophuoc@hcmut.edu.vn</p>"
                + "<p>Phone: 0869018053</p>"
                + "<p>Showroom: 268, Ly Thuong Kiet, Ward 14, District 10, HCM City.</p>"
                + "</body>"
                + "</html>";

        Email email = new Email();
        email.setToEmail(toEmail);
        email.setSubject(subject);
        email.setBody(body);
        sendEmail(email);
    }

    public void handleSendCancelOrder(Order order) throws Exception {
        Long userId = order.getUser().getId();
        User user = userService.findById(userId);
        String toEmail = user.getEmail();
        List<OrderDetail> orderDetails = orderDetailService.findByOrderId(order.getId());

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < orderDetails.size(); i++) {
            OrderDetail detail = orderDetails.get(i);

            Long days = detail.getTimeRenting() / 24;
            Long hours = detail.getTimeRenting() % 24;

            // Định dạng chuỗi kết quả
            String timeRenting = String.format("%d Days, %d hours", days, hours);

            result.append(String.format("<p> %d. Product: %s - Quantity: %d - Time Renting: %s </p>",
                    i + 1,
                    detail.getProduct().getName(),
                    detail.getNumberOfProducts(),
                    timeRenting));
        }

        String subject = "[BeeRushTech] Order Cancellation Confirmation";
        String body = "<html>"
                + "<body>"
                + "<p style='font-weight: bold;'>Dear " + user.getFullName() + ",</p>"
                + "<p>We have received your request to cancel your order, and we are writing to confirm that your order has been successfully cancelled. Below are the details of your cancelled order:</p>"
                + "<p><strong>ORDER ID: </strong>" + order.getId() + "</p>"
                + "<p><strong>The rented product(s):</strong></p>"
                + "<p>" + result.toString() + "</p>"
                + "<p><strong>Total Price: </strong>" + order.getTotalMoney().longValue() + " VND</p>"
                + "<p><strong>Payment Method: </strong>" + order.getPaymentMethod() + "</p>"
                + "<br>"
                + "<p>Your cancellation request has been processed successfully. If you have already made a payment, the refund will be processed to your payment method. Please allow a few business days for the refund to be completed.</p>"
                + "<p>If your cancellation is related to a rental product, you are not required to return the product. If you encounter any issues or have further questions, feel free to reach out to us.</p>"
                + "<p>Best regards,<br> Customer Service at Bee RushTech</p>"
                + "<br>"
                + "<p><strong>Attention:</strong></p>"
                + "<ul>"
                + "<li style='color: red;'>If you have any doubts about the cancellation or refund process, please contact us as soon as possible to clarify your concerns.</li>"
                + "<li style='color: red;'>If you encounter any issues with the refund process, please contact our customer service team for assistance.</li>"
                + "</ul>"
                + "<br>"
                + "<p>You can reach us through the following channels:</p>"
                + "<p>Email: hien.nguyenhophuoc@hcmut.edu.vn</p>"
                + "<p>Phone: 0869018053</p>"
                + "<p>Showroom: 268, Ly Thuong Kiet, Ward 14, District 10, HCM City.</p>"
                + "</body>"
                + "</html>";

        Email email = new Email();
        email.setToEmail(toEmail);
        email.setSubject(subject);
        email.setBody(body);
        sendEmail(email);
    }

    public void handleSendMailReturn(Order order, String methodReturn) throws Exception {
        Long userId = order.getUser().getId();
        User user = userService.findById(userId);
        String toEmail = user.getEmail();
        List<OrderDetail> orderDetails = orderDetailService.findByOrderId(order.getId());

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < orderDetails.size(); i++) {
            OrderDetail detail = orderDetails.get(i);

            Long days = detail.getTimeRenting() / 24;
            Long hours = detail.getTimeRenting() % 24;

            // Định dạng chuỗi kết quả
            String timeRenting = String.format("%d Days, %d hours", days, hours);

            result.append(String.format("<p> %d. Product: %s - Quantity: %d - Time Renting: %s </p>",
                    i + 1,
                    detail.getProduct().getName(),
                    detail.getNumberOfProducts(),
                    timeRenting));
        }

        String subject = "[BeeRushTech] Confirmation of return request";

        String body = "";
        if (methodReturn.equals("store")) {
            body = "<html>"
                    + "<body>"
                    + "<p style='font-weight: bold;'>Dear " + user.getFullName() + ",</p>"
                    + "<p>We have received your request to return the following rented product(s). We would like to confirm that we are processing your return request:</p>"
                    + "<p><strong>ORDER ID: </strong>" + order.getId() + "</p>"
                    + "<p><strong>The rented product(s):</strong></p>"
                    + "<p>" + result.toString() + "</p>"
                    + "<br>"
                    + "<p>To complete the return process, please bring the product(s) to our showroom by the due date. Kindly note that returning items after the due date may result in additional charges as per our rental policy.</p>"
                    + "<br>"
                    + "<p><strong>Return Address: 268, Ly Thuong Kiet, Ward 14, District 10, HCM City</strong></p>"
                    + "<br>"
                    + "<p>If you need any assistance or have any questions regarding the return process, please do not hesitate to contact us.</p>"
                    + "<br>"
                    + "<p>Thank you for choosing our services. We hope to continue serving you in the future!</p>"
                    + "<p>Best regards,<br> Customer Service at Bee RushTech</p>"
                    + "<br>"
                    + "<p><strong>Important Notes:</strong></p>"
                    + "<ul>"
                    + "<li style='color: red;'>Please ensure that the product(s) are returned in the same condition as when they were rented. Damaged or missing items may result in additional fees.</li>"
                    + "<li style='color: red;'>If you are unable to return the items by the due date, please contact us immediately to arrange an extension or an alternative solution.</li>"
                    + "</ul>"
                    + "<br>"
                    + "<p>You can reach us through the following channels:</p>"
                    + "<p>Email: hien.nguyenhophuoc@hcmut.edu.vn</p>"
                    + "<p>Phone: 0869018053</p>"
                    + "<p>Showroom: 268, Ly Thuong Kiet, Ward 14, District 10, HCM City</p>"
                    + "</body>"
                    + "</html>";

        } else if (methodReturn.equals("home")) {
            body = "<html>"
                    + "<body>"
                    + "<p style='font-weight: bold;'>Dear " + user.getFullName() + ",</p>"
                    + "<p>We have received your request to return the following rented product(s). We would like to confirm that we are processing your return request:</p>"
                    + "<p><strong>ORDER ID: </strong>" + order.getId() + "</p>"
                    + "<p><strong>The rented product(s):</strong></p>"
                    + "<p>" + result.toString() + "</p>"
                    + "<br>"
                    + "<p>To complete the return process, our team will come to your provided address to collect the product(s). Kindly note that any delay in returning the items may result in additional charges as per our rental policy.</p>"
                    + "<br>"
                    + "<p><strong>Return Address (Your address):</strong> " + user.getAddress() + "</p>"
                    + "<br>"
                    + "<p>If you need any assistance or have any questions regarding the return process, please do not hesitate to contact us.</p>"
                    + "<br>"
                    + "<p>Thank you for choosing our services. We hope to continue serving you in the future!</p>"
                    + "<p>Best regards,<br> Customer Service at Bee RushTech</p>"
                    + "<br>"
                    + "<p><strong>Important Notes:</strong></p>"
                    + "<ul>"
                    + "<li style='color: red;'>Please ensure that the product(s) are returned in the same condition as when they were rented. Damaged or missing items may result in additional fees.</li>"
                    + "<li style='color: red;'>If you are unable to return the items by the due date, please contact us immediately to arrange an extension or an alternative solution.</li>"
                    + "</ul>"
                    + "<br>"
                    + "<p>You can reach us through the following channels:</p>"
                    + "<p>Email: hien.nguyenhophuoc@hcmut.edu.vn</p>"
                    + "<p>Phone: 0869018053</p>"
                    + "<p>Showroom: 268, Ly Thuong Kiet, Ward 14, District 10, HCM City</p>"
                    + "</body>"
                    + "</html>";

        }

        Email email = new Email();
        email.setToEmail(toEmail);
        email.setSubject(subject);
        email.setBody(body);
        sendEmail(email);
    }
}
