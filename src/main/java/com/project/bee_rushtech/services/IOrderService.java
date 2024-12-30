package com.project.bee_rushtech.services;

import com.project.bee_rushtech.dtos.HandleOrderDTO;
import com.project.bee_rushtech.dtos.OrderDTO;
import com.project.bee_rushtech.responses.OrderResponse;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface IOrderService {
    OrderResponse createOrder(OrderDTO orderDTO, HttpServletRequest request) throws Exception;

    OrderResponse getOrder(Long orderId, HttpServletRequest request) throws Exception;

    OrderResponse updateOrder(Long id, OrderDTO orderDTO, HttpServletRequest request) throws Exception;

    void deleteOrder(Long orderId, HttpServletRequest request) throws Exception;

    List<OrderResponse> findByUserId(Long userId);

    OrderResponse findByOrderIdAndUserId(Long orderId, Long userId) throws Exception;

    boolean checkOrderOwner(Long orderId, Long userId);

    void handleOrder(HandleOrderDTO handleOrderDTO, HttpServletRequest request) throws Exception;

    void handleOrderForCustomer(HandleOrderDTO handleOrderDTO, HttpServletRequest request) throws Exception;

    List<OrderResponse> findAll();

}
