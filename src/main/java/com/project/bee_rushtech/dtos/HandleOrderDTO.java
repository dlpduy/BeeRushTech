package com.project.bee_rushtech.dtos;

import lombok.Data;

@Data
public class HandleOrderDTO {
    private Long orderId;
    private String status;
    private String returnMethod;
}
