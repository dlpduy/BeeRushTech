package com.project.bee_rushtech.models;

import jakarta.persistence.*;
import lombok.*;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Date;

//Xác định nó là thực thể
@Entity
// Bảng trong db là categories mà class chúng ta lại là Category
// --> dùng @Table để ánh xạ
@Table(name = "orders")
@Data // toString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order {
    @Id // primary key
    // No same instance --> when add a new instance --> auto increment
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "phone_number", nullable = false, length = 100)
    private String phoneNumber;

    @Column(name = "shipping_address", nullable = false, length = 100)
    private String shippingAddress;

    @Column(name = "note", length = 100)
    private String note;

    @Column(name = "order_date")
    private Date orderDate;

    @Column(name = "status")
    private String status;

    @Column(name = "total_money")
    private Float totalMoney;

    @Column(name = "address")
    private String address;

    @Column(name = "shipping_method")
    private String shippingMethod;

    @Column(name = "shipping_date")
    private LocalDate shippingDate;

    @Column(name = "tracking_number")
    private String trackingNumber;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "active")
    private Boolean active;// thuộc về admin

    @Column(name = "order_method")
    private String orderMethod;

    @Column(name = "payment_url")
    private String paymentUrl;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int LENGTH = 20;

    public static String generateTrackingNumber() {
        SecureRandom random = new SecureRandom();
        StringBuilder trackingNumber = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            trackingNumber.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return trackingNumber.toString();
    }

}
