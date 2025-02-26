package com.task.management.system.service;

import com.task.management.system.configuration.security.Token;
import com.task.management.system.configuration.security.model.TokenUser;
import com.task.management.system.repository.DeactivatedTokenRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
public class TokenAuthenticationUserDetailsService
        implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

    private final DeactivatedTokenRepository deactivatedTokenRepository;

    public TokenAuthenticationUserDetailsService(DeactivatedTokenRepository deactivatedTokenRepository) {
        this.deactivatedTokenRepository = deactivatedTokenRepository;
    }

    @Override
    public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken authenticationToken)
            throws UsernameNotFoundException {
        if (authenticationToken.getPrincipal() instanceof Token token) {
            boolean isTokenActive = !deactivatedTokenRepository.existsById(token.id()) &&
                    token.expiresAt().isAfter(Instant.now());
            return new TokenUser(token.subject(), "nopassword", true, true,
                    isTokenActive, true,
                    token.authorities().stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList()), token);
        }
        throw new UsernameNotFoundException("Principal must be of type Token");
    }
}
