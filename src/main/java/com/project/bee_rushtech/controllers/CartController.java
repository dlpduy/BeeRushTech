package com.project.bee_rushtech.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.bee_rushtech.dtos.CartItemDTO;
import com.project.bee_rushtech.models.Cart;
import com.project.bee_rushtech.models.CartItem;
import com.project.bee_rushtech.models.User;
import com.project.bee_rushtech.responses.AddItemToCartResponse;
import com.project.bee_rushtech.responses.CartItemResponse;
import com.project.bee_rushtech.services.CartService;
import com.project.bee_rushtech.services.UserService;
import com.project.bee_rushtech.utils.SecurityUtil;
import com.project.bee_rushtech.utils.annotation.ApiMessage;
import com.project.bee_rushtech.utils.errors.InvalidException;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("${api.prefix}/customer")
public class CartController {

    private final CartService cartService;
    private final SecurityUtil securityUtil;
    private final UserService userService;

    public CartController(CartService cartService, SecurityUtil securityUtil, UserService userService) {
        this.cartService = cartService;
        this.securityUtil = securityUtil;
        this.userService = userService;
    }

    @PostMapping("/cart")
    public ResponseEntity<AddItemToCartResponse> addProductToCart(@RequestBody CartItemDTO cartItemDTO,
            HttpServletRequest request) throws InvalidException {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        Long userId = this.securityUtil.getUserFromToken(token).getId();
        if (!this.cartService.existsByUserId(userId)) {
            User user = this.userService.findById(userId);
            this.cartService.createCart(user);
        }
        Cart currenCart = this.cartService.getByUserId(userId);
        if (currenCart == null) {
            throw new InvalidException("Cart not found!");
        }
        Long cartId = currenCart.getId();
        this.cartService.addProductToCart(cartId, cartItemDTO.getProductId(),
                cartItemDTO.getQuantity());

        AddItemToCartResponse res = new AddItemToCartResponse(cartId, cartItemDTO.getProductId(),
                cartItemDTO.getQuantity());
        return ResponseEntity.ok(res);
    }

    @GetMapping("/cart")
    public ResponseEntity<List<CartItemResponse>> getAllCarts(HttpServletRequest request)
            throws InvalidException {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        Long userId = this.securityUtil.getUserFromToken(token).getId();
        Cart geCart = cartService.getByUserId(userId);
        List<CartItemResponse> cartResponse = new ArrayList<>();
        if (geCart == null) {
            return ResponseEntity.status(HttpStatus.OK).body(cartResponse);
        }
        Long CartId = geCart.getId();
        List<CartItem> cart = cartService.getAllCartItems(CartId);

        for (CartItem item : cart) {
            CartItemResponse res = new CartItemResponse();
            res.setId(item.getId());
            res.setProductId(item.getProduct().getId());
            res.setQuantity(item.getQuantity());
            res.setPriceProduct(item.getProduct().getPrice());
            res.setName(item.getProduct().getName());
            cartResponse.add(res);
        }

        return ResponseEntity.status(HttpStatus.OK).body(cartResponse);
    }

    @PutMapping("/cart")
    public ResponseEntity<CartItemResponse> updateCartItem(HttpServletRequest request,
            @RequestBody CartItemDTO cartItemDTO) throws InvalidException {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        Long userId = this.securityUtil.getUserFromToken(token).getId();
        Cart cart = cartService.getByUserId(userId);
        if (cart == null) {
            throw new InvalidException("Your cart is empty!");
        }
        Long CartId = cart.getId();
        CartItem cartItem = this.cartService.getItemByProductIdAndCartId(cartItemDTO.getProductId(), CartId);
        if (cartItem == null) {
            throw new InvalidException("Item not found!");
        }
        cartItem.setQuantity(cartItemDTO.getQuantity());
        this.cartService.updateCartItem(cartItem);
        CartItemResponse cartResponse = new CartItemResponse();
        cartResponse.setId(cartItem.getId());
        cartResponse.setProductId(cartItem.getProduct().getId());
        cartResponse.setQuantity(cartItemDTO.getQuantity());
        cartResponse.setName(cartItem.getProduct().getName());
        return ResponseEntity.ok(cartResponse);
    }

    @DeleteMapping("/cart")
    @ApiMessage("Delete product from cart successfully")
    public ResponseEntity<Void> removeProductFromCart(HttpServletRequest request,
            @RequestParam Long productId) throws InvalidException {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        Long userId = this.securityUtil.getUserFromToken(token).getId();
        Cart cart = cartService.getByUserId(userId);
        if (cart == null) {
            throw new InvalidException("Your cart is empty!");
        }
        Long CartId = cart.getId();
        CartItem cartItem = this.cartService.getItemByProductIdAndCartId(productId, CartId);
        if (cartItem == null) {
            throw new InvalidException("Item not found!");
        }
        this.cartService.removeProductFromCart(cartItem);
        return ResponseEntity.ok().build();
    }
}
