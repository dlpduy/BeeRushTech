package com.project.bee_rushtech.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.project.bee_rushtech.models.Email;
import com.project.bee_rushtech.models.Order;
import com.project.bee_rushtech.repositories.OrderRepository;
import com.project.bee_rushtech.services.EmailService;
import com.project.bee_rushtech.services.OrderService;
import com.project.bee_rushtech.utils.SecurityUtil;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;

import java.security.Security;

import org.hibernate.annotations.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class EmailController {
    private final EmailService emailService;
    private final OrderRepository orderRepository;
    private final SecurityUtil securityUtil;

    public EmailController(EmailService emailService, OrderRepository orderRepository, SecurityUtil securityUtil) {
        this.emailService = emailService;
        this.orderRepository = orderRepository;
        this.securityUtil = securityUtil;
    }

    @PostMapping("/email")
    public ResponseEntity<Void> sendEmail(@RequestBody Email email) {
        this.emailService.sendEmail(email);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/test-email")
    public ResponseEntity<Void> testEmail(HttpServletRequest request, @RequestParam Long orderId) throws Exception {
        Order order = orderRepository.findById(orderId).get();
        this.emailService.handleSendMailStore(order);
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

}
