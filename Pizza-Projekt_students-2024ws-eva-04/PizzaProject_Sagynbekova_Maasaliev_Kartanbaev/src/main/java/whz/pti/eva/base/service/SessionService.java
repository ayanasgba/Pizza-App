package whz.pti.eva.base.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

    private static final String SESSION_CART_ATTRIBUTE = "ANONYMOUS_CART_ID";

    public String getOrCreateSessionId(HttpSession session) {
        String sessionId = (String) session.getAttribute(SESSION_CART_ATTRIBUTE);
        if (sessionId == null) {
            sessionId = session.getId();
            session.setAttribute(SESSION_CART_ATTRIBUTE, sessionId);
        }
        return sessionId;
    }
}
