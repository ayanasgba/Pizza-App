package whz.pti.eva.cart.service.Impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import whz.pti.eva.cart.domai.Cart;
import whz.pti.eva.customer.domain.Customer;
import whz.pti.eva.customer.repository.CustomerRepository;
import whz.pti.eva.item.domain.Item;
import whz.pti.eva.pizza.domain.Pizza;
import whz.pti.eva.pizza.domain.enums.PizzaSize;
import whz.pti.eva.cart.repository.CartRepository;
import whz.pti.eva.item.repository.ItemRepository;
import whz.pti.eva.pizza.repository.PizzaRepository;
import whz.pti.eva.cart.service.CartService;

import java.util.Optional;
import java.util.UUID;


@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;
    private final PizzaRepository pizzaRepository;
    private final CustomerRepository customerRepository;

    public CartServiceImpl(CartRepository cartRepository, ItemRepository itemRepository, PizzaRepository pizzaRepository, CustomerRepository customerRepository) {
        this.cartRepository = cartRepository;
        this.itemRepository = itemRepository;
        this.pizzaRepository = pizzaRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional
    public Cart getOrCreateCart(String sessionId) {
        return cartRepository.findBySessionId(sessionId)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setSessionId(sessionId);
                    return cartRepository.save(cart);
                });
    }

    @Override
    @Transactional
    public Cart getCartByCustomer(Long customerId) {
        return cartRepository.findByCustomer_Id(customerId)
                .orElseGet(() -> {
                    Customer customer = customerRepository.findById(customerId)
                            .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + customerId));

                    Cart newCart = new Cart();
                    newCart.setCustomer(customer);
                    return cartRepository.save(newCart);
                });
    }

    @Override
    @Transactional
    public void addItemToCart(Long customerId, Long pizzaId, String pizzaSize, int quantity) {
        Cart cart = getCartByCustomer(customerId);
        addItem(cart, pizzaId, pizzaSize, quantity);
    }

    @Override
    @Transactional
    public void addItemToAnonymousCart(String sessionId, Long pizzaId, String pizzaSize, int quantity) {
        Cart cart = getOrCreateCart(sessionId);
        addItem(cart, pizzaId, pizzaSize, quantity);
    }

    private void addItem(Cart cart, Long pizzaId, String pizzaSize, int quantity) {
        Pizza pizza = pizzaRepository.findById(pizzaId)
                .orElseThrow(() -> new IllegalArgumentException("Pizza not found"));

        Item item = new Item(quantity, pizza, PizzaSize.valueOf(pizzaSize));
        cart.addItem(item);
        itemRepository.save(item);
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void removeItemFromCart(Long customerId, UUID itemId) {
        Cart cart = getCartByCustomer(customerId);
        removeItem(cart, itemId);
    }

    @Override
    @Transactional
    public void removeItemFromAnonymousCart(String sessionId, UUID itemId) {
        Cart cart = cartRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found for session"));
        removeItem(cart, itemId);
    }

    private void removeItem(Cart cart, UUID itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        cart.removeItem(item);
        itemRepository.delete(item);
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void updateItemQuantityForCustomer(Long customerId, UUID itemId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        Cart cart = getCartByCustomer(customerId);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found with id: " + itemId));

        if (!cart.getItems().contains(item)) {
            throw new IllegalArgumentException("Item does not belong to the customer's cart");
        }

        item.setQuantity(quantity);
        itemRepository.save(item);

        cart.setQuantity(cart.getTotalItems());
        cartRepository.save(cart);
    }


    @Override
    @Transactional
    public void updateItemQuantityForAnonymous(String sessionId, UUID itemId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        Cart cart = getOrCreateCart(sessionId);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found with id: " + itemId));

        if (!cart.getItems().contains(item)) {
            throw new IllegalArgumentException("Item does not belong to the anonymous cart");
        }

        item.setQuantity(quantity);
        itemRepository.save(item);

        cart.setQuantity(cart.getTotalItems());
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void mergeCarts(String sessionId, Long customerId) {
        Optional<Cart> anonymousCartOpt = cartRepository.findBySessionId(sessionId);
        if (anonymousCartOpt.isEmpty()) {
            return;
        }
        Cart anonymousCart = anonymousCartOpt.get();

        Cart userCart = cartRepository.findByCustomer_Id(customerId)
                .orElseGet(() -> {
                    Customer customer = customerRepository.findById(customerId)
                            .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + customerId));

                    Cart newCart = new Cart();
                    newCart.setCustomer(customer);
                    return cartRepository.save(newCart);
                });

        for (Item anonymousItem : anonymousCart.getItems()) {
            Optional<Item> existingItemOpt = userCart.getItems().stream()
                    .filter(item -> item.getPizza().getId().equals(anonymousItem.getPizza().getId()) &&
                            item.getPizzaSize().equals(anonymousItem.getPizzaSize()))
                    .findFirst();

            if (existingItemOpt.isPresent()) {
                Item existingItem = existingItemOpt.get();
                existingItem.setQuantity(existingItem.getQuantity() + anonymousItem.getQuantity());
            } else {
                userCart.addItem(anonymousItem);
            }
        }

        cartRepository.save(userCart);
        cartRepository.delete(anonymousCart);
    }

    @Override
    @Transactional
    public void clearCartForCustomer(Long customerId) {
        Cart cart = cartRepository.findByCustomer_Id(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found for customer with ID: " + customerId));
        cart.clear();
        cartRepository.save(cart);
    }


    @Override
    @Transactional
    public void clearCartForAnonymous(String sessionId) {
        Cart cart = cartRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found for session ID: " + sessionId));
        cart.clear();
        cartRepository.save(cart);
    }

    @Override
    public Cart getCartById(Long id) {
        return cartRepository.getById(id);
    }
}
