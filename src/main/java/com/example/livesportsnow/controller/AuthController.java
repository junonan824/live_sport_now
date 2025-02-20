package com.example.livesportsnow.controller;

import com.example.livesportsnow.dto.MemberRequest;
import com.example.livesportsnow.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody MemberRequest request) {
        authService.signup(request);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/login") 
    public ResponseEntity<String> login(@RequestBody MemberRequest request) {
        String token = authService.login(request);
        return ResponseEntity.ok(token);
    }
} 