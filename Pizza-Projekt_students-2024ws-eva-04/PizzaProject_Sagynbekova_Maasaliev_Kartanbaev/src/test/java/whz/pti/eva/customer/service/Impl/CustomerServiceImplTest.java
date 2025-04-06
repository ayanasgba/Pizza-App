package whz.pti.eva.customer.service.Impl;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import whz.pti.eva.customer.domain.Customer;
import whz.pti.eva.customer.domain.enums.Role;
import whz.pti.eva.customer.domain.request.CustomerAddressRequest;
import whz.pti.eva.customer.repository.CustomerRepository;
import whz.pti.eva.deliveryAddress.domain.DeliveryAddress;
import whz.pti.eva.deliveryAddress.service.DeliveryAddressService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CustomerServiceImplTest {

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private DeliveryAddressService deliveryAddressService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private Customer customer1;
    private Customer customer2;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        customer1 = new Customer();
        customer1.setId(1L);
        customer1.setFirstName("John");
        customer1.setLastName("Doe");
        customer1.setLoginName("johndoe");
        customer1.setPasswordHash("hashedPassword");
        customer1.setRole(Role.USER);

        customer2 = new Customer();
        customer2.setId(2L);
        customer2.setFirstName("Jane");
        customer2.setLastName("Smith");
        customer2.setLoginName("janesmith");
        customer2.setPasswordHash("hashedPassword");
        customer2.setRole(Role.ADMIN);
    }

    @Test
    public void testSaveCustomer() {
        when(customerRepository.save(customer1)).thenReturn(customer1);

        Customer savedCustomer = customerService.save(customer1);

        assertNotNull(savedCustomer);
        assertEquals("John", savedCustomer.getFirstName());
        verify(customerRepository, times(1)).save(customer1);
    }

    @Test
    public void testGetAllCustomers() {
        when(customerRepository.findAll()).thenReturn(Arrays.asList(customer1, customer2));

        List<Customer> customers = customerService.getAll();

        assertNotNull(customers);
        assertEquals(2, customers.size());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    public void testGetCustomerById() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer1));

        Customer foundCustomer = customerService.getById(1L);

        assertNotNull(foundCustomer);
        assertEquals("John", foundCustomer.getFirstName());
        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetCustomerByIdNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        Customer foundCustomer = customerService.getById(1L);

        assertNull(foundCustomer);
        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    public void testUpdateCustomer() {
        Customer updatedDetails = new Customer();
        updatedDetails.setFirstName("Updated");
        updatedDetails.setLastName("Name");
        updatedDetails.setLoginName("updatedLogin");
        updatedDetails.setPasswordHash("newPassword");
        updatedDetails.setRole(Role.ADMIN);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer1));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer1);

        Customer updatedCustomer = customerService.update(1L, updatedDetails);

        assertNotNull(updatedCustomer);
        assertEquals("Updated", updatedCustomer.getFirstName());
        verify(customerRepository, times(1)).findById(1L);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    public void testDeleteCustomerById() {
        when(customerRepository.existsById(1L)).thenReturn(true);

        boolean isDeleted = customerService.deleteById(1L);

        assertTrue(isDeleted);
        verify(customerRepository, times(1)).existsById(1L);
        verify(customerRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteCustomerByIdNotFound() {
        when(customerRepository.existsById(1L)).thenReturn(false);

        boolean isDeleted = customerService.deleteById(1L);

        assertFalse(isDeleted);
        verify(customerRepository, times(1)).existsById(1L);
        verify(customerRepository, never()).deleteById(anyLong());
    }

    @Test
    public void testCreateCustomer() {
        CustomerAddressRequest request = new CustomerAddressRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setLoginName("johndoe");
        request.setPasswordHash("password");
        request.setStreet("Main Street");
        request.setHouseNumber("123");
        request.setTown("SampleTown");
        request.setPostalCode("12345");

        DeliveryAddress deliveryAddress = new DeliveryAddress();
        deliveryAddress.setStreet(request.getStreet());
        deliveryAddress.setHouseNumber(request.getHouseNumber());
        deliveryAddress.setTown(request.getTown());
        deliveryAddress.setPostalCode(request.getPostalCode());

        Customer expectedCustomer = new Customer();
        expectedCustomer.setFirstName("John");
        expectedCustomer.setLastName("Doe");
        expectedCustomer.setLoginName("johndoe");
        expectedCustomer.setPasswordHash("encodedPassword");
        expectedCustomer.setRole(Role.USER);
        expectedCustomer.addDeliveryAddress(deliveryAddress);

        when(deliveryAddressService.save(any(DeliveryAddress.class))).thenReturn(deliveryAddress);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(customerRepository.save(any(Customer.class))).thenReturn(expectedCustomer);

        Customer createdCustomer = customerService.create(request);

        assertNotNull(createdCustomer);
        assertEquals("John", createdCustomer.getFirstName());
        assertEquals("encodedPassword", createdCustomer.getPasswordHash());
        verify(deliveryAddressService, times(1)).save(any(DeliveryAddress.class));
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    public void testFindCustomerByLoginName() {
        when(customerRepository.findByLoginName("johndoe")).thenReturn(Optional.of(customer1));

        Optional<Customer> foundCustomer = customerService.findByLoginName("johndoe");

        assertTrue(foundCustomer.isPresent());
        assertEquals("John", foundCustomer.get().getFirstName());
        verify(customerRepository, times(1)).findByLoginName("johndoe");
    }
}
