package com.project.bee_rushtech.configs;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.checkerframework.checker.units.qual.s;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class CORSConfiguration {

    private String getClientIp() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost(); // Lấy IP máy cục bộ
            return inetAddress.getHostAddress(); // Trả về địa chỉ IPv4
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "localhost"; // Mặc định nếu không thể lấy địa chỉ IP
        }
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://bee-rush-tech-fe-git-main-dlpduys-projects.vercel.app",
                "http://localhost:3000", "https://bee-rush-tech-fe.vercel.app"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
