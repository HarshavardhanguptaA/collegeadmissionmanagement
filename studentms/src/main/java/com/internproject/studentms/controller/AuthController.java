package com.internproject.studentms.controller;

import com.internproject.studentms.dto.AuthResponse;
import com.internproject.studentms.dto.LoginRequest;
import com.internproject.studentms.dto.RegisterRequest;
import com.internproject.studentms.security.UserPrincipal;
import com.internproject.studentms.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService) { this.authService = authService; }
    @PostMapping("/register") @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) { return authService.register(request); }
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) { return authService.login(request); }
    @GetMapping("/me")
    public AuthResponse me(@AuthenticationPrincipal UserPrincipal principal) { return authService.me(principal); }
}
