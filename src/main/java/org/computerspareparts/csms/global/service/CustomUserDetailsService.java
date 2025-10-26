package org.computerspareparts.csms.global.service;



import org.computerspareparts.csms.global.entity.User;
import org.computerspareparts.csms.global.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Attempting to load user by email: {}", email);
        Optional<User> opt = userRepository.findByEmail(email);
        User user = opt.orElseThrow(() -> {
            log.warn("User not found with email: {}", email);
            return new UsernameNotFoundException("User not found with email: " + email);
        });

        // Log a preview of the stored password hash (not the plaintext) to detect formatting issues
        String pwd = user.getPassword();
        if (pwd != null) {
            String preview = pwd.length() > 12 ? pwd.substring(0, 12) + "..." : pwd;
            log.info("Loaded user id={}, email={}, role={}, active={}, passwordHashPreview='{}', hashLength={}",
                    user.getUserId(), user.getEmail(), user.getRole(), user.isActive(), preview, pwd.length());
        } else {
            log.info("Loaded user id={}, email={}, role={}, active={}, password is null", user.getUserId(), user.getEmail(), user.getRole(), user.isActive());
        }

        // Sanitize stored password: trim whitespace and strip surrounding quotes if present
        String stored = user.getPassword();
        String passwordForAuth = null;
        if (stored != null) {
            passwordForAuth = stored.trim();
            if ((passwordForAuth.startsWith("\"") && passwordForAuth.endsWith("\"")) || (passwordForAuth.startsWith("'") && passwordForAuth.endsWith("'"))) {
                passwordForAuth = passwordForAuth.substring(1, passwordForAuth.length() - 1).trim();
                log.debug("Stripped surrounding quotes from stored password hash for user {}", user.getEmail());
            }
        }

        // Map role to authorities and supply the sanitized password
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(passwordForAuth != null ? passwordForAuth : "")
                .roles(user.getRole().name())
                .disabled(!user.isActive())
                .build();
    }
}
