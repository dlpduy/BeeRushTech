
package com.project.bee_rushtech.services;

import com.project.bee_rushtech.dtos.ProductDTO;
import com.project.bee_rushtech.dtos.ProductImageDTO;
import com.project.bee_rushtech.models.Category;
import com.project.bee_rushtech.models.Product;
import com.project.bee_rushtech.models.ProductImage;
import com.project.bee_rushtech.repositories.CategoryRepository;
import com.project.bee_rushtech.repositories.ProductImageRepository;
import com.project.bee_rushtech.repositories.ProductRepository;
import com.project.bee_rushtech.responses.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.project.bee_rushtech.utils.errors.*;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;

    @Override
    public Product createProduct(ProductDTO productDTO) throws DataNotFoundException {
        Category existingCategory = categoryRepository
                .findById(productDTO.getCategoryId())
                .orElseThrow(() -> new DataNotFoundException(
                        "Cannot find category with id: " + productDTO.getCategoryId()));

        Product newProduct = Product.builder()
                .name(productDTO.getName())
                .brand(productDTO.getBrand())
                .color(productDTO.getColor())
                .price(productDTO.getPrice())
                .description(productDTO.getDescription())
                .importPrice(productDTO.getImportPrice())
                .thumbnail(productDTO.getThumbnail())
                .category(existingCategory)
                .available(true)
                .quantity(productDTO.getQuantity())
                .rentedQuantity(0L)
                .build();
        return productRepository.save(newProduct);
    }

    @Override
    public Product getProductById(long productId) throws Exception {
        return productRepository.findById(productId).orElseThrow(() -> new DataNotFoundException(
                "Cannot find product with id =" + productId));
    }

    @Override
    public Page<ProductResponse> getAllProducts(PageRequest pageRequest) {
        // Lấy danh sách sản phẩm theo trang(page) và giới hạn(limit)
        return productRepository.findAll(pageRequest).map(product -> {
            ProductResponse productResponse = ProductResponse.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .brand(product.getBrand())
                    .color(product.getColor())
                    .description(product.getDescription())
                    .price(product.getPrice())
                    .importPrice(product.getImportPrice())
                    .thumbnail(product.getThumbnail())
                    .category(product.getCategory())
                    .available(product.getAvailable())
                    .quantity(product.getQuantity())
                    .rentedQuantity(product.getRentedQuantity())
                    .build();
            productResponse.setCreatedAt(product.getCreatedAt());
            productResponse.setUpdatedAt(product.getUpdatedAt());
            return productResponse;
        });
    }

    @Override
    public Product updateProduct(
            long id,
            ProductDTO productDTO)
            throws Exception {
        Product existingProduct = getProductById(id);
        if (existingProduct != null) {
            // copy các thuộc tính từ DTO -> Product
            // Có thể sử dụng ModelMapper
            Category existingCategory = categoryRepository
                    .findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new DataNotFoundException(
                            "Cannot find category with id: " + productDTO.getCategoryId()));
            existingProduct.setName(productDTO.getName());
            existingProduct.setCategory(existingCategory);
            existingProduct.setPrice(productDTO.getPrice());
            existingProduct.setDescription(productDTO.getDescription());
            existingProduct.setThumbnail(productDTO.getThumbnail());
            existingProduct.setBrand(productDTO.getBrand());
            existingProduct.setColor(productDTO.getColor());
            existingProduct.setQuantity(productDTO.getQuantity());
            existingProduct.setImportPrice(productDTO.getImportPrice());
            existingProduct.setAvailable(productDTO.getAvailable());
            existingProduct.setQuantity(productDTO.getQuantity());
            return productRepository.save(existingProduct);
        }
        return null;
    }

    @Override
    public void deleteProduct(long id) {
        productRepository.deleteById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return productRepository.existsByName(name);
    }

    @Override
    public ProductImage createProductImage(ProductImageDTO productImageDTO) throws Exception {
        Product existingProduct = productRepository
                .findById(productImageDTO.getProductId())
                .orElseThrow(() -> new DataNotFoundException(
                        "Cannot find product with id: " + productImageDTO.getProductId()));
        ProductImage newProductImage = ProductImage.builder()
                .product(existingProduct)
                .imageUrl(productImageDTO.getImageUrl())
                .build();
        return productImageRepository.save(newProductImage);
    }

    @Override
    public List<ProductImage> getProductImagesByProductId(long productId) {
        return productImageRepository.findByProductId(productId);
    }
}
