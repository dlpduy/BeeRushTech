package com.project.bee_rushtech.services;

import java.util.Map;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public record GoogleService() {
    private static final String GOOGLE_USERINFO_ENDPOINT = "https://oauth2.googleapis.com/tokeninfo?id_token=";

    public static Map<String, Object> getUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String url = GOOGLE_USERINFO_ENDPOINT + accessToken;
        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new IllegalArgumentException("Invalid ID Token");
        }
    }

}
