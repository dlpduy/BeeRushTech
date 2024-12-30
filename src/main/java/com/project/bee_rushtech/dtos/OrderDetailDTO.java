package com.project.bee_rushtech.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailDTO {
    private Long orderId;

    @JsonProperty("cart_item_id")
    @Min(value = 1, message = "cartItemId >=1")
    private Long cartItemId;

    @JsonProperty("time_renting")
    private Long timeRenting;
}

// package com.project.bee_rushtech.dtos;
//
// import com.fasterxml.jackson.annotation.JsonProperty;
// import jakarta.validation.constraints.Min;
// import lombok.*;
//
// import java.time.LocalDate;
//
// @Data
// @Builder
// @Getter
// @Setter
// @AllArgsConstructor
// @NoArgsConstructor
// public class OrderDetailDTO {
// @JsonProperty("order_id")
// @Min(value = 1, message = "orderId>=1")
// private Long orderId;
//
// @JsonProperty("product_id")
// @Min(value = 1, message = "productID >=1")
// private Long productId;
//
// @Min(value = 0, message = "price >=0")
// private Float price;
//
// @JsonProperty("number_of_product")
// @Min(value = 1, message = "numPro>=1")
// private Long numberOfProduct;
//
// @JsonProperty("total_money")
// @Min(value = 0, message = "total money >=0")
// private Float totalMoney;
//
// @JsonProperty("return_date")
// private LocalDate returnDate;
// }
//
