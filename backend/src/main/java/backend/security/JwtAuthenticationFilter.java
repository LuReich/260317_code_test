package backend.security;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                Claims claims = jwtTokenProvider.parse(token);
                String loginId = claims.getSubject();
                String role = String.valueOf(claims.get("role"));

                List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(loginId, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (JwtException | IllegalArgumentException e) {
                // 토큰이 이상하면 그냥 인증 없이 통과 (보호된 API면 401/403으로 걸러짐)
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
