package org.computerspareparts.csms.global.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFailureListener implements ApplicationListener<AbstractAuthenticationFailureEvent> {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationFailureListener.class);

    @Override
    public void onApplicationEvent(AbstractAuthenticationFailureEvent event) {
        Object principal = event.getAuthentication() != null ? event.getAuthentication().getPrincipal() : "<unknown>";
        String who = principal instanceof String ? (String) principal : String.valueOf(principal);
        String exMsg = event.getException() != null ? event.getException().getMessage() : "<no-exception-message>";
        log.warn("Authentication failure for user='{}', reason={}", who, exMsg);
    }
}

