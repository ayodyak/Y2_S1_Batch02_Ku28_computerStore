package org.computerspareparts.csms.global.security;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private static final Logger log = LoggerFactory.getLogger(CustomAccessDeniedHandler.class);

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.warn("Access denied for request {}. Reason: {}", request.getRequestURI(), accessDeniedException.getMessage());
        // set 403 status
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        // forward to the dedicated 403 error view
        RequestDispatcher dispatcher = request.getRequestDispatcher("/error/403");
        dispatcher.forward(request, response);
    }
}

