    package com.loanflow.config;

    import com.loanflow.security.JwtAuthFilter;
    import lombok.RequiredArgsConstructor;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.authentication.AuthenticationProvider;
    import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
    import org.springframework.security.config.Customizer;
    import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
    import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
    import org.springframework.security.config.http.SessionCreationPolicy;
    import org.springframework.security.core.userdetails.UserDetailsService;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.security.web.SecurityFilterChain;
    import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
    import org.springframework.security.authentication.ProviderManager;



    @Configuration
    @EnableWebSecurity
    @EnableMethodSecurity
    @RequiredArgsConstructor
    public class SecurityConfig {

        private final JwtAuthFilter jwtAuthFilter;
        private final UserDetailsService userDetailsService;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(AbstractHttpConfigurer::disable)
                    .cors(cors -> cors.configurationSource(request -> {
                        var config = new org.springframework.web.cors.CorsConfiguration();
                        config.setAllowedOriginPatterns(java.util.List.of("*"));
                        config.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                        config.setAllowedHeaders(java.util.List.of("*"));
                        config.setAllowCredentials(true);
                        return config;
                    }))
                    .authorizeHttpRequests( auth -> auth
                            .requestMatchers("/api/auth/**").permitAll()
                            .requestMatchers("/api/simulator/**").permitAll()

                            // Users: /me para cualquiera, el resto solo ADMIN
                            .requestMatchers("/api/users/me").authenticated()
                            .requestMatchers("/api/users/**").hasRole("ADMIN")

                            // Loan types: lectura para ambos, escritura solo ADMIN
                            .requestMatchers("GET", "/api/loan-types/**").hasAnyRole("ADMIN", "CLIENT")
                            .requestMatchers("/api/loan-types/**").hasRole("ADMIN")

                            .requestMatchers("/api/dashboard/**").hasRole("ADMIN")
                            .requestMatchers("/api/payments/all").hasRole("ADMIN")
                            .requestMatchers("/api/loans/**").hasAnyRole("ADMIN", "CLIENT")
                            .requestMatchers("/api/payments/**").hasAnyRole("ADMIN", "CLIENT")
                            .requestMatchers("/api/installments/**").hasAnyRole("ADMIN", "CLIENT")

                            .anyRequest().authenticated()
                    )
                    .sessionManagement(session ->
                            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    )
                    .authenticationProvider(authenticationProvider())
                    .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

            return http.build();
        }

        @Bean
        public AuthenticationProvider authenticationProvider() {
            DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
            provider.setUserDetailsService(userDetailsService);
            provider.setPasswordEncoder(passwordEncoder());
            return provider;
        }

        @Bean
        public AuthenticationManager authenticationManager() {
            // Creamos el AuthenticationManager directamente desde nuestro provider
            // Así garantizamos que usa nuestro BCryptPasswordEncoder y UserDetailsService
            return new ProviderManager(authenticationProvider());
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }
