package whz.pti.eva.order.boundary;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

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

import whz.pti.eva.cart.service.CartService;
import whz.pti.eva.order.domain.Ordered;
import whz.pti.eva.order.service.OrderService;
import whz.pti.eva.config.TestSecurityConfig;

import java.math.BigDecimal;
import java.util.List;

@ExtendWith(SpringExtension.class)
@WebMvcTest(OrderController.class)
@Import(TestSecurityConfig.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private CartService cartService;

    private Ordered order;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        order = new Ordered();
        order.setId(1L);
        order.setTotalPrice(BigDecimal.valueOf(100.0));
        order.setNumberOfItems(5);

        when(orderService.getOrderById(1L)).thenReturn(order);
        when(orderService.getAllOrderByUserId(any())).thenReturn(List.of(order));
    }

    @Test
    public void testShowOrderDetails() throws Exception {
        mockMvc.perform(get("/orders/details/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("order/order_details"))
                .andExpect(model().attributeExists("order"))
                .andExpect(model().attribute("order", order))
                .andDo(print());
        verify(orderService, times(1)).getOrderById(1L);
    }

  

    @Test
    public void testCreateOrderFromCart() throws Exception {
        when(orderService.createOrderFromCart(1L)).thenReturn(order);

        mockMvc.perform(post("/orders/create")
                        .param("cartId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders/details/1"))
                .andDo(print());
        verify(orderService, times(1)).createOrderFromCart(1L);
    }
}
