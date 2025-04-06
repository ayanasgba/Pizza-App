package whz.pti.eva.cart.service.Impl;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import whz.pti.eva.cart.domai.Cart;
import whz.pti.eva.cart.repository.CartRepository;
import whz.pti.eva.customer.domain.Customer;
import whz.pti.eva.customer.repository.CustomerRepository;
import whz.pti.eva.item.domain.Item;
import whz.pti.eva.item.repository.ItemRepository;
import whz.pti.eva.pizza.domain.Pizza;
import whz.pti.eva.pizza.domain.enums.PizzaSize;
import whz.pti.eva.pizza.repository.PizzaRepository;

import java.math.BigDecimal;
import java.util.*;

public class CartServiceImplTest {

    @InjectMocks
    private CartServiceImpl cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private PizzaRepository pizzaRepository;

    @Mock
    private CustomerRepository customerRepository;

    private Cart userCart;
    private Cart anonymousCart;
    private Customer customer;
    private Pizza pizza;
    private Item userItem;
    private Item anonymousItem;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("John");

        userCart = new Cart();
        userCart.setId(1L);
        userCart.setCustomer(customer);
        userCart.setSessionId("session123");

        pizza = new Pizza();
        pizza.setId(1L);
        pizza.setName("Margherita");
        pizza.setPriceSmall(BigDecimal.valueOf(5.0));

        userItem = new Item(1, pizza, PizzaSize.SMALL);
        userItem.setId(UUID.randomUUID());
        userCart.addItem(userItem);

        anonymousCart = new Cart();
        anonymousCart.setSessionId("session1234");

        anonymousItem = new Item(2, pizza, PizzaSize.SMALL);
        anonymousItem.setId(UUID.randomUUID());
        anonymousCart.addItem(anonymousItem);
    }

    @Test
    public void testGetOrCreateCartWhenCartExists() {
        when(cartRepository.findBySessionId("session123")).thenReturn(Optional.of(userCart));

        Cart foundCart = cartService.getOrCreateCart("session123");

        assertNotNull(foundCart);
        assertEquals("session123", foundCart.getSessionId());
        verify(cartRepository, times(1)).findBySessionId("session123");
    }

    @Test
    public void testGetOrCreateCartWhenCartDoesNotExist() {
        when(cartRepository.findBySessionId("session123")).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(userCart);

        Cart createdCart = cartService.getOrCreateCart("session123");

        assertNotNull(createdCart);
        assertEquals("session123", createdCart.getSessionId());
        verify(cartRepository, times(1)).findBySessionId("session123");
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    public void testGetCartByCustomerWhenCartExists() {
        when(cartRepository.findByCustomer_Id(1L)).thenReturn(Optional.of(userCart));

        Cart foundCart = cartService.getCartByCustomer(1L);

        assertNotNull(foundCart);
        assertEquals(1L, foundCart.getCustomer().getId());
        verify(cartRepository, times(1)).findByCustomer_Id(1L);
    }

    @Test
    public void testGetCartByCustomerWhenCartDoesNotExist() {
        when(cartRepository.findByCustomer_Id(1L)).thenReturn(Optional.empty());
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(cartRepository.save(any(Cart.class))).thenReturn(userCart);

        Cart createdCart = cartService.getCartByCustomer(1L);

        assertNotNull(createdCart);
        assertEquals(1L, createdCart.getCustomer().getId());
        verify(cartRepository, times(1)).findByCustomer_Id(1L);
        verify(customerRepository, times(1)).findById(1L);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    public void testAddItemToCart() {
        when(cartRepository.findByCustomer_Id(1L)).thenReturn(Optional.of(userCart));
        when(pizzaRepository.findById(1L)).thenReturn(Optional.of(pizza));
        when(itemRepository.save(any(Item.class))).thenReturn(userItem);

        cartService.addItemToCart(1L, 1L, "SMALL", 2);

        verify(cartRepository, times(1)).findByCustomer_Id(1L);
        verify(pizzaRepository, times(1)).findById(1L);
        verify(itemRepository, times(1)).save(any(Item.class));
        verify(cartRepository, times(1)).save(userCart);
    }

    @Test
    public void testRemoveItemFromCart() {
        when(cartRepository.findByCustomer_Id(1L)).thenReturn(Optional.of(userCart));
        when(itemRepository.findById(userItem.getId())).thenReturn(Optional.of(userItem));

        cartService.removeItemFromCart(1L, userItem.getId());

        verify(cartRepository, times(1)).findByCustomer_Id(1L);
        verify(itemRepository, times(1)).findById(userItem.getId());
        verify(itemRepository, times(1)).delete(userItem);
        verify(cartRepository, times(1)).save(userCart);
    }

    @Test
    public void testUpdateItemQuantityForCustomer() {
        when(cartRepository.findByCustomer_Id(1L)).thenReturn(Optional.of(userCart));
        when(itemRepository.findById(userItem.getId())).thenReturn(Optional.of(userItem));

        cartService.updateItemQuantityForCustomer(1L, userItem.getId(), 3);

        assertEquals(3, userItem.getQuantity());
        verify(cartRepository, times(1)).findByCustomer_Id(1L);
        verify(itemRepository, times(1)).findById(userItem.getId());
        verify(itemRepository, times(1)).save(userItem);
        verify(cartRepository, times(1)).save(userCart);
    }

    @Test
    public void testClearCartForCustomer() {
        when(cartRepository.findByCustomer_Id(1L)).thenReturn(Optional.of(userCart));

        cartService.clearCartForCustomer(1L);

        assertTrue(userCart.getItems().isEmpty());
        verify(cartRepository, times(1)).findByCustomer_Id(1L);
        verify(cartRepository, times(1)).save(userCart);
    }

    @Test
    public void testMergeCarts() {
        when(cartRepository.findBySessionId("session1234")).thenReturn(Optional.of(anonymousCart));
        when(cartRepository.findByCustomer_Id(1L)).thenReturn(Optional.of(userCart));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        cartService.mergeCarts("session1234", 1L);

        assertEquals(1, userCart.getItems().size());
        Item mergedItem = userCart.getItems().stream()
                .filter(item -> item.getPizza().getId().equals(pizza.getId()))
                .findFirst()
                .orElse(null);

        assertNotNull(mergedItem);
        assertEquals(3, mergedItem.getQuantity());

        verify(cartRepository, times(1)).findBySessionId("session1234");
        verify(cartRepository, times(1)).findByCustomer_Id(1L);
        verify(cartRepository, times(1)).save(userCart);
        verify(cartRepository, times(1)).delete(anonymousCart);
    }
}
