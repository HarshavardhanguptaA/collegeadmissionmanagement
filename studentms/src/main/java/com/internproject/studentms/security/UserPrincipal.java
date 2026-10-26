package com.internproject.studentms.security;

import com.internproject.studentms.entity.UserAccount;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

public class UserPrincipal implements UserDetails {
    private final Long id;
    private final String fullName;
    private final String email;
    private final String passwordHash;
    private final String role;
    private final Long studentId;

    public UserPrincipal(UserAccount user) {
        this.id = user.getId();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.passwordHash = user.getPasswordHash();
        this.role = user.getRole().name();
        this.studentId = user.getStudentProfile() == null ? null : user.getStudentProfile().getId();
    }
    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }
    public Long getStudentId() { return studentId; }
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return List.of(new SimpleGrantedAuthority("ROLE_" + role)); }
    @Override public String getPassword() { return passwordHash; }
    @Override public String getUsername() { return email; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
