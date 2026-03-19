package backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import backend.security.JwtAuthenticationFilter;
import backend.security.JwtTokenProvider;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtTokenProvider jwtTokenProvider) throws Exception {
        http.csrf(csrf -> csrf.disable()); // REST API(JWT)에서는 CSRF 비활성화

        http.authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/h2-console/**").permitAll()
            .requestMatchers("/api/v1/users/login").permitAll()
            .requestMatchers("/api/v1/contents/**").permitAll()
            .anyRequest().authenticated()
        );

        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin())); // H2 콘솔이 프레임 내에서 작동하도록 허용
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
