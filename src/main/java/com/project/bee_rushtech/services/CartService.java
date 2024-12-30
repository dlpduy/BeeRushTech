package com.project.bee_rushtech.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.javafaker.Cat;
import com.project.bee_rushtech.models.Cart;
import com.project.bee_rushtech.models.CartItem;
import com.project.bee_rushtech.models.Product;
import com.project.bee_rushtech.models.User;
import com.project.bee_rushtech.repositories.CartItemRepository;
import com.project.bee_rushtech.repositories.CartRepository;
import com.project.bee_rushtech.repositories.ProductRepository;
import com.project.bee_rushtech.utils.errors.InvalidException;

import java.util.Optional;
import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    public CartService(CartRepository cartRepository, ProductRepository productRepository,
            CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
    }

    public void createCart(User user) {
        Cart cart = new Cart();

        cart.setUser(user);

        cartRepository.save(cart);
    }

    public CartItem addProductToCart(Long cartId, Long productId, Long quantity) {
        Optional<Product> productOpt = productRepository.findById(productId);
        Optional<Cart> cartOpt = cartRepository.findById(cartId);

        if (productOpt.isEmpty() || cartOpt.isEmpty()) {
            throw new RuntimeException("Product or Cart not found!");
        }

        CartItem cartItem = this.cartItemRepository.findByProductIdAndCartId(productId, cartId);
        if (cartItem == null) {
            cartItem = new CartItem();
            cartItem.setCart(cartOpt.get());
            cartItem.setProduct(productOpt.get());
            cartItem.setQuantity(quantity);
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        }

        return this.cartItemRepository.save(cartItem);
    }

    public void updateCartItem(CartItem cartItem) throws InvalidException {
        cartItemRepository.save(cartItem);
    }

    public void removeProductFromCart(CartItem cartItem) {
        cartItemRepository.delete(cartItem);
    }

    public List<CartItem> getAllCartItems(Long cartId) {
        return cartItemRepository.findAllByCartId(cartId);
    }

    public CartItem getItemByProductIdAndCartId(Long productId, Long cartId) {
        return cartItemRepository.findByProductIdAndCartId(productId, cartId);
    }

    public boolean existsByUserId(Long userId) {
        return cartRepository.existsByUserId(userId);
    }

    public Cart getByUserId(Long userId) {
        return cartRepository.findByUserId(userId);
    }
}
