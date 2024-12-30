package com.project.bee_rushtech.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.project.bee_rushtech.models.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {

    boolean existsByUserId(Long userId);

    Cart save(Cart cart);

    Cart findByUserId(Long userId);
}
