package com.project.bee_rushtech.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="product_images")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter

public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_url", length = 300)
    private String imageUrl;
    @ManyToOne
    @JoinColumn(name="product_id")
    private Product product;
}
