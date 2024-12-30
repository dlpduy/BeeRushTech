package com.project.bee_rushtech.controllers;

import com.project.bee_rushtech.dtos.HandleOrderDTO;
import com.project.bee_rushtech.dtos.OrderDTO;
import com.project.bee_rushtech.responses.OrderResponse;
import com.project.bee_rushtech.services.IOrderService;
import com.project.bee_rushtech.utils.SecurityUtil;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties.Http;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/orders")
@RequiredArgsConstructor
public class OrderController {
    private final IOrderService orderService;
    private final SecurityUtil securityUtil;

    @PostMapping("")
    public ResponseEntity<?> createOrder(@RequestBody @Valid OrderDTO orderDTO, HttpServletRequest request,
            BindingResult result) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            // String token = request.getHeader("Authorization").substring(7);
            // Long userId = this.securityUtil.getUserFromToken(token).getId();
            OrderResponse orderResponse = orderService.createOrder(orderDTO, request);
            return ResponseEntity.ok(orderResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("")
    public ResponseEntity<?> getOrders(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization").substring(7);
            Long userId = this.securityUtil.getUserFromToken(token).getId();
            List<OrderResponse> orderResponses = orderService.findByUserId(userId);
            return ResponseEntity.ok(orderResponses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(@PathVariable("id") Long orderId, HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization").substring(7);
            Long userId = this.securityUtil.getUserFromToken(token).getId();

            return ResponseEntity.ok(orderService.findByOrderIdAndUserId(orderId, userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    // admin works
    public ResponseEntity<?> updateOrder(@PathVariable Long id,
            @Valid @RequestBody OrderDTO orderDTO, HttpServletRequest request) {
        try {
            OrderResponse orderResponse = orderService.updateOrder(id, orderDTO, request);
            return ResponseEntity.ok(orderResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@Valid @PathVariable Long id, HttpServletRequest request) {
        // soft delete -> active = false
        try {
            orderService.deleteOrder(id, request);
            return ResponseEntity.ok("Order deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("admin/getAll")
    public ResponseEntity<?> getAllOrders(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization").substring(7);
            String role = this.securityUtil.getUserFromToken(token).getRole();
            if (!role.equals("ADMIN")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized");
            }
            List<OrderResponse> orderResponses = orderService.findAll();
            return ResponseEntity.ok(orderResponses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("customer/handle")
    public ResponseEntity<?> handleOrderCustomer(@Valid @RequestBody HandleOrderDTO handleorderDTO,
            HttpServletRequest request) {
        try {
            orderService.handleOrderForCustomer(handleorderDTO, request);
            return ResponseEntity.ok("Order handled successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("admin/handle")
    public ResponseEntity<?> handleOrder(@Valid @RequestBody HandleOrderDTO handleorderDTO,
            HttpServletRequest request) {
        try {
            orderService.handleOrder(handleorderDTO, request);
            return ResponseEntity.ok("Order handled successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
