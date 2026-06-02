package com.universidad.kardex_backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SchemaFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        try {

            System.out.println("===== SCHEMA FILTER =====");
            System.out.println("URI: " + request.getRequestURI());

            String schema = request.getHeader("X-Schema");

            System.out.println("Header X-Schema = " + schema);

            if (schema != null && !schema.isBlank()) {
                SchemaInterceptor.setCurrentSchema(schema);
                System.out.println(
                        "Schema guardado = " +
                                SchemaInterceptor.getCurrentSchema());
            }

            filterChain.doFilter(request, response);

        } finally {
            SchemaInterceptor.clear();
        }
    }
}