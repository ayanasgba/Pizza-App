package whz.pti.eva.customer.boundary;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import whz.pti.eva.base.service.PaymentServiceClient;
import whz.pti.eva.customer.domain.Account;
import whz.pti.eva.customer.domain.CurrentUser;
import whz.pti.eva.customer.domain.Customer;
import whz.pti.eva.customer.domain.request.CustomerAddressRequest;
import whz.pti.eva.customer.service.CustomerService;
import whz.pti.eva.deliveryAddress.domain.DeliveryAddress;

@Controller
@RequestMapping("/customers")
public class CustomerController {
    
    @Autowired
    private CustomerService customerService;

    @Autowired
    private PaymentServiceClient paymentService;


    private void addCurrentUserToModel(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof CurrentUser) {
            CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
            model.addAttribute("currentUser", currentUser);
        } else {
            model.addAttribute("currentUser", null);
        }
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        addCurrentUserToModel(model);
        model.addAttribute("customer", new CustomerAddressRequest());
        return "customer/create_customer";
    }

    @PostMapping("/create")
    public String createCustomer(@ModelAttribute CustomerAddressRequest customerAddressRequest) {
        customerService.create(customerAddressRequest);
        return "redirect:/customers";
    }

    @GetMapping
    public String getAllCustomers(Model model) {
        addCurrentUserToModel(model);
        List<Customer> customers = customerService.getAll();
        model.addAttribute("customers", customers);
        return "customer/list_customers";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(Model model, @PathVariable("id") Long id) {
        addCurrentUserToModel(model);
        Customer customer = customerService.getById(id);
        if (customer != null) {
            model.addAttribute("customer", customer);
            model.addAttribute("deliveryAddresses", new DeliveryAddress());
            return "customer/edit_customer";
        } else {
            return "redirect:/customers";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateCustomer(@PathVariable("id") Long id, @ModelAttribute("customer") Customer customer, BigDecimal amount) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CurrentUser currentUser) {
            customerService.update(id, customer);
            if (amount != null) {

                paymentService.addFundsToAccount(id, amount);

            }
            return "redirect:/customers/edit/" + currentUser.getId();
        }
        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable("id") Long id) {
        customerService.deleteById(id);
        return "redirect:/customers";
    }

    @GetMapping("/add")
    public String add(Model model) {
        addCurrentUserToModel(model);
        Long customerId = 0L;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CurrentUser currentUser) {
            customerId = currentUser.getId();
            model.addAttribute("account", paymentService.getAccountByCustomerId(currentUser.getId()));
            model.addAttribute("customer", currentUser.getCustomer());
            return "customer/balance_customer";
        }
        return "redirect:/customers/edit/" + customerId;
    }

    @PostMapping("/add")
    public String addFunds(@RequestParam Long customerId, @RequestParam BigDecimal amount, Model model) {
        try {
            String response = paymentService.addFundsToAccount(customerId, amount);
            model.addAttribute("message", response);
        } catch (Exception e) {
            model.addAttribute("error", "Failed to update balance: " + e.getMessage());
        }
        return "redirect:/customers/edit/" + customerId;
    }

}
