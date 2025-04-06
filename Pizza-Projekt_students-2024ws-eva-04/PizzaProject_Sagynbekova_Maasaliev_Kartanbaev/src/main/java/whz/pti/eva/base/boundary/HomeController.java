package whz.pti.eva.base.boundary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import whz.pti.eva.cart.domai.Cart;
import whz.pti.eva.customer.domain.CurrentUser;
import whz.pti.eva.cart.service.CartService;
import whz.pti.eva.pizza.service.PizzaService;

@Controller
public class HomeController {

    @Autowired
    private CartService cartService;

    @Autowired
    private PizzaService pizzaService; 

    @GetMapping("/") 
    public String home(Model model) {
        model.addAttribute("title", "Home - Pizza Website");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof CurrentUser) {
            CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
            model.addAttribute("currentUser", currentUser);

            Cart cart = cartService.getCartByCustomer(currentUser.getId());
            model.addAttribute("cartQuantity", cart.getQuantity());
        } else {
            model.addAttribute("currentUser", null);
            model.addAttribute("cartQuantity", 0);
        }

        model.addAttribute("pizzas", pizzaService.getAll());

        return "home"; 
    }
}
