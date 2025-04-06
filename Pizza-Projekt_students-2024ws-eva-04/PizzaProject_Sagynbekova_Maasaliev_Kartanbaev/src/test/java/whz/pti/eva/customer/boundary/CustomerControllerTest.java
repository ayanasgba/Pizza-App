package whz.pti.eva.customer.boundary;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import whz.pti.eva.customer.domain.Customer;
import whz.pti.eva.customer.domain.request.CustomerAddressRequest;
import whz.pti.eva.customer.service.CustomerService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private UserDetailsService userDetailsService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        Customer customer1 = new Customer();
        customer1.setId(1L);
        customer1.setFirstName("John");
        customer1.setLastName("Doe");
        customer1.setLoginName("johndoe");

        Customer customer2 = new Customer();
        customer2.setId(2L);
        customer2.setFirstName("Jane");
        customer2.setLastName("Smith");
        customer2.setLoginName("janesmith");

        when(customerService.getAll()).thenReturn(List.of(customer1, customer2));
        when(customerService.getById(1L)).thenReturn(customer1);
        when(customerService.getById(2L)).thenReturn(customer2);
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testShowCreateForm() throws Exception {
        mockMvc.perform(get("/customers/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("customer/create_customer"))
                .andExpect(model().attributeExists("customer"))
                .andDo(print());
    }

    

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testGetAllCustomers() throws Exception {
        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(view().name("customer/list_customers"))
                .andExpect(model().attributeExists("customers"))
                .andDo(print());
        verify(customerService, times(1)).getAll();
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testShowEditForm() throws Exception {
        mockMvc.perform(get("/customers/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("customer/edit_customer"))
                .andExpect(model().attributeExists("customer"))
                .andDo(print());
        verify(customerService, times(1)).getById(1L);
    }

    

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testDeleteCustomer() throws Exception {
        mockMvc.perform(get("/customers/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/customers"))
                .andDo(print());
        verify(customerService, times(1)).deleteById(1L);
    }
}
