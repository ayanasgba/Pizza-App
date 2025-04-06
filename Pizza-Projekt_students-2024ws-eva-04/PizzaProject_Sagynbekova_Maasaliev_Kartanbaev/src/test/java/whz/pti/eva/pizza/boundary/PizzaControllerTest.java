package whz.pti.eva.pizza.boundary;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import whz.pti.eva.base.service.SessionService;
import whz.pti.eva.cart.domai.Cart;
import whz.pti.eva.cart.service.CartService;
import whz.pti.eva.pizza.domain.Pizza;
import whz.pti.eva.pizza.service.PizzaService;
import whz.pti.eva.config.TestSecurityConfig;

@ExtendWith(SpringExtension.class)
@WebMvcTest(PizzaController.class)
@Import(TestSecurityConfig.class)
public class PizzaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PizzaService pizzaService;

    @MockBean
    private CartService cartService;

    @MockBean
    private SessionService sessionService;

    private Pizza pizza;
    private Cart cart;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        pizza = new Pizza();
        pizza.setId(1L);
        pizza.setName("Margherita");
        pizza.setPriceSmall(BigDecimal.valueOf(5.0));
        pizza.setPriceMedium(BigDecimal.valueOf(7.0));
        pizza.setPriceLarge(BigDecimal.valueOf(9.0));
        pizza.setImagePath("uploads/margherita.jpg");

        when(pizzaService.getAll()).thenReturn(List.of(pizza));
        when(pizzaService.getById(1L)).thenReturn(pizza);

        cart = new Cart();
        cart.setId(1L);
        cart.setQuantity(1);
        cart.setTotalPrice(BigDecimal.valueOf(5.0));
        cart.setItems(List.of());

        when(cartService.getCartByCustomer(anyLong())).thenReturn(cart);
        when(cartService.getOrCreateCart(anyString())).thenReturn(cart);
    }
    

    @Test
    public void testShowEditForm() throws Exception {
        mockMvc.perform(get("/pizzas/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("pizza/edit_pizza"))
                .andExpect(model().attributeExists("pizza"))
                .andDo(print());
        verify(pizzaService, times(1)).getById(1L);
    }

    @Test
    public void testUpdatePizza() throws Exception {
        mockMvc.perform(post("/pizzas/edit/1")
                        .param("name", "Updated Pizza")
                        .param("priceSmall", "6.0")
                        .param("priceMedium", "8.0")
                        .param("priceLarge", "10.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pizzas"))
                .andDo(print());
        verify(pizzaService, times(1)).update(eq(1L), any(Pizza.class));
    }

    @Test
    public void testDeletePizza() throws Exception {
        mockMvc.perform(get("/pizzas/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pizzas"))
                .andDo(print());
        verify(pizzaService, times(1)).deleteById(1L);
    }

    @Test 
    public void testShowCreateForm() throws Exception { 
        mockMvc.perform(get("/pizzas/create")) 
                .andExpect(status().isOk()) 
                .andExpect(view().name("pizza/create_pizza")) 
                .andExpect(model().attributeExists("pizza")) 
                .andDo(print()); 
    }
}
