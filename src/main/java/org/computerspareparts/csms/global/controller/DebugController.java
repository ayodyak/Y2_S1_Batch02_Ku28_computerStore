package org.computerspareparts.csms.global.controller;

import org.computerspareparts.csms.global.entity.User;
import org.computerspareparts.csms.global.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Profile("dev")
@RestController
public class DebugController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Example: GET /debug/check-password?email=user@example.com&plain=secret
    @GetMapping("/debug/check-password")
    public ResponseEntity<Map<String, Object>> checkPassword(@RequestParam String email, @RequestParam String plain) {
        var opt = userRepository.findByEmail(email);
        Map<String, Object> resp = new HashMap<>();
        if (opt.isEmpty()) {
            resp.put("found", false);
            resp.put("match", false);
            resp.put("message", "user-not-found");
            return ResponseEntity.ok(resp);
        }

        var user = opt.get();
        String stored = user.getPassword();
        if (stored != null) stored = stored.trim();
        boolean match = stored != null && passwordEncoder.matches(plain, stored);
        resp.put("found", true);
        resp.put("match", match);
        resp.put("storedPreview", stored == null ? null : (stored.length() > 12 ? stored.substring(0,12) + "..." : stored));
        resp.put("storedLength", stored == null ? 0 : stored.length());
        return ResponseEntity.ok(resp);
    }

    // Dev-only: set/reset password for an existing user (encoded via PasswordEncoder)
    // Example: GET /debug/set-password?email=user@example.com&plain=newpass
    @GetMapping("/debug/set-password")
    public ResponseEntity<Map<String, Object>> setPassword(@RequestParam String email, @RequestParam String plain) {
        Map<String, Object> resp = new HashMap<>();
        var opt = userRepository.findByEmail(email);
        if (opt.isEmpty()) {
            resp.put("updated", false);
            resp.put("message", "user-not-found");
            return ResponseEntity.ok(resp);
        }

        User user = opt.get();
        String encoded = passwordEncoder.encode(plain);
        user.setPassword(encoded);
        userRepository.save(user);

        resp.put("updated", true);
        resp.put("storedPreview", encoded.length() > 12 ? encoded.substring(0,12) + "..." : encoded);
        resp.put("storedLength", encoded.length());
        return ResponseEntity.ok(resp);
    }
}
