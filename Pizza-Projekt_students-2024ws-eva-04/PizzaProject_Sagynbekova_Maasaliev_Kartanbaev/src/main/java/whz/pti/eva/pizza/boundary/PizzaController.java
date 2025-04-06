package whz.pti.eva.pizza.boundary;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import whz.pti.eva.base.service.SessionService;
import whz.pti.eva.cart.domai.Cart;
import whz.pti.eva.customer.domain.CurrentUser;
import whz.pti.eva.pizza.domain.Pizza;
import whz.pti.eva.cart.service.CartService;
import whz.pti.eva.pizza.service.PizzaService;

import java.io.File;
import java.io.IOException;
@Controller
@RequestMapping("/pizzas")
public class PizzaController {

    @Autowired
    private PizzaService pizzaService;
    
    @Autowired
    private CartService cartService;

    @Autowired
    private SessionService sessionService;

    private final String UPLOAD_DIR = "uploads/"; 

    @PostMapping("/create")
    public String createPizza(@ModelAttribute Pizza pizza, @RequestParam("image") MultipartFile imageFile) {
        try {
            
            String fileName = imageFile.getOriginalFilename();
            File file = new File(UPLOAD_DIR + fileName);
            imageFile.transferTo(file); 

            pizza.setImagePath(UPLOAD_DIR + fileName);

            pizzaService.save(pizza); 
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/pizzas";
    }
    
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("pizza", new Pizza());
        return "pizza/create_pizza";
    }

    @GetMapping
    public String getAllPizzas(Model model, HttpSession session) {
        model.addAttribute("pizzas", pizzaService.getAll());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof CurrentUser) {
            CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
            model.addAttribute("currentUser", currentUser);

            Cart cart = cartService.getCartByCustomer(currentUser.getId());
            model.addAttribute("cart", cart);
            model.addAttribute("cartQuantity", cart.getQuantity());
            model.addAttribute("cartTotalPrice", cart.getTotalPrice());
        } else {
            String sessionId = sessionService.getOrCreateSessionId(session);
            Cart cart = cartService.getOrCreateCart(sessionId);
            model.addAttribute("cart", cart);
            model.addAttribute("cartQuantity", cart.getQuantity());
            model.addAttribute("cartTotalPrice", cart.getTotalPrice());
        }

        return "pizza/list_pizzas";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(Model model, @PathVariable("id") Long id) {
        Pizza pizza = pizzaService.getById(id);
        if (pizza != null) {
            model.addAttribute("pizza", pizza);
            return "pizza/edit_pizza";
        } else {
            return "redirect:/pizzas";
        }
    }

    @PostMapping("/edit/{id}")
    public String updatePizza(@PathVariable("id") Long id, @ModelAttribute("pizza") Pizza pizza) {
        pizzaService.update(id, pizza);
        return "redirect:/pizzas";
    }

    @GetMapping("/delete/{id}")
    public String deletePizza(@PathVariable("id") Long id) {
        pizzaService.deleteById(id);
        return "redirect:/pizzas";
    }

}