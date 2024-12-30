package com.project.bee_rushtech.services;

import com.project.bee_rushtech.dtos.ProductDTO;
import com.project.bee_rushtech.dtos.ProductImageDTO;
import com.project.bee_rushtech.responses.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.project.bee_rushtech.models.*;

import java.util.List;

public interface IProductService {
    Product createProduct(ProductDTO productDTO) throws Exception;
    Product getProductById(long id) throws Exception;
    Page<ProductResponse> getAllProducts(PageRequest pageRequest);
    Product updateProduct(long id, ProductDTO productDTO) throws Exception;
    void deleteProduct(long id);
    boolean existsByName(String name);
    ProductImage createProductImage(ProductImageDTO productImageDTO) throws Exception;
    List<ProductImage> getProductImagesByProductId(long productId);
}
