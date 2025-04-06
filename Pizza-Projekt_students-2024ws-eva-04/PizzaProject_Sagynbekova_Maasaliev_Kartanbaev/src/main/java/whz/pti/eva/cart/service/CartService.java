package whz.pti.eva.cart.service;

import whz.pti.eva.cart.domai.Cart;

import java.util.UUID;

public interface CartService {
    Cart getOrCreateCart(String sessionId);
    Cart getCartByCustomer(Long customerId);
    void addItemToCart(Long customerId, Long pizzaId, String pizzaSize, int quantity);
    void addItemToAnonymousCart(String sessionId, Long pizzaId, String pizzaSize, int quantity);
    void removeItemFromCart(Long customerId, UUID itemId);
    void removeItemFromAnonymousCart(String sessionId, UUID itemId);
    void updateItemQuantityForCustomer(Long customerId, UUID itemId, int quantity);
    void updateItemQuantityForAnonymous(String sessionId, UUID itemId, int quantity);
    void mergeCarts(String sessionId, Long customerId);
    void clearCartForCustomer(Long customerId);
    void clearCartForAnonymous(String sessionId);
    Cart getCartById(Long id);
}