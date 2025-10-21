package com.tri.apigateway.service;

import com.tri.apigateway.dto.response.ApiResponse;
import com.tri.apigateway.dto.response.IntrospectResponse;
import reactor.core.publisher.Mono;

public interface IdentityService {

    public Mono<ApiResponse<IntrospectResponse>> introspect(String token);
}
