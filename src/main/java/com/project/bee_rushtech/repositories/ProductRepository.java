package com.project.bee_rushtech.repositories;

import com.project.bee_rushtech.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.*;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Product save(Product product);

    boolean existsByName(String name);

    Page<Product> findAll(Pageable pageable);// phân trang
}
