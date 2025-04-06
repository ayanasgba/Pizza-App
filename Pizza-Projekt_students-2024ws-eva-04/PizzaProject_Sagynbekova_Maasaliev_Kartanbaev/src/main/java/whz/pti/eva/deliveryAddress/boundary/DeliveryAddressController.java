package whz.pti.eva.deliveryAddress.boundary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import whz.pti.eva.cart.domai.Cart;
import whz.pti.eva.customer.domain.CurrentUser;
import whz.pti.eva.customer.domain.Customer;
import whz.pti.eva.customer.service.CustomerService;
import whz.pti.eva.deliveryAddress.domain.DeliveryAddress;
import whz.pti.eva.deliveryAddress.service.DeliveryAddressService;

@Controller
@RequestMapping("/address")
public class DeliveryAddressController {

    @Autowired
    private DeliveryAddressService deliveryAddressService;
    @Autowired
    private CustomerService customerService;

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("deliveryAddress", deliveryAddressService.getById(id));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof CurrentUser) {
            CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
            model.addAttribute("currentUser", currentUser);

        } else {
            model.addAttribute("currentUser", null);
            model.addAttribute("cartQuantity", 0);
        }

        return "address/edit_address";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable("id") Long id, DeliveryAddress deliveryAddress) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CurrentUser currentUser) {
            deliveryAddressService.update(id, deliveryAddress);

            return "redirect:/customers/edit/" + currentUser.getId();
        }
        return "redirect:/";
    }

    @PostMapping("/add")
    public String addAddress(@ModelAttribute("deliveryAddress") DeliveryAddress deliveryAddress) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CurrentUser currentUser) {
            deliveryAddressService.save(deliveryAddress);

            Customer customer = customerService.getById(currentUser.getId());
            customer.addDeliveryAddress(deliveryAddress);
            customerService.save(customer);

            return "redirect:/customers/edit/" + currentUser.getId();
        }
        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable("id") Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CurrentUser currentUser) {
            deliveryAddressService.deleteById(id);

            return "redirect:/customers/edit/" + currentUser.getId();
        }
        return "redirect:/";
    }
}
