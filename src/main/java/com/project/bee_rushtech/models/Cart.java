package com.project.bee_rushtech.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "carts")
@Data
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL)
    // private List<CartItem> cartItems;

    @OneToOne
    @JoinColumn(name = "userId", referencedColumnName = "id")
    private User user;

    // Getters và Setters
}
