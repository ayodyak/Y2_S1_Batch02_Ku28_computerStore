package org.computerspareparts.csms.global.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationSuccessListener.class);

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        Object principal = event.getAuthentication().getPrincipal();
        String name = (principal instanceof org.springframework.security.core.userdetails.UserDetails) ?
                ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername() : String.valueOf(principal);
        log.info("Authentication success for user={}", name);
    }
}

