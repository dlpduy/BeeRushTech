package com.project.bee_rushtech.services;

import com.project.bee_rushtech.dtos.HandleOrderDTO;
import com.project.bee_rushtech.dtos.OrderDTO;
import com.project.bee_rushtech.dtos.OrderDetailDTO;
import com.project.bee_rushtech.utils.SecurityUtil;
import com.project.bee_rushtech.utils.errors.DataNotFoundException;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;

import com.project.bee_rushtech.models.Order;
import com.project.bee_rushtech.models.OrderDetail;
import com.project.bee_rushtech.models.OrderStatus;
import com.project.bee_rushtech.models.User;
import com.project.bee_rushtech.repositories.CartItemRepository;
import com.project.bee_rushtech.repositories.OrderDetailRepository;
import com.project.bee_rushtech.repositories.OrderRepository;
import com.project.bee_rushtech.repositories.UserRepository;
import com.project.bee_rushtech.responses.OrderResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {
        private final UserRepository userRepository;
        private final OrderRepository orderRepository;
        private final ModelMapper modelMapper;
        private final OrderDetailService orderDetailService;
        private final CartItemRepository cartItemRepository;
        private final PaymentService paymentService;
        private final SecurityUtil securityUtil;
        private final OrderDetailRepository orderDetailRepository;
        private final EmailService emailService;

        @Override
        public OrderResponse createOrder(OrderDTO orderDTO, HttpServletRequest request) throws Exception {
                String token = request.getHeader("Authorization").substring(7);
                Long userId = securityUtil.getUserFromToken(token).getId();
                User user = userRepository
                                .findById(userId)
                                .orElseThrow(() -> new DataNotFoundException(
                                                "User not found"));
                modelMapper.typeMap(OrderDTO.class, Order.class)
                                .addMappings(mapper -> mapper.skip(Order::setId));
                Order order = new Order();
                order = modelMapper.map(orderDTO, Order.class);
                order.setUser(user);
                order.setFullName(orderDTO.getFullName());
                order.setOrderDate(new Date());
                order.setStatus(OrderStatus.PENDING);
                order.setOrderMethod(orderDTO.getOrderMethod());
                order.setAddress(orderDTO.getAddress());
                // default 5 ngày kể từ ngày hiện tại
                // LocalDate shippingDate = orderDTO.getShippingDate() == null ?
                // LocalDate.now().plusDays(5)
                // : orderDTO.getShippingDate();
                // if (shippingDate.isBefore(LocalDate.now())) {
                // throw new DateTimeException("Invalid shipping date");
                // }
                order.setActive(true);

                order.setTrackingNumber(Order.generateTrackingNumber());

                List<OrderDetailDTO> orderDetailDTOS = orderDTO.getListOrderDetail();
                for (OrderDetailDTO orderDetailDTO : orderDetailDTOS) {
                        cartItemRepository.findById(orderDetailDTO.getCartItemId())
                                        .orElseThrow(() -> new DataNotFoundException(
                                                        "Cart item not found with id "
                                                                        + orderDetailDTO.getCartItemId()));
                }
                orderRepository.save(order);
                Float totalMoney = 0f;
                for (OrderDetailDTO orderDetailDTO : orderDetailDTOS) {
                        orderDetailDTO.setOrderId(order.getId());
                        OrderDetail orderDetail = orderDetailService.createOrderDetail(orderDetailDTO, request);
                        totalMoney += orderDetail.getTotalMoney();
                }
                order.setTotalMoney(totalMoney);
                if (orderDTO.getOrderMethod().equals("home")) {
                        order.setShippingMethod("GHN");

                } else if (orderDTO.getOrderMethod().equals("store")) {
                        order.setShippingMethod(null);
                }

                if (orderDTO.getPaymentMethod().equals("COD")) {
                        order.setPaymentMethod("COD");
                        order.setShippingDate(LocalDate.now().plusDays(1));

                } else if (orderDTO.getPaymentMethod().equals("CREDIT")) {
                        order.setStatus(OrderStatus.PENDING);
                        String paymentUrl = paymentService.createVnPayPayment(request, totalMoney, order.getId());
                        order.setPaymentUrl(paymentUrl);
                }
                orderRepository.save(order);

                return OrderResponse.fromOrder(order);
        }

        @Override
        public OrderResponse getOrder(Long orderId, HttpServletRequest request) throws Exception {
                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new DataNotFoundException("Order not found with id " + orderId));
                return OrderResponse.fromOrder(order);
        }

        @Override
        public OrderResponse updateOrder(Long id, OrderDTO orderDTO, HttpServletRequest request) throws Exception {
                String token = request.getHeader("Authorization").substring(7);
                Long userId = securityUtil.getUserFromToken(token).getId();
                if (!checkOrderOwner(id, userId)) {
                        throw new RuntimeException("You are not the owner of this order");
                }

                Order order = orderRepository.findById(id)
                                .orElseThrow(() -> new DataNotFoundException("Order not found with id " + id));
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new DataNotFoundException(
                                                "User not found"));
                modelMapper.typeMap(OrderDTO.class, Order.class)
                                .addMappings(mapper -> mapper.skip(Order::setId));
                modelMapper.map(orderDTO, order);
                LocalDate shippingDate = orderDTO.getShippingDate() == null ? LocalDate.now().plusDays(5)
                                : orderDTO.getShippingDate();
                order.setUser(user);
                order.setShippingDate(shippingDate);
                orderRepository.save(order);
                return OrderResponse.fromOrder(order);
        }

        @Override
        public void deleteOrder(Long orderId, HttpServletRequest request) throws Exception {
                // soft delete
                String token = request.getHeader("Authorization").substring(7);
                Long userId = securityUtil.getUserFromToken(token).getId();
                if (!checkOrderOwner(orderId, userId)) {
                        throw new RuntimeException("You are not the owner of this order");
                }
                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new DataNotFoundException("Order not found with id " + orderId));
                order.setActive(false);
                orderRepository.save(order);
        }

        @Override
        public List<OrderResponse> findByUserId(Long userId) {
                List<Order> orders = orderRepository.findByUserId(userId);
                List<OrderResponse> orderResponses = orders
                                .stream()
                                .map(OrderResponse::fromOrder)
                                .collect(Collectors.toList());
                // model mapper phai match all fields, neu co 1 filed khong match thi phai skip,
                // khong skip thi gia tri tra ve se bi null het
                return orderResponses;

        }

        @Override
        public OrderResponse findByOrderIdAndUserId(Long orderId, Long userId) throws Exception {
                Order order = orderRepository.findByIdAndUserId(orderId, userId);
                if (order == null) {
                        throw new DataNotFoundException("Order not found with id " + orderId);
                }
                return OrderResponse.fromOrder(order);
        }

        @Override
        public boolean checkOrderOwner(Long orderId, Long userId) {
                Order order = orderRepository.findByIdAndUserId(orderId, userId);
                return order != null;
        }

        @Override
        public void handleOrder(HandleOrderDTO handleOrderDTO, HttpServletRequest request) throws Exception {
                String token = request.getHeader("Authorization").substring(7);
                String role = securityUtil.getUserFromToken(token).getRole();
                if (!role.equals("ADMIN")) {
                        throw new RuntimeException("You are not authorized to do this action");
                }

                Order order = orderRepository.findById(handleOrderDTO.getOrderId())
                                .orElseThrow(() -> new DataNotFoundException(
                                                "Order not found with id " + handleOrderDTO.getOrderId()));

                order.setStatus(handleOrderDTO.getStatus());

                if (handleOrderDTO.getStatus().equals(OrderStatus.CONFIRMED)) {
                        order.setShippingDate(LocalDate.now());
                        order.setTrackingNumber(Order.generateTrackingNumber());
                        order.setShippingMethod("GHN");
                        emailService.handleSendMailShip(order);
                } else if (handleOrderDTO.getStatus().equals(OrderStatus.RECEIVE)) {
                        List<OrderDetail> orderDetails = orderDetailService.findByOrderId(order.getId());
                        for (OrderDetail orderDetail : orderDetails) {
                                orderDetail.setReturnDateTime(
                                                LocalDateTime.now().plusHours(orderDetail.getTimeRenting()));
                                orderDetailRepository.save(orderDetail);
                        }
                }

                orderRepository.save(order);
        }

        public void handleOrderForCustomer(HandleOrderDTO handleOrderDTO, HttpServletRequest request) throws Exception {
                String token = request.getHeader("Authorization").substring(7);
                Long userId = securityUtil.getUserFromToken(token).getId();
                if (!checkOrderOwner(handleOrderDTO.getOrderId(), userId)) {
                        throw new RuntimeException("You are not the owner of this order");
                }

                Order order = orderRepository.findById(handleOrderDTO.getOrderId())
                                .orElseThrow(() -> new DataNotFoundException(
                                                "Order not found with id " + handleOrderDTO.getOrderId()));

                order.setStatus(handleOrderDTO.getStatus());

                if (handleOrderDTO.getStatus().equals(OrderStatus.CANCELLED)) {
                        emailService.handleSendCancelOrder(order);
                } else if (handleOrderDTO.getStatus().equals(OrderStatus.RETURN)) {
                        if (handleOrderDTO.getReturnMethod().equals("store")) {
                                emailService.handleSendMailReturn(order, "store");
                        } else if (handleOrderDTO.getReturnMethod().equals("home")) {
                                emailService.handleSendMailReturn(order, "home");
                        }

                } else {
                        throw new RuntimeException("You are not authorized to do this action");
                }
                orderRepository.save(order);
        }

        @Override
        public List<OrderResponse> findAll() {
                List<Order> orders = orderRepository.findAllByOrderByIdDesc();
                List<OrderResponse> orderResponses = orders
                                .stream()
                                .map(OrderResponse::fromOrder)
                                .collect(Collectors.toList());
                return orderResponses;
        }

}
