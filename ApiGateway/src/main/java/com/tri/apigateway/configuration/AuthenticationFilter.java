package com.tri.apigateway.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tri.apigateway.dto.response.ApiResponse;
import com.tri.apigateway.enums.ErrorCode;
import com.tri.apigateway.util.JwtUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PACKAGE, makeFinal = true)
public class AuthenticationFilter implements GlobalFilter, Ordered {
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    JwtUtil jwtUtil;
    ObjectMapper objectMapper;


    private String[] publicEndpoints = {
            "/auth/register",
            "/auth/register-teacher",
            "/auth/login",
            "/lessons/public/**"

    };

    @Value("${app.api-prefix}")
    @NonFinal
    private String apiPrefix;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("Enter authentication filter....");
        log.info("Gateway path: {}", exchange.getRequest().getURI().getPath());
        if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        if (isPublicEndpoint(exchange.getRequest()))
            return chain.filter(exchange);

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(authHeader))
            return unauthenticated(exchange);

        String token = authHeader.replace("Bearer ", "");
        log.info("Token: {}", token);

        if (jwtUtil.validateToken(token)){
            return chain.filter(exchange);
        }
        return unauthenticated(exchange);
//        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private boolean isPublicEndpoint(ServerHttpRequest request){
        String path = request.getURI().getPath();
        String normalizedPrefix = normalizePrefix(apiPrefix);

        boolean match = Arrays.stream(publicEndpoints)
                .map(this::normalizePattern)
                .map(pattern -> normalizedPrefix + pattern)
                .anyMatch(fullPattern -> {
                    boolean result = PATH_MATCHER.match(fullPattern, path);
                    log.info("Checking pattern {} against {} => {}", fullPattern, path, result);
                    return result;
                });

        log.info("isPublicEndpoint? path={} result={}", path, match);
        return match;
    }

    private String normalizePrefix(String prefix) {
        if (prefix == null || prefix.isBlank()) {
            return "";
        }
        String trimmed = prefix.trim();
        if (trimmed.endsWith("/")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        if (!trimmed.startsWith("/")) {
            trimmed = "/" + trimmed;
        }
        return trimmed;
    }

    private String normalizePattern(String pattern) {
        if (pattern == null || pattern.isBlank()) {
            return "";
        }
        return pattern.startsWith("/") ? pattern : "/" + pattern;
    }

    Mono<Void> unauthenticated(ServerWebExchange exchange){
        ServerHttpResponse response = exchange.getResponse();
        ErrorCode errorCode = ErrorCode.UNAUTHENTICATE;
        ApiResponse<?> apiResponse = ApiResponse.error(errorCode);

        String body = null;
        try {
            body = objectMapper.writeValueAsString(apiResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        response.setStatusCode(errorCode.getStatusCode());
        addCorsHeaders(exchange, response);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        return response.writeWith(
                Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }

    private void addCorsHeaders(ServerWebExchange exchange, ServerHttpResponse response) {
        String origin = exchange.getRequest().getHeaders().getOrigin();
        if (origin != null) {
            response.getHeaders().set(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
            response.getHeaders().set(HttpHeaders.VARY, HttpHeaders.ORIGIN);
            response.getHeaders().set(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
            response.getHeaders().set(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET,POST,PUT,DELETE,OPTIONS");
            response.getHeaders().set(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "Authorization,Content-Type");
        }
    }
}
