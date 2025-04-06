package whz.pti.eva.cart.boundary;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import whz.pti.eva.base.service.SessionService;
import whz.pti.eva.cart.domai.Cart;
import whz.pti.eva.cart.service.CartService;
import whz.pti.eva.customer.domain.CurrentUser;

import java.util.UUID;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private SessionService sessionService;

    @GetMapping
    public String viewCart(HttpSession session, Model model, @RequestParam(value = "error", required = false) String error) {
        Cart cart;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CurrentUser) {
            CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
            model.addAttribute("currentUser", currentUser);
            cart = cartService.getCartByCustomer(currentUser.getId());
        } else {
            String sessionId = sessionService.getOrCreateSessionId(session);
            cart = cartService.getOrCreateCart(sessionId);
        }

        if (error != null) {
            model.addAttribute("errorMessage", "There are not enough funds to place an order."); // Сообщение об ошибке
        }

        model.addAttribute("cart", cart);
        return "cart/view_cart";
    }

    @PostMapping("/add")
    public String addItemToCart(HttpSession session,
                                @RequestParam("pizzaId") Long pizzaId,
                                @RequestParam("pizzaSize") String pizzaSize,
                                @RequestParam("quantity") int quantity) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CurrentUser) {
            CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
            cartService.addItemToCart(currentUser.getId(), pizzaId, pizzaSize, quantity);
        } else {
            String sessionId = (String) session.getAttribute("ANONYMOUS_CART_ID");
            if (sessionId == null) {
                sessionId = UUID.randomUUID().toString();
                session.setAttribute("ANONYMOUS_CART_ID", sessionId);
            }
            cartService.addItemToAnonymousCart(sessionId, pizzaId, pizzaSize, quantity);
        }
        return "redirect:/pizzas";
    }

    @PostMapping("/update")
    public String updateItemQuantity(
            HttpSession session,
            @RequestParam("id") UUID itemId,
            @RequestParam("quantity") int quantity) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CurrentUser) {
            CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();

            cartService.updateItemQuantityForCustomer(currentUser.getId(), itemId, quantity);
        } else {
            String sessionId = (String) session.getAttribute("ANONYMOUS_CART_ID");
            if (sessionId == null) {
                throw new IllegalArgumentException("Session ID is missing for anonymous cart.");
            }
            cartService.updateItemQuantityForAnonymous(sessionId, itemId, quantity);
        }

        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeItem(
            @RequestParam("itemId") UUID itemId,
            @RequestParam(value = "customerId", required = false) Long customerId,
            @RequestParam(value = "sessionId", required = false) String sessionId
    ) {
        if (customerId != null) {
            cartService.removeItemFromCart(customerId, itemId);
        } else if (sessionId != null) {
            cartService.removeItemFromAnonymousCart(sessionId, itemId);
        } else {
            throw new IllegalArgumentException("Either customerId or sessionId must be provided");
        }
        return "redirect:/cart";
    }


    @PostMapping("/clear")
    public String clearCart(
            HttpSession session,
            @RequestParam(value = "customerId", required = false) Long customerId,
            @RequestParam(value = "sessionId", required = false) String sessionId) {

        if (customerId != null) {
            cartService.clearCartForCustomer(customerId);
        } else if (sessionId != null) {
            cartService.clearCartForAnonymous(sessionId);
        } else {
            throw new IllegalArgumentException("Neither customerId nor sessionId provided");
        }

        return "redirect:/cart";
    }


    @PostMapping("/checkout")
    public String checkout(HttpSession session) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof CurrentUser)) {
            return "redirect:/auth/login";
        }

        return "redirect:/order/confirmation";
    }
}