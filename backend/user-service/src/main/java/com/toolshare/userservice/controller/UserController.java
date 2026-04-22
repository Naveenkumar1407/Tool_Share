package com.toolshare.userservice.controller;

import com.toolshare.userservice.dto.UserDTO;
import com.toolshare.userservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // get current logged-in user's profile
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getMyProfile(Authentication auth) {
        UserDTO user = userService.getUserByUsername(auth.getName());
        return ResponseEntity.ok(user);
    }

    // update own profile
    @PutMapping("/me")
    public ResponseEntity<UserDTO> updateProfile(Authentication auth, @RequestBody UserDTO updates) {
        UserDTO updated = userService.updateProfile(auth.getName(), updates);
        return ResponseEntity.ok(updated);
    }

    // get user by id - public endpoint (for tool-service to look up owners)
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(userService.getUserById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // admin: list all users
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
