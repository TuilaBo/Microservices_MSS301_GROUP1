package com.khoavdse170395.accountservice.config;

import com.khoavdse170395.accountservice.model.Account;
import com.khoavdse170395.accountservice.security.JwtTokenProvider;
import com.khoavdse170395.accountservice.service.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;

@Component
@Slf4j
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider tokenProvider;
    private final AccountService accountService;
    
    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException {
        
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        
        log.info("OAuth2 login attempt for email: {}", email);
        
        if (email != null) {
            try {
                // Try to get account (should already be created in oauth2UserService)
                Account account;
                try {
                    account = accountService.getAccountByEmail(email);
                    log.info("Account retrieved: {}", account.getEmail());
                } catch (Exception e) {
                    // If account not found, create it
                    log.warn("Account not found, creating new account for: {}", email);
                    account = accountService.processOAuth2Login(email, name);
                    log.info("Account created: {}", account.getEmail());
                }
                
                // Get role from account
                String roleName = account.getRole() != null ? account.getRole().getRoleName() : "USER";
                
                // Create authentication for JWT
                Authentication jwtAuth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + roleName))
                );
                
                // Generate JWT token
                String token = tokenProvider.generateToken(jwtAuth);
                log.info("JWT token generated successfully");
                
                // Redirect to frontend with token in URL
                String redirectUrl = frontendUrl + "/#oauth-callback?token=" + token;
                log.info("Redirecting to: {}", redirectUrl);
                getRedirectStrategy().sendRedirect(request, response, redirectUrl);
                
            } catch (Exception e) {
                log.error("OAuth2 login failed for email: {}", email, e);
                // On error, redirect to login with error
                String redirectUrl = frontendUrl + "/login?error=oauth_failed&message=" + e.getMessage();
                getRedirectStrategy().sendRedirect(request, response, redirectUrl);
            }
        } else {
            log.error("Email not found in OAuth2 user attributes");
            String redirectUrl = frontendUrl + "/login?error=email_not_found";
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
        }
    }
}
