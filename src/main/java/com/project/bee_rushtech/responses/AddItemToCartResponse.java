package com.project.bee_rushtech.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddItemToCartResponse {
    private Long cartId;
    private Long productId;
    private Long quantity;

}
