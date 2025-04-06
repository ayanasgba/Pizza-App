package whz.pti.eva.order.boundary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import whz.pti.eva.cart.domai.Cart;
import whz.pti.eva.cart.service.CartService;
import whz.pti.eva.customer.domain.CurrentUser;
import whz.pti.eva.order.domain.Ordered;
import whz.pti.eva.order.service.OrderService;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;

    @Autowired
    public OrderController(OrderService orderService, CartService cartService) {
        this.orderService = orderService;
        this.cartService = cartService;
    }

    private void addCurrentUserToModel(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CurrentUser) {
            CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
            model.addAttribute("currentUser", currentUser);
        } else {
            model.addAttribute("currentUser", null);
        }
    }

    @PostMapping("/create")
    public String createOrderFromCart(@RequestParam("cartId") Long cartId, Model model) {
        addCurrentUserToModel(model);
        try {
            Ordered order = orderService.createOrderFromCart(cartId);
            model.addAttribute("order", order);
            return "redirect:/orders/details/" + order.getId();
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage()); // Добавляем сообщение об ошибке
            return "redirect:/cart?error=true";
        }
    }

    @GetMapping("/details/{orderId}")
    public String showOrderDetails(@PathVariable("orderId") Long orderId, Model model) {
        addCurrentUserToModel(model);
        Ordered order = orderService.getOrderById(orderId);
        model.addAttribute("order", order);
        return "order/order_details";
    }

    @GetMapping
    public String showOrders(Model model) {
        addCurrentUserToModel(model);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<Ordered> orders = new ArrayList<>();
        if (authentication != null && authentication.getPrincipal() instanceof CurrentUser) {
            CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
            orders = orderService.getAllOrderByUserId(currentUser.getCustomer());
        }

        model.addAttribute("orders", orders);
        return "order/orders";
    }

    @GetMapping("/getAll")
    public String showAllOrders(Model model) {
        addCurrentUserToModel(model);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<Ordered> orders = new ArrayList<>();
        if (authentication != null && authentication.getPrincipal() instanceof CurrentUser) {
            CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
            orders = orderService.getAll();
        }

        model.addAttribute("orders", orders);
        return "order/orders";
    }
}
