package com.project.bee_rushtech.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.bee_rushtech.models.CartItem;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrderDTO {

    @JsonProperty("full_name")
    private String fullName;

    private String email;

    private String address;

    @JsonProperty("phone_number")
    @NotBlank(message = "Phone number is required")
    @Size(min = 5, message = "phone number >=5 characters")
    private String phoneNumber;

    private String note;

    @JsonProperty("total_money")
    @Min(value = 0, message = "Total money must be >=0")
    private Float totalMoney;

    @JsonProperty("shipping_method")
    private String shippingMethod;

    @JsonProperty("shipping_address")
    private String shippingAddress;

    @JsonProperty("shipping_date")
    private LocalDate shippingDate;

    @JsonProperty("order_method")
    private String orderMethod;

    @JsonProperty("payment_method")
    private String paymentMethod;

    @JsonProperty("list_order_detail")
    private List<OrderDetailDTO> listOrderDetail;

}
