package com.project.bee_rushtech.responses;

import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.bee_rushtech.models.OrderDetail;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderDetailResponse {
    private Long id;

    @JsonProperty("order_id")
    private Long orderId;

    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("price")
    private Float price;

    @JsonProperty("number_of_products")
    private Long numberOfProducts;

    @JsonProperty("total_money")
    private Float totalMoney;

    private Long timeRenting;

    @JsonProperty("return_date")
    private String returnDateTime;

    public static OrderDetailResponse fromOrderDetail(OrderDetail orderDetail) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return OrderDetailResponse.builder()
                .id(orderDetail.getId())
                .orderId(orderDetail.getOrder().getId())
                .productId(orderDetail.getProduct().getId())
                .price(orderDetail.getPrice())
                .numberOfProducts(orderDetail.getNumberOfProducts())
                .totalMoney(orderDetail.getTotalMoney())
                .timeRenting(orderDetail.getTimeRenting())
                .returnDateTime(
                        orderDetail.getReturnDateTime() != null ? orderDetail.getReturnDateTime().format(formatter)
                                : null)
                .build();
    }
}
