package whz.pti.eva.pizza.service.Impl;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import whz.pti.eva.pizza.domain.Pizza;
import whz.pti.eva.pizza.repository.PizzaRepository;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class PizzaServiceImplTest {

    @InjectMocks
    private PizzaServiceImpl pizzaService;

    @Mock
    private PizzaRepository pizzaRepository;

    private Pizza pizza1;
    private Pizza pizza2;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        pizza1 = new Pizza();
        pizza1.setId(1L);
        pizza1.setName("Margherita");
        pizza1.setPriceSmall(BigDecimal.valueOf(5.0));
        pizza1.setPriceMedium(BigDecimal.valueOf(7.0));
        pizza1.setPriceLarge(BigDecimal.valueOf(9.0));

        pizza2 = new Pizza();
        pizza2.setId(2L);
        pizza2.setName("Pepperoni");
        pizza2.setPriceSmall(BigDecimal.valueOf(6.0));
        pizza2.setPriceMedium(BigDecimal.valueOf(8.0));
        pizza2.setPriceLarge(BigDecimal.valueOf(10.0));
    }

    @Test
    public void testSavePizza() {
        when(pizzaRepository.save(pizza1)).thenReturn(pizza1);

        Pizza savedPizza = pizzaService.save(pizza1);

        assertNotNull(savedPizza);
        assertEquals("Margherita", savedPizza.getName());
        verify(pizzaRepository, times(1)).save(pizza1);
    }

    @Test
    public void testGetAllPizzas() {
        when(pizzaRepository.findAll()).thenReturn(Arrays.asList(pizza1, pizza2));

        List<Pizza> pizzas = pizzaService.getAll();

        assertNotNull(pizzas);
        assertEquals(2, pizzas.size());
        verify(pizzaRepository, times(1)).findAll();
    }

    @Test
    public void testGetPizzaById() {
        when(pizzaRepository.findById(1L)).thenReturn(Optional.of(pizza1));

        Pizza foundPizza = pizzaService.getById(1L);

        assertNotNull(foundPizza);
        assertEquals("Margherita", foundPizza.getName());
        verify(pizzaRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetPizzaByIdNotFound() {
        when(pizzaRepository.findById(1L)).thenReturn(Optional.empty());

        Pizza foundPizza = pizzaService.getById(1L);

        assertNull(foundPizza);
        verify(pizzaRepository, times(1)).findById(1L);
    }

    @Test
    public void testUpdatePizza() {
        Pizza updatedDetails = new Pizza();
        updatedDetails.setName("Veggie");
        updatedDetails.setPriceSmall(BigDecimal.valueOf(6.0));
        updatedDetails.setPriceMedium(BigDecimal.valueOf(8.0));
        updatedDetails.setPriceLarge(BigDecimal.valueOf(10.0));

        when(pizzaRepository.findById(1L)).thenReturn(Optional.of(pizza1));
        when(pizzaRepository.save(any(Pizza.class))).thenReturn(pizza1);

        Pizza updatedPizza = pizzaService.update(1L, updatedDetails);

        assertNotNull(updatedPizza);
        assertEquals("Veggie", updatedPizza.getName());
        verify(pizzaRepository, times(1)).findById(1L);
        verify(pizzaRepository, times(1)).save(any(Pizza.class));
    }

    @Test
    public void testUpdatePizzaNotFound() {
        Pizza updatedDetails = new Pizza();
        updatedDetails.setName("Veggie");
        updatedDetails.setPriceSmall(BigDecimal.valueOf(6.0));
        updatedDetails.setPriceMedium(BigDecimal.valueOf(8.0));
        updatedDetails.setPriceLarge(BigDecimal.valueOf(10.0));

        when(pizzaRepository.findById(1L)).thenReturn(Optional.empty());

        Pizza updatedPizza = pizzaService.update(1L, updatedDetails);

        assertNull(updatedPizza);
        verify(pizzaRepository, times(1)).findById(1L);
        verify(pizzaRepository, never()).save(any(Pizza.class));
    }

    @Test
    public void testDeletePizzaById() {
        when(pizzaRepository.existsById(1L)).thenReturn(true);

        boolean isDeleted = pizzaService.deleteById(1L);

        assertTrue(isDeleted);
        verify(pizzaRepository, times(1)).existsById(1L);
        verify(pizzaRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeletePizzaByIdNotFound() {
        when(pizzaRepository.existsById(1L)).thenReturn(false);

        boolean isDeleted = pizzaService.deleteById(1L);

        assertFalse(isDeleted);
        verify(pizzaRepository, times(1)).existsById(1L);
        verify(pizzaRepository, never()).deleteById(anyLong());
    }
}
