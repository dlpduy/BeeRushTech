package com.project.bee_rushtech.models;

import jakarta.persistence.*;
import lombok.*;

//Xác định nó là thực thể
@Entity

@Table(name="categories")
@Data //toString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="name", nullable = false)
    private String name;

}
