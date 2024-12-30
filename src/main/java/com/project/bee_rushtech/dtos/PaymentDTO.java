package com.project.bee_rushtech.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;

public abstract class PaymentDTO {
    @Builder
    @AllArgsConstructor
    public static class VNPayResponse {
        public String code;
        public String message;
    }
}
