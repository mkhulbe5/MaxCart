package com.myStartup.services.cartService;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.myStartup.exceptions.ResourceNotFoundException;
import com.myStartup.model.Cart;
import com.myStartup.model.CartItem;
import com.myStartup.model.Product;
import com.myStartup.repository.CartItemRepository;
import com.myStartup.repository.CartRepository;
import com.myStartup.services.productService.IProductService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {
	private final CartItemRepository cartItemRepository;
	private final CartRepository cartRepository;
	private final IProductService productService;
	private final CartService cartService;

	@Override
	public void addItemToCart(Long cartId, Long productId, int quantity) {
		// 1. Get the cart
		// 2. Get the product
		// 3. Check if the product already in the cart
		// 4. If Yes, then increase the quantity with the requested quantity
		// 5. If No, then initiate a new CartItem entry.
		Cart cart = cartService.getCart(cartId);
		Product product = productService.getProductById(productId);
		CartItem cartItem = cart.getItems().stream().filter(item -> item.getProduct().getId().equals(productId))
				.findFirst().orElse(new CartItem());
		if (cartItem.getId() == null) {
			cartItem.setCart(cart);
			cartItem.setProduct(product);
			cartItem.setQuantity(quantity);
			cartItem.setUnitPrice(product.getPrice());
		} else {
			cartItem.setQuantity(cartItem.getQuantity() + quantity);
		}
		cartItem.setTotalPrice();
		cart.addItem(cartItem);
		cartItemRepository.save(cartItem);
		cartRepository.save(cart);
	}

	@Override
	public void removeItemFromCart(Long cartId, Long productId) {
		Cart cart = cartService.getCart(cartId);
		CartItem itemToRemove = getCartItem(cartId, productId);
		cart.removeItem(itemToRemove);
		cartRepository.save(cart);
	}

	@Override
	public void updateItemQuantity(Long cartId, Long productId, int quantity) {
		Cart cart = cartService.getCart(cartId);
		cart.getItems().stream().filter(item -> item.getProduct().getId().equals(productId)).findFirst()
				.ifPresent(item -> {
					item.setQuantity(quantity);
					item.setUnitPrice(item.getProduct().getPrice());
					item.setTotalPrice();
				});
		BigDecimal totalAmount = cart.getItems().stream().map(CartItem::getTotalPrice).reduce(BigDecimal.ZERO,
				BigDecimal::add);

		cart.setTotalAmount(totalAmount);
		cartRepository.save(cart);
	}

	@Override
	public CartItem getCartItem(Long cartId, Long productId) {
		Cart cart = cartService.getCart(cartId);
		return cart.getItems().stream().filter(item -> item.getProduct().getId().equals(productId)).findFirst()
				.orElseThrow(() -> new ResourceNotFoundException("Item not found"));
	}
}