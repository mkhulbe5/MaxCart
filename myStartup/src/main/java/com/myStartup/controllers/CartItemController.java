package com.myStartup.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.myStartup.exceptions.ResourceNotFoundException;
import com.myStartup.requests.ApiResponse;
import com.myStartup.services.cartService.CartItemService;
import com.myStartup.services.cartService.CartService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cartItem")
public class CartItemController {
	private final CartItemService cartItemService;
	private final CartService cartService;

	@PostMapping("/item/add")
	public ResponseEntity<ApiResponse> addItemToCart(@RequestParam(required = false) Long cartId,
			@RequestParam Long productId, @RequestParam Integer quantity) {
		try {
			if (cartId == null) {
				cartId = cartService.initializeNewCart();
			}
			cartItemService.addItemToCart(cartId, productId, quantity);
			return ResponseEntity.ok(new ApiResponse("Add Item Success", null));
		} catch (ResourceNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
		}
	}

	@DeleteMapping("/cart/{cartId}/item/{itemId}/remove")
	public ResponseEntity<ApiResponse> removeItemFromCart(@PathVariable Long cartId, @PathVariable Long itemId) {
		try {
			cartItemService.removeItemFromCart(cartId, itemId);
			return ResponseEntity.ok(new ApiResponse("Remove Item Success", null));
		} catch (ResourceNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
		}
	}

	@PutMapping("/cart/{cartId}/item/{itemId}/update")
	public ResponseEntity<ApiResponse> updateItemQuantity(@PathVariable Long cartId, @PathVariable Long itemId,
			@RequestParam Integer quantity) {
		try {
			cartItemService.updateItemQuantity(cartId, itemId, quantity);
			return ResponseEntity.ok(new ApiResponse("Update Item Success", null));
		} catch (ResourceNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
		}

	}
}
