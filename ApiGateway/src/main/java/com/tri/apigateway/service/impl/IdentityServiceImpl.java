package com.tri.apigateway.service.impl;

import com.tri.apigateway.client.IdentityClient;
import com.tri.apigateway.dto.request.IntrospectRequest;
import com.tri.apigateway.dto.response.ApiResponse;
import com.tri.apigateway.dto.response.IntrospectResponse;
import com.tri.apigateway.service.IdentityService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IdentityServiceImpl implements IdentityService {
    IdentityClient identityClient;

    @Override
    public Mono<ApiResponse<IntrospectResponse>> introspect(String token){
        return identityClient.introspect(new IntrospectRequest(token));
    }
}
