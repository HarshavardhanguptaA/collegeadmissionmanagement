package com.internproject.studentms.security;

import com.internproject.studentms.repository.UserAccountRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {
    private final UserAccountRepository users;
    public DatabaseUserDetailsService(UserAccountRepository users) { this.users = users; }
    @Override public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return users.findByEmailIgnoreCase(username.trim())
            .map(UserPrincipal::new)
            .orElseThrow(() -> new UsernameNotFoundException("Account not found"));
    }
}
