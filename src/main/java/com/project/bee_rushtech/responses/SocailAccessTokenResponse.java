package com.project.bee_rushtech.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SocailAccessTokenResponse {
    private String name;
    private String email;
    private String accessToken;
}
