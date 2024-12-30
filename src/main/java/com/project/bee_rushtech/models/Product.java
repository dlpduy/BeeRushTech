package com.project.bee_rushtech.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 350)
    private String name;

    private Float price;
    private Float importPrice;
    @Column(name = "thumbnail", length = 300)
    private String thumbnail;
    @Column(name = "description")
    private String description;

    private String brand;
    private String color;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    private Boolean available;
    private Long quantity;
    private Long rentedQuantity;
}
