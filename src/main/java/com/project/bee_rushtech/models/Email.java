package com.project.bee_rushtech.models;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Email {
    @NotBlank(message = "Email is required")
    private String toEmail;
    @NotBlank(message = "Subject is required")
    private String subject;
    @NotBlank(message = "Body is required")
    private String body;
}
