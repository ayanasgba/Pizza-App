package whz.pti.eva.deliveryAddress.boundary;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import whz.pti.eva.customer.domain.CurrentUser;
import whz.pti.eva.customer.domain.Customer;
import whz.pti.eva.customer.domain.enums.Role;
import whz.pti.eva.customer.service.CustomerService;
import whz.pti.eva.deliveryAddress.domain.DeliveryAddress;
import whz.pti.eva.deliveryAddress.service.DeliveryAddressService;
import whz.pti.eva.config.TestSecurityConfig;

@ExtendWith(SpringExtension.class)
@WebMvcTest(DeliveryAddressController.class)
@Import(TestSecurityConfig.class)
public class DeliveryAddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeliveryAddressService deliveryAddressService;

    @MockBean
    private CustomerService customerService;

    @Mock
    private Customer customer;

    private CurrentUser currentUser;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("Test");
        customer.setLastName("User");
        customer.setLoginName("testuser");
        customer.setPasswordHash("password");
        customer.setRole(Role.USER);

        currentUser = new CurrentUser(customer);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(currentUser, null, currentUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void testAddAddress() throws Exception {
        DeliveryAddress deliveryAddress = new DeliveryAddress();
        deliveryAddress.setStreet("Test Street");
        deliveryAddress.setHouseNumber("123");
        deliveryAddress.setTown("Test Town");
        deliveryAddress.setPostalCode("12345");

        when(customerService.getById(1L)).thenReturn(customer);
        when(deliveryAddressService.save(any(DeliveryAddress.class))).thenReturn(deliveryAddress);

        mockMvc.perform(post("/address/add")
                        .param("street", "Test Street")
                        .param("houseNumber", "123")
                        .param("town", "Test Town")
                        .param("postalCode", "12345"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/customers/edit/1"));

        verify(deliveryAddressService, times(1)).save(any(DeliveryAddress.class));
        verify(customerService, times(1)).getById(1L);
        verify(customerService, times(1)).save(customer);
    }
}
