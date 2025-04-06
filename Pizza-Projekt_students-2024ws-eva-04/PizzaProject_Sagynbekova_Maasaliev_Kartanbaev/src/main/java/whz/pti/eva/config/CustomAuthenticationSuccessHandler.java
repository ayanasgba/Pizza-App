package whz.pti.eva.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import whz.pti.eva.cart.service.CartService;
import whz.pti.eva.customer.domain.CurrentUser;


import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final CartService cartService;

    public CustomAuthenticationSuccessHandler(CartService cartService) {
        this.cartService = cartService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (authentication.getPrincipal() instanceof CurrentUser) {
            CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();

            String sessionId = (String) request.getSession().getAttribute("ANONYMOUS_CART_ID");

            if (sessionId != null) {
                cartService.mergeCarts(sessionId, currentUser.getId());

                request.getSession().removeAttribute("ANONYMOUS_CART_ID");
            }
        }

        response.sendRedirect("/");
    }
}
