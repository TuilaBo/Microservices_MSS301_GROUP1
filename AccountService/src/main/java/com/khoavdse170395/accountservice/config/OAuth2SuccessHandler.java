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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;

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
                Account account = accountService.processOAuth2Login(email, name);
                log.info("OAuth2 login account verified: {}", account.getEmail());

                String roleName = account.getRole() != null ? account.getRole().getRoleName() : "USER";

                ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_" + roleName));
                if (email.endsWith("@fpt.edu.vn")) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                }

                Authentication jwtAuth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        authorities
                );

                String token = tokenProvider.generateToken(jwtAuth);
                log.info("JWT token generated successfully");

                String redirectUrl = frontendUrl + "/#oauth-callback?token=" + token;
                log.info("Redirecting to: {}", redirectUrl);
                getRedirectStrategy().sendRedirect(request, response, redirectUrl);

            } catch (UsernameNotFoundException e) {
                log.error("OAuth2 login failed - account not registered: {}", email);
                String redirectUrl = frontendUrl + "/login?error=account_not_registered";
                getRedirectStrategy().sendRedirect(request, response, redirectUrl);
            } catch (IllegalStateException e) {
                log.error("OAuth2 login failed - account inactive: {}", email);
                String redirectUrl = frontendUrl + "/login?error=account_inactive";
                getRedirectStrategy().sendRedirect(request, response, redirectUrl);
            } catch (Exception e) {
                log.error("OAuth2 login failed for email: {}", email, e);
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
