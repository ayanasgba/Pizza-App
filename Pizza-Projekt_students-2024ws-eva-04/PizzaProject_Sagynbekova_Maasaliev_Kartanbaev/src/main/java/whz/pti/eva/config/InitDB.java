package whz.pti.eva.config;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import whz.pti.eva.base.service.PaymentServiceClient;
import whz.pti.eva.customer.domain.Customer;
import whz.pti.eva.deliveryAddress.domain.DeliveryAddress;
import whz.pti.eva.pizza.domain.Pizza;
import whz.pti.eva.customer.domain.enums.Role;
import whz.pti.eva.customer.repository.CustomerRepository;
import whz.pti.eva.deliveryAddress.repository.DeliveryAddressRepository;
import whz.pti.eva.pizza.service.PizzaService;

@Configuration
public class InitDB {

    private final PaymentServiceClient paymentServiceClient;

    public InitDB(PaymentServiceClient paymentServiceClient) {
        this.paymentServiceClient = paymentServiceClient;
    }

    @Bean
    CommandLineRunner initData(PizzaService pizzaService, CustomerRepository customerRepository, DeliveryAddressRepository deliveryAddressRepository, PasswordEncoder passwordEncoder) {
        return args -> {

            Customer admin = new Customer();
            admin.setFirstName("Chingiz");
            admin.setLastName("Kartanbaev");
            admin.setLoginName("admin");
            admin.setPasswordHash(passwordEncoder.encode("a1"));
            admin.setRole(Role.ADMIN);

            DeliveryAddress adminAddress = new DeliveryAddress();
            adminAddress.setHouseNumber("123");
            adminAddress.setStreet("Admin Street");
            adminAddress.setTown("AdminTown");
            adminAddress.setPostalCode("11111");

            admin.addDeliveryAddress(adminAddress);

            customerRepository.saveAndFlush(admin);

            Customer user = new Customer();
            user.setFirstName("Aiana");
            user.setLastName("Sagynbekova");
            user.setLoginName("bnutz");
            user.setPasswordHash(passwordEncoder.encode("n1"));
            user.setRole(Role.USER);

            DeliveryAddress userAddress = new DeliveryAddress();
            userAddress.setHouseNumber("456");
            userAddress.setStreet("User Avenue");
            userAddress.setTown("UserTown");
            userAddress.setPostalCode("22222");

            user.addDeliveryAddress(userAddress);

            paymentServiceClient.createPaymentAccount(customerRepository.saveAndFlush(user).getId());


            Customer secondUser = new Customer();
            secondUser.setFirstName("Bektur");
            secondUser.setLastName("Maasaliev");
            secondUser.setLoginName("cnutz");
            secondUser.setPasswordHash(passwordEncoder.encode("n2"));
            secondUser.setRole(Role.USER);

            DeliveryAddress secondUserAddress = new DeliveryAddress();
            secondUserAddress.setHouseNumber("456");
            secondUserAddress.setStreet("User Central Avenue");
            secondUserAddress.setTown("UserCentralTown");
            secondUserAddress.setPostalCode("22222");

            secondUser.addDeliveryAddress(secondUserAddress);

            paymentServiceClient.createPaymentAccount(customerRepository.saveAndFlush(secondUser).getId());


            if (pizzaService.getAll().isEmpty()) {
            
            Pizza pepperoni = new Pizza();
            pepperoni.setName("Pepperoni");
            pepperoni.setPriceSmall(BigDecimal.valueOf(14));
            pepperoni.setPriceMedium(BigDecimal.valueOf(20));
            pepperoni.setPriceLarge(BigDecimal.valueOf(26));   
            pepperoni.setImagePath("https://png.pngtree.com/png-vector/20240203/ourmid/pngtree-pepperoni-pizza-isolated-illustration-ai-generative-png-image_11535530.png");          
            pizzaService.save(pepperoni);
            
            Pizza margherita = new Pizza();
            margherita.setName("Margherita");
            margherita.setPriceSmall(BigDecimal.valueOf(10));
            margherita.setPriceMedium(BigDecimal.valueOf(15));
            margherita.setPriceLarge(BigDecimal.valueOf(20));    
            margherita.setImagePath("https://png.pngtree.com/png-vector/20240707/ourmid/pngtree-tasty-margherita-pizza-clipart-png-image_13011624.png");      
            pizzaService.save(margherita);
            
            Pizza capricciosa = new Pizza();
            capricciosa.setName("Capricciosa");
            capricciosa.setPriceSmall(BigDecimal.valueOf(15));
            capricciosa.setPriceMedium(BigDecimal.valueOf(21));
            capricciosa.setPriceLarge(BigDecimal.valueOf(27));   
            capricciosa.setImagePath("https://png.pngtree.com/png-vector/20240517/ourmid/pngtree-capricciosa-pizza-top-view-png-image_12442199.png");    
            pizzaService.save(capricciosa);

            Pizza hawaiian = new Pizza();
            hawaiian.setName("Hawaiian");
            hawaiian.setPriceSmall(BigDecimal.valueOf(16));
            hawaiian.setPriceMedium(BigDecimal.valueOf(22));
            hawaiian.setPriceLarge(BigDecimal.valueOf(28));
            hawaiian.setImagePath("https://api.horoshiki.ru/storage/products/139/dough_thick_30/gavajskaya30420.png?ts=1602412221807115");
            pizzaService.save(hawaiian);
            }
        };
    }
}