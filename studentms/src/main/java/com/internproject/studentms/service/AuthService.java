package com.internproject.studentms.service;

import com.internproject.studentms.dto.AuthResponse;
import com.internproject.studentms.dto.LoginRequest;
import com.internproject.studentms.dto.RegisterRequest;
import com.internproject.studentms.entity.AccountRole;
import com.internproject.studentms.entity.Student;
import com.internproject.studentms.entity.UserAccount;
import com.internproject.studentms.repository.StudentRepository;
import com.internproject.studentms.repository.UserAccountRepository;
import com.internproject.studentms.security.JwtService;
import com.internproject.studentms.security.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    private final UserAccountRepository users;
    private final StudentRepository students;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserAccountRepository users, StudentRepository students, PasswordEncoder encoder,
                       AuthenticationManager authenticationManager, JwtService jwtService) {
        this.users = users; this.students = students; this.encoder = encoder;
        this.authenticationManager = authenticationManager; this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (users.existsByEmailIgnoreCase(email) || students.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An account with this email already exists");
        }
        Student student = new Student();
        student.setFirstName(request.firstName().trim());
        student.setLastName(request.lastName().trim());
        student.setEmail(email);
        student.setGender("Prefer not to say");
        student.setPhone(blankToNull(request.phone()));
        student.setAddress(blankToNull(request.address()));
        Student savedStudent = students.save(student);

        UserAccount account = new UserAccount();
        account.setFullName(savedStudent.getFirstName() + " " + savedStudent.getLastName());
        account.setEmail(email);
        account.setPasswordHash(encoder.encode(request.password()));
        account.setRole(AccountRole.STUDENT);
        account.setStudentProfile(savedStudent);
        users.save(account);
        return toResponse(account, jwtService.createToken(new UserPrincipal(account)));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email().trim().toLowerCase(), request.password()));
        } catch (AuthenticationException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        return users.findByEmailIgnoreCase(principal.getUsername())
            .map(user -> toResponse(user, jwtService.createToken(new UserPrincipal(user))))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
    }

    @Transactional(readOnly = true)
    public AuthResponse me(UserPrincipal principal) {
        UserAccount user = users.findByEmailIgnoreCase(principal.getUsername())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account no longer exists"));
        return toResponse(user, null);
    }

    private AuthResponse toResponse(UserAccount user, String token) {
        Long studentId = user.getStudentProfile() == null ? null : user.getStudentProfile().getId();
        return new AuthResponse(token, user.getId(), user.getFullName(), user.getEmail(), user.getRole().name(), studentId);
    }
    private String blankToNull(String value) { return value == null || value.isBlank() ? null : value.trim(); }
}
