package com.fib.cash_operations.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthorizationInterceptor implements HandlerInterceptor {

    @Value("${fib.api.key}")
    private String apiKey;

    private static final String AUTHORIZATION_HEADER_NAME = "FIB-X-AUTH";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authorizationHeaderValue = request.getHeader(AUTHORIZATION_HEADER_NAME);

        if (authorizationHeaderValue == null || !authorizationHeaderValue.equals(apiKey)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized access: Invalid API Key");
            return false;
        }
        return true;
    }
}
