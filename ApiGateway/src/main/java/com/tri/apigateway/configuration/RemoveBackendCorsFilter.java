package com.tri.apigateway.configuration;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class RemoveBackendCorsFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange.mutate().response(
            new ServerHttpResponseDecorator(exchange.getResponse()) {
                @Override
                public HttpHeaders getHeaders() {
                    HttpHeaders headers = super.getHeaders();
                    
                    // Remove CORS headers only when duplicates are detected
                    // This prevents "multiple values" error when both gateway and backend add CORS headers
                    // Gateway's CorsWebFilter will add the correct single CORS header value
                    if (headers.containsKey(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN)) {
                        String originValue = headers.getFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
                        boolean hasDuplicates = (originValue != null && originValue.contains(",")) 
                            || headers.get(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN).size() > 1;
                        
                        if (hasDuplicates) {
                            // Remove all CORS headers when duplicates detected
                            // Gateway CORS filter will add correct single value
                            headers.remove(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
                            headers.remove(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS);
                            headers.remove(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS);
                            headers.remove(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS);
                            headers.remove(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS);
                            headers.remove(HttpHeaders.ACCESS_CONTROL_MAX_AGE);
                        }
                    }
                    
                    return headers;
                }
            }
        ).build());
    }

    @Override
    public int getOrder() {
        // Run early to intercept response headers
        // But after CORS filter processes preflight requests
        return -1;
    }
}

