package org.computerspareparts.csms.global.security;



import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.computerspareparts.csms.global.factory.UserRoleFactory;
import org.computerspareparts.csms.global.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String username = authentication.getName();
        log.info("Authentication success handler invoked for user={}", username);

        // Default fallback
        String targetUrl = "/";

        try {
            var optUser = userRepository.findByEmail(username);
            if (optUser.isPresent()) {
                var user = optUser.get();
                var handler = UserRoleFactory.getHandler(user.getRole());
                targetUrl = handler.getDashboardUrl();
            } else {
                log.warn("User not found in success handler: {}", username);
            }
        } catch (Exception ex) {
            log.error("Error determining target URL for user {}: {}", username, ex.getMessage());
        }

        // Build absolute context path aware URL
        String redirectUrl = UriComponentsBuilder.fromPath(request.getContextPath()).path(targetUrl).toUriString();
        log.info("Redirecting user {} to {}", username, redirectUrl);
        response.sendRedirect(redirectUrl);
    }
}
