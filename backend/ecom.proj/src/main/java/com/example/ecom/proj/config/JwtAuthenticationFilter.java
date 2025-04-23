package com.example.ecom.proj.config;

import com.example.ecom.proj.constant.SecurityConstants;
import com.example.ecom.proj.dao.TokenRepository;
import com.example.ecom.proj.dao.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        // It didn't authenticate the /api/v1/auth/** like login, register
//        if (request.getServletPath().contains(SecurityConstants.AUTH_BASE)) {
//            filterChain.doFilter(request, response); // next filter chain
//            return;
//        }

        // Only skip the routes in AUTH_WHITELIST
        if (SecurityConstants.AUTH_WHITELIST.contains(request.getServletPath())) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String token;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.setContentType("application/json");
//            response.getWriter().write("{\"error\": \"Missing or invalid Authorization header\"}");
            return;
        }

        token = authHeader.substring(7);
        userEmail = jwtService.extractUsername(token);
        if (!userEmail.isEmpty() && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails user = userRepository.findByEmail(userEmail)
                    .orElseThrow();
            boolean isTokenValid = tokenRepository.findByToken(token)
                    .map(t -> !t.isExpired() && !t.isRevoked())
                    .orElse(false);
            if (jwtService.isTokenValid(token, user, userEmail) && isTokenValid) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        user.getAuthorities() // list of roles/permissions for this user
                        // (like [admin:create, admin:update, admin:delete, management:create, admin:read, management:read,
                        // management:update, management:delete, ROLE_ADMIN])
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);  // Spring Security's storage for current authentication
            }
        }
        filterChain.doFilter(request, response);
    }
}
