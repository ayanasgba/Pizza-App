package whz.pti.eva.deliveryAddress.service.Impl;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import whz.pti.eva.deliveryAddress.domain.DeliveryAddress;
import whz.pti.eva.deliveryAddress.repository.DeliveryAddressRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class DeliveryAddressServiceImplTest {

    @InjectMocks
    private DeliveryAddressServiceImpl deliveryAddressService;

    @Mock
    private DeliveryAddressRepository deliveryAddressRepository;

    private DeliveryAddress address1;
    private DeliveryAddress address2;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        address1 = new DeliveryAddress();
        address1.setId(1L);
        address1.setStreet("Main Street");
        address1.setHouseNumber("123");
        address1.setTown("SampleTown");
        address1.setPostalCode("12345");

        address2 = new DeliveryAddress();
        address2.setId(2L);
        address2.setStreet("Second Street");
        address2.setHouseNumber("456");
        address2.setTown("AnotherTown");
        address2.setPostalCode("67890");
    }

    @Test
    public void testSaveDeliveryAddress() {
        when(deliveryAddressRepository.save(address1)).thenReturn(address1);

        DeliveryAddress savedAddress = deliveryAddressService.save(address1);

        assertNotNull(savedAddress);
        assertEquals("Main Street", savedAddress.getStreet());
        verify(deliveryAddressRepository, times(1)).save(address1);
    }

    @Test
    public void testGetAllDeliveryAddresses() {
        when(deliveryAddressRepository.findAll()).thenReturn(Arrays.asList(address1, address2));

        List<DeliveryAddress> addresses = deliveryAddressService.getAll();

        assertNotNull(addresses);
        assertEquals(2, addresses.size());
        verify(deliveryAddressRepository, times(1)).findAll();
    }

    @Test
    public void testGetDeliveryAddressById() {
        when(deliveryAddressRepository.findById(1L)).thenReturn(Optional.of(address1));

        DeliveryAddress foundAddress = deliveryAddressService.getById(1L);

        assertNotNull(foundAddress);
        assertEquals("Main Street", foundAddress.getStreet());
        verify(deliveryAddressRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetDeliveryAddressByIdNotFound() {
        when(deliveryAddressRepository.findById(1L)).thenReturn(Optional.empty());

        DeliveryAddress foundAddress = deliveryAddressService.getById(1L);

        assertNull(foundAddress);
        verify(deliveryAddressRepository, times(1)).findById(1L);
    }

    @Test
    public void testUpdateDeliveryAddress() {
        DeliveryAddress updatedDetails = new DeliveryAddress();
        updatedDetails.setStreet("Updated Street");
        updatedDetails.setHouseNumber("789");
        updatedDetails.setTown("UpdatedTown");
        updatedDetails.setPostalCode("54321");

        when(deliveryAddressRepository.findById(1L)).thenReturn(Optional.of(address1));
        when(deliveryAddressRepository.save(any(DeliveryAddress.class))).thenReturn(address1);

        DeliveryAddress updatedAddress = deliveryAddressService.update(1L, updatedDetails);

        assertNotNull(updatedAddress);
        assertEquals("Updated Street", updatedAddress.getStreet());
        verify(deliveryAddressRepository, times(1)).findById(1L);
        verify(deliveryAddressRepository, times(1)).save(any(DeliveryAddress.class));
    }

    @Test
    public void testUpdateDeliveryAddressNotFound() {
        DeliveryAddress updatedDetails = new DeliveryAddress();
        updatedDetails.setStreet("Updated Street");
        updatedDetails.setHouseNumber("789");
        updatedDetails.setTown("UpdatedTown");
        updatedDetails.setPostalCode("54321");

        when(deliveryAddressRepository.findById(1L)).thenReturn(Optional.empty());

        DeliveryAddress updatedAddress = deliveryAddressService.update(1L, updatedDetails);

        assertNull(updatedAddress);
        verify(deliveryAddressRepository, times(1)).findById(1L);
        verify(deliveryAddressRepository, never()).save(any(DeliveryAddress.class));
    }

    @Test
    public void testDeleteDeliveryAddressById() {
        when(deliveryAddressRepository.existsById(1L)).thenReturn(true);

        boolean isDeleted = deliveryAddressService.deleteById(1L);

        assertTrue(isDeleted);
        verify(deliveryAddressRepository, times(1)).existsById(1L);
        verify(deliveryAddressRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteDeliveryAddressByIdNotFound() {
        when(deliveryAddressRepository.existsById(1L)).thenReturn(false);

        boolean isDeleted = deliveryAddressService.deleteById(1L);

        assertFalse(isDeleted);
        verify(deliveryAddressRepository, times(1)).existsById(1L);
        verify(deliveryAddressRepository, never()).deleteById(anyLong());
    }
}
