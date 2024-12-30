package com.project.bee_rushtech.services;

import com.project.bee_rushtech.configs.VNPAYConfig;
import com.project.bee_rushtech.dtos.PaymentDTO;
import com.project.bee_rushtech.models.Order;
import com.project.bee_rushtech.models.OrderStatus;
import com.project.bee_rushtech.repositories.OrderRepository;
import com.project.bee_rushtech.utils.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final VNPAYConfig vnPayConfig;
    private final OrderRepository orderRepository;

    public String createVnPayPayment(HttpServletRequest request, Float amount, Long orderId) {
        // change float to long
        Long amountLong = amount.longValue() * 100L;
        String bankCode = "VNPAY";
        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig();
        vnpParamsMap.put("vnp_Amount", String.valueOf(amountLong));
        vnpParamsMap.put("vnp_OrderInfo", String.valueOf(orderId));
        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParamsMap.put("vnp_BankCode", bankCode);
        }
        vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));
        // build query url
        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;
        return paymentUrl;
    }

    public void handlePaymentOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id " + orderId));
        order.setStatus(OrderStatus.PAID);
        order.setPaymentUrl("");
        orderRepository.save(order);
    }
}
