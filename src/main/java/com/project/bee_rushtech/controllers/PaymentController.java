package com.project.bee_rushtech.controllers;

import com.project.bee_rushtech.dtos.PaymentDTO;
import com.project.bee_rushtech.responses.ResponseObject;
import com.project.bee_rushtech.services.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/payment")
@RequiredArgsConstructor

// Thông tin tài khoản dùng để test nhé
// Ngân hàng NCB
// Số thẻ 9704198526191432198
// Tên chủ thẻ NGUYEN VAN A
// Ngày phát hành 07/15
// Mật khẩu OTP 123456
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/vn-pay")
    public ResponseObject<String> pay(HttpServletRequest request) {
        return new ResponseObject<>(HttpStatus.OK, "Success",
                paymentService.createVnPayPayment(request, 1000000.2F, 1L));
    }

    @GetMapping("/vn-pay-callback")
    public ResponseEntity<?> payCallbackHandler(HttpServletRequest request) {
        try {
            String status = request.getParameter("vnp_ResponseCode");
            String redirectUrl;
            if (status.equals("00")) {
                Long orderId = Long.parseLong(request.getParameter("vnp_OrderInfo"));
                paymentService.handlePaymentOrder(orderId);
                redirectUrl = "http://localhost:3000/success";
                return ResponseEntity.status(HttpStatus.FOUND)
                        .header("Location", redirectUrl)
                        .body(null);
            } else {
                return new ResponseObject<>(HttpStatus.BAD_REQUEST, "Failed", null);
            }
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST, "This URL is not valid", null);
        }
    }
}
