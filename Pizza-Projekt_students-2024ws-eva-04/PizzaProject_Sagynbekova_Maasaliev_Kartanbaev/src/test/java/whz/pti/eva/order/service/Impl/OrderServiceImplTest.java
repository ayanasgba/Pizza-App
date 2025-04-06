package whz.pti.eva.order.service.Impl;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import whz.pti.eva.cart.domai.Cart;
import whz.pti.eva.cart.repository.CartRepository;
import whz.pti.eva.customer.domain.Customer;
import whz.pti.eva.item.domain.Item;
import whz.pti.eva.item.repository.ItemRepository;
import whz.pti.eva.order.domain.Ordered;
import whz.pti.eva.order.repository.OrderedRepository;
import whz.pti.eva.pizza.domain.Pizza;
import whz.pti.eva.pizza.domain.enums.PizzaSize;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderServiceImplTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderedRepository orderedRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ItemRepository itemRepository;

    private Cart cart;
    private Ordered order;
    private Customer customer;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("John");

        cart = new Cart();
        cart.setId(1L);
        cart.setCustomer(customer);
        cart.setSessionId("session123");
        cart.setQuantity(2);
        cart.setTotalPrice(BigDecimal.valueOf(25.00));

        Pizza pizza1 = new Pizza();
        pizza1.setId(1L);
        pizza1.setName("Margherita");
        pizza1.setPriceSmall(BigDecimal.valueOf(5.0));

        Pizza pizza2 = new Pizza();
        pizza2.setId(2L);
        pizza2.setName("Pepperoni");
        pizza2.setPriceMedium(BigDecimal.valueOf(7.5));

        Item item1 = new Item(1, pizza1, PizzaSize.SMALL);
        item1.setCart(cart);

        Item item2 = new Item(1, pizza2, PizzaSize.MEDIUM);
        item2.setCart(cart);

        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        cart.setItems(items);

        order = new Ordered();
        order.setId(1L);
        order.setUserId("1");
    }

    @Test
    @Transactional
    public void testCreateOrderFromCart() {
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(orderedRepository.save(any(Ordered.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ordered createdOrder = orderService.createOrderFromCart(1L);

        assertNotNull(createdOrder);
        assertEquals("1", createdOrder.getUserId());
        assertEquals(2, createdOrder.getNumberOfItems());
        assertEquals(BigDecimal.valueOf(25.00), createdOrder.getTotalPrice());
        verify(cartRepository, times(1)).findById(1L);
        verify(orderedRepository, times(1)).save(any(Ordered.class));
        verify(cartRepository, times(1)).save(cart);

        assertTrue(cart.getItems().isEmpty());
        assertEquals(0, cart.getQuantity());
    }

    @Test
    public void testCreateOrderFromEmptyCart() {
        cart.setItems(new ArrayList<>());

        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrderFromCart(1L);
        });

        assertEquals("Cannot create an order from an empty cart.", exception.getMessage());
        verify(cartRepository, times(1)).findById(1L);
        verify(orderedRepository, never()).save(any(Ordered.class));
    }

    @Test
    public void testGetOrderById() {
        when(orderedRepository.getById(1L)).thenReturn(order);

        Ordered foundOrder = orderService.getOrderById(1L);

        assertNotNull(foundOrder);
        assertEquals(1L, foundOrder.getId());
        verify(orderedRepository, times(1)).getById(1L);
    }

    @Test
    public void testGetAllOrdersByUserId() {
        List<Ordered> orders = List.of(order);
        when(orderedRepository.findAllByUserId("1")).thenReturn(orders);

        List<Ordered> foundOrders = orderService.getAllOrderByUserId(customer);

        assertNotNull(foundOrders);
        assertEquals(1, foundOrders.size());
        assertEquals("1", foundOrders.get(0).getUserId());
        verify(orderedRepository, times(1)).findAllByUserId("1");
    }
}
