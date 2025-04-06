package whz.pti.eva.cart.boundary;

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
import whz.pti.eva.config.TestSecurityConfig;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CartController.class)
@Import(TestSecurityConfig.class)
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @MockBean
    private SessionService sessionService;

    private Cart testCart;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Настройка тестовой корзины
        testCart = new Cart();
        testCart.setId(1L);
        testCart.setQuantity(2);
        testCart.setTotalPrice(BigDecimal.valueOf(25.0));
    }

    
  

    @Test
    public void testRemoveItemFromCart() throws Exception {
        UUID itemId = UUID.randomUUID();

        mockMvc.perform(post("/cart/remove")
                        .param("itemId", itemId.toString())
                        .param("customerId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));

        verify(cartService, times(1)).removeItemFromCart(eq(1L), eq(itemId));
    }

    @Test
    public void testClearCartForCustomer() throws Exception {
        mockMvc.perform(post("/cart/clear")
                        .param("customerId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));

        verify(cartService, times(1)).clearCartForCustomer(eq(1L));
    }

    @Test
    public void testCheckoutAsUnauthenticatedUser() throws Exception {
        mockMvc.perform(post("/cart/checkout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }
}
