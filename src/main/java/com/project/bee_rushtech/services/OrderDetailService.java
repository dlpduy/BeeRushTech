package com.project.bee_rushtech.services;

import com.project.bee_rushtech.dtos.OrderDetailDTO;
import com.project.bee_rushtech.models.*;
import com.project.bee_rushtech.repositories.CartItemRepository;
import com.project.bee_rushtech.repositories.CartRepository;
import com.project.bee_rushtech.utils.SecurityUtil;
import com.project.bee_rushtech.utils.errors.*;

import jakarta.servlet.http.HttpServletRequest;

import com.project.bee_rushtech.repositories.OrderDetailRepository;
import com.project.bee_rushtech.repositories.OrderRepository;
import com.project.bee_rushtech.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Security;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderDetailService implements IOrderDetailService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final CartItemRepository cartItemRepository;
    private final SecurityUtil securityUtil;
    private final CartRepository cartRepository;

    @Override
    public OrderDetail createOrderDetail(OrderDetailDTO newOrderDetail, HttpServletRequest request) throws Exception {
        String token = request.getHeader("Authorization").substring(7);
        Long userId = this.securityUtil.getUserFromToken(token).getId();
        Long cartId = cartRepository.findByUserId(userId).getId();
        CartItem cartItem = cartItemRepository
                .findById(newOrderDetail.getCartItemId())
                .orElseThrow(() -> new DataNotFoundException(
                        "Cart item not found with id " + newOrderDetail.getCartItemId()));

        if (cartItemRepository.findByIdAndCartId(cartItem.getId(), cartId) == null) {
            throw new DataNotFoundException("Your cart does not contain this item");
        }
        Order order = orderRepository
                .findById(newOrderDetail.getOrderId())
                .orElseThrow(() -> new DataNotFoundException("Order not found with id " + newOrderDetail.getOrderId()));
        Product product = productRepository
                .findById(cartItem.getProduct().getId())
                .orElseThrow(
                        () -> new DataNotFoundException("Product not found with id " + cartItem.getProduct().getId()));

        User user = order.getUser();

        OrderDetail orderDetail = OrderDetail.builder()
                .order(order)
                .product(product)
                .price(product.getPrice())
                .totalMoney(product.getPrice() * cartItem.getQuantity() * newOrderDetail.getTimeRenting())
                .numberOfProducts(cartItem.getQuantity())
                .build();
        // Xử lý khuyến mãi
        // First time buy
        Long hoursDifference = newOrderDetail.getTimeRenting();
        Long timeRenting = newOrderDetail.getTimeRenting();
        if (hoursDifference <= 0) {
            throw new InvalidException("Return date must be after current date");
        }
        if (orderRepository.countByUserId(order.getUser().getId()) == 1) {
            if (hoursDifference < 5) {
                // orderDetail.setReturnDateTime(orderDetail.getReturnDateTime().plusHours(1));
                timeRenting += 1;
            } else {
                // orderDetail.setReturnDateTime(orderDetail.getReturnDateTime().plusHours(3));
                timeRenting += 3;
            }
        } else if (orderRepository.countByUserId(order.getUser().getId()) > 1
                && hoursDifference >= 8) {
            // orderDetail.setReturnDateTime(orderDetail.getReturnDateTime().plusHours(1));
            timeRenting += 1;
        }
        if (isEduEmail(user.getEmail())) {
            if (hoursDifference < 5 * 24) {
                // orderDetail.setReturnDateTime(orderDetail
                // .getReturnDateTime()
                // .plusHours(hoursDifference / 4));
                timeRenting += hoursDifference / 4;
            } else {
                // orderDetail.setReturnDateTime(orderDetail
                // .getReturnDateTime()
                // .plusHours(2 * 24));
                timeRenting += 2 * 24;
            }
        }
        // System.out.println("Time renting: " + timeRenting);
        orderDetail.setTimeRenting(timeRenting);
        orderDetail.setReturnDateTime(null);

        // Xử lý số lượng sản phẩm
        if (product.getQuantity() - cartItem.getQuantity() < 0) {
            throw new InvalidException("Product out of stock");
        }
        product.setQuantity(product.getQuantity() - cartItem.getQuantity());

        // xử lý số lượt thuê
        product.setRentedQuantity(product.getRentedQuantity() +
                cartItem.getQuantity());
        productRepository.save(product);
        // Xóa cart item
        cartItemRepository.delete(cartItem);
        return orderDetailRepository.save(orderDetail);
    }

    @Override
    public OrderDetail getOrderDetail(Long id) throws Exception {
        return orderDetailRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Order detail not found with id " + id));
    }

    // @Override
    // public OrderDetail updateOrderDetail(Long id, OrderDetailDTO
    // newOrderDetailDTO) throws Exception {
    // Order order = orderRepository
    // .findById(newOrderDetailDTO.getOrderId())
    // .orElseThrow(() -> new DataNotFoundException("Order not found with id
    // "+newOrderDetailDTO.getOrderId()));
    // Product product =
    // productRepository.findById(newOrderDetailDTO.getProductId())
    // .orElseThrow(() -> new DataNotFoundException("Product not found with id
    // "+newOrderDetailDTO.getProductId()));
    // return orderDetailRepository.findById(id)//Hàm map này của optional
    // .map(orderDetail -> {
    // orderDetail.setPrice(newOrderDetailDTO.getPrice());
    // orderDetail.setNumberOfProducts(newOrderDetailDTO.getNumberOfProduct());
    // orderDetail.setTotalMoney(newOrderDetailDTO.getTotalMoney());
    // orderDetail.setReturnDate(newOrderDetailDTO.getReturnDate());
    // orderDetail.setOrder(order);
    // orderDetail.setProduct(product);
    // return orderDetailRepository.save(orderDetail);
    // }).orElseThrow(() -> new DataNotFoundException("Order detail not found with
    // id "+id));
    //
    // }

    @Override
    public void deleteOrderDetail(Long id) {
        orderDetailRepository.deleteById(id);
    }

    @Override
    public List<OrderDetail> findByOrderId(Long orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }

    private boolean isEduEmail(String email) {
        try {
            // Tách domain từ email (phần sau @)
            String domain = email.substring(email.indexOf("@") + 1);

            // Kiểm tra xem domain có chứa ".edu"
            return domain.contains(".edu");
        } catch (Exception e) {
            return false; // Trả về false nếu xảy ra lỗi (vd: email không hợp lệ)
        }
    }
}
