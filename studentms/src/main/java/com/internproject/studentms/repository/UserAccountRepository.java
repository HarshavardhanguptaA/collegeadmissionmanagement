package com.internproject.studentms.repository;

import com.internproject.studentms.entity.AccountRole;
import com.internproject.studentms.entity.UserAccount;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    // Load the linked student profile before the repository transaction closes;
    // authentication builds a principal that includes the student ID.
    @EntityGraph(attributePaths = "studentProfile")
    Optional<UserAccount> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
    long countByRole(AccountRole role);
}
