    package com.loanflow.entity;

    import com.loanflow.enums.Role;
    import jakarta.persistence.*;
    import lombok.*;
    import org.springframework.security.core.GrantedAuthority;
    import org.springframework.security.core.authority.SimpleGrantedAuthority;
    import org.springframework.security.core.userdetails.UserDetails;

    import java.time.LocalDateTime;
    import java.util.Collection;
    import java.util.List;

    @Entity
    @Table(name = "users")
    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public class User implements UserDetails {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private String name;

        @Column(nullable = false, unique = true)
        private String email;

        @Column(nullable = false)
        private String password;

        @Column(nullable = false, unique = true, length = 8)
        private String dni;

        private String phone;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private Role role;

        @Column(name = "created_at")
        private LocalDateTime createdAt;

        @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        private List<Loan> loans;


        @PrePersist
        public void prePersist() {
            this.createdAt = LocalDateTime.now();
        }

        // --- UserDetails methods ---
        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
        }

        @Override
        public String getUsername() {
            return email;
        }

        @Override
        public boolean isAccountNonExpired() { return true; }

        @Override
        public boolean isAccountNonLocked() { return true; }

        @Override
        public boolean isCredentialsNonExpired() { return true; }

        @Override
        public boolean isEnabled() { return true; }
    }
