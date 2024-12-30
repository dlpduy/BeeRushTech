package com.project.bee_rushtech.services;

import com.project.bee_rushtech.dtos.OrderDetailDTO;
import com.project.bee_rushtech.models.OrderDetail;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface IOrderDetailService {
    OrderDetail createOrderDetail(OrderDetailDTO newOrderDetail, HttpServletRequest request) throws Exception;

    OrderDetail getOrderDetail(Long id) throws Exception;

    // OrderDetail updateOrderDetail(Long id, OrderDetailDTO newOrderDetailDTO)
    // throws Exception;
    void deleteOrderDetail(Long id);

    List<OrderDetail> findByOrderId(Long orderId);
}
