package org.computerspareparts.csms.global.controller;

import org.computerspareparts.csms.global.entity.User;
import org.computerspareparts.csms.global.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/customer/profile")
public class ProfileController {

    private final UserService userService;

    @Autowired
    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    // GET /customer/profile
    @GetMapping
    public ResponseEntity<?> getProfile(Authentication auth) {
        if (auth == null || auth.getName() == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthenticated"));
        User user = userService.findByEmail(auth.getName()).orElse(null);
        if (user == null) return ResponseEntity.status(404).body(Map.of("error", "User not found"));

        // Return a simplified profile DTO
        return ResponseEntity.ok(Map.of(
                "user_id", user.getUserId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "phone", user.getPhone(),
                "address", user.getAddress(),
                "city", user.getCity(),
                "role", user.getRole() == null ? null : user.getRole().name(),
                "created_at", user.getCreatedAt() == null ? null : user.getCreatedAt().toString()
        ));
    }

    // PUT /customer/profile
    @PutMapping
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, String> payload, Authentication auth) {
        if (auth == null || auth.getName() == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthenticated"));
        try {
            String name = payload.get("name");
            String phone = payload.get("phone");
            String address = payload.get("address");
            String city = payload.get("city");
            User updated = userService.updateProfile(auth.getName(), name, phone, address, city);
            return ResponseEntity.ok(Map.of(
                    "user_id", updated.getUserId(),
                    "name", updated.getName(),
                    "email", updated.getEmail(),
                    "phone", updated.getPhone(),
                    "address", updated.getAddress(),
                    "city", updated.getCity()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    // POST /customer/profile/change-password
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> payload, Authentication auth) {
        if (auth == null || auth.getName() == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthenticated"));
        String current = payload.get("currentPassword");
        String next = payload.get("newPassword");
        if (current == null || next == null) return ResponseEntity.badRequest().body(Map.of("error", "Missing fields"));
        try {
            userService.changePassword(auth.getName(), current, next);
            return ResponseEntity.ok(Map.of("message", "Password changed"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}

