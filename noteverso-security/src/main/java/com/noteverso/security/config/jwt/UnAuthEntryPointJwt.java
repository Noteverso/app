package com.noteverso.security.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noteverso.common.api.model.ErrorDetail;
import com.noteverso.common.api.model.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class UnAuthEntryPointJwt implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setCode(HttpServletResponse.SC_UNAUTHORIZED);
        errorDetail.setMessage(authException.getMessage());
        errorDetail.setType("Unauthorized");

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError(errorDetail);
        // ObjectMapper 是 Jackson 的核心类，用于在 Java 对象和 JSON 数据之间转换
        final ObjectMapper mapper = new ObjectMapper();
        // 将 ErrorResponse 对象转换为 JSON 数据并写入到 HttpServletResponse 对象的输出流中
        mapper.writeValue(response.getOutputStream(), errorResponse);
    }

}
