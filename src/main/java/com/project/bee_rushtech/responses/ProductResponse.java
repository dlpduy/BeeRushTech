package com.project.bee_rushtech.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.bee_rushtech.models.Category;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse extends BaseResponse {
    private Long id;
    private String name;
    private Float price;
    @JsonProperty("import_price")
    private Float importPrice;
    private String thumbnail;
    private String description;
    private String brand;
    private Category category;
    private Boolean available;
    private String color;
    private Long quantity;
    @JsonProperty("rented_quantity")
    private Long rentedQuantity;
}
