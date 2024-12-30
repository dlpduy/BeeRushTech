package com.project.bee_rushtech.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import java.util.Properties;

@Configuration
public class MailConfiguration {
    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com"); // dịch vụ mail server
        mailSender.setPort(587); // cổng dịch vụ mail server
        mailSender.setUsername("beerushtech@gmail.com"); // tài khoản gửi mail 
        mailSender.setPassword("yeqyvrparljqvhvr"); // app mật khẩu tài khoản gửi mail
        
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp"); // giao thức truyền tải
        props.put("mail.smtp.auth", "true"); // xác thực
        props.put("mail.smtp.starttls.enable", "true"); // bắt đầu gửi mail
        props.put("mail.debug", "true"); 
        return mailSender;
    }
}