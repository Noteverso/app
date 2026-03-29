package com.noteverso.core.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noteverso.core.model.request.CreateUserRequest;
import com.noteverso.core.model.request.LoginRequest;
import com.noteverso.core.security.jwt.JwtUtils;
import com.noteverso.core.service.EmailService;
import com.noteverso.core.service.UserService;
import com.noteverso.core.util.RedisUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private RedisUtils redisUtils;

    @Mock
    private EmailService emailService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        AuthController controller = new AuthController(userService, authenticationManager, jwtUtils, redisUtils, emailService);
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new com.noteverso.core.handler.GlobalExceptionHandler())
                .build();
        this.objectMapper = new ObjectMapper();
    }

    @Test
    void should_sendCaptcha_successfully() throws Exception {
        // Arrange
        String email = "test@gmail.com";
        when(userService.existsByEmail(email)).thenReturn(false);
        when(redisUtils.get(anyString())).thenReturn(null);
        when(redisUtils.hasKey(anyString())).thenReturn(false);

        // Act & Assert
        mockMvc.perform(get("/api/auth/verify-captcha")
                        .param("email", email))
                .andExpect(status().isOk())
                .andExpect(content().string("Captcha sent successfully"));

        verify(emailService).sendSimpleMessage(eq(email), anyString(), anyString());
        verify(redisUtils, times(2)).set(anyString(), anyString(), anyLong());
    }

    @Test
    void should_rejectCaptcha_whenEmailExists() throws Exception {
        // Arrange
        String email = "existing@example.com";
        when(userService.existsByEmail(email)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(get("/api/auth/verify-captcha")
                        .param("email", email))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("Email already exists"));

        verify(emailService, never()).sendSimpleMessage(anyString(), anyString(), anyString());
    }

    @Test
    void should_rejectCaptcha_whenRateLimited() throws Exception {
        // Arrange
        String email = "test@gmail.com";
        when(userService.existsByEmail(email)).thenReturn(false);
        when(redisUtils.get("captcha:verification:" + email + ":create_time"))
                .thenReturn(String.valueOf(System.currentTimeMillis()));

        // Act & Assert
        mockMvc.perform(get("/api/auth/verify-captcha")
                        .param("email", email))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("Please try again in 1 minute"));

        verify(emailService, never()).sendSimpleMessage(anyString(), anyString(), anyString());
    }

    @Test
    void should_signup_successfully() throws Exception {
        // Arrange
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("newuser@example.com");
        request.setUsername("newuser");
        request.setPassword("Admin123456");
        request.setCaptchaCode("123456");

        when(userService.existsByEmail(request.getEmail())).thenReturn(false);
        when(redisUtils.get("captcha:verification:" + request.getEmail() + ":code"))
                .thenReturn("123456");

        // Act & Assert
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("User created successfully"));

        verify(userService).createUser(request.getEmail(), request.getUsername(), request.getPassword());
        verify(redisUtils).del("captcha:verification:" + request.getEmail() + ":code");
    }

    @Test
    void should_rejectSignup_whenEmailExists() throws Exception {
        // Arrange
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("existing@example.com");
        request.setUsername("user");
        request.setPassword("Admin123456");
        request.setCaptchaCode("123456");

        when(userService.existsByEmail(request.getEmail())).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("Email already exists"));

        verify(userService, never()).createUser(anyString(), anyString(), anyString());
    }

    @Test
    void should_rejectSignup_whenInvalidCaptcha() throws Exception {
        // Arrange
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("test@gmail.com");
        request.setUsername("user");
        request.setPassword("Admin123456");
        request.setCaptchaCode("000000");

        when(userService.existsByEmail(request.getEmail())).thenReturn(false);
        when(redisUtils.get("captcha:verification:" + request.getEmail() + ":code"))
                .thenReturn("123456");

        // Act & Assert
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("Invalid captcha code"));

        verify(userService, never()).createUser(anyString(), anyString(), anyString());
    }

    @Test
    void should_rejectSignup_whenPasswordTooShort() throws Exception {
        // Arrange
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("test@gmail.com");
        request.setUsername("user");
        request.setPassword("pass");
        request.setCaptchaCode("123456");

        // Act & Assert
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").exists());

        verify(userService, never()).createUser(anyString(), anyString(), anyString());
    }

    @Test
    void should_login_successfully_with_email() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("Admin123456");

        // Mock authentication
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtUtils.generateJwtToken(any())).thenReturn("mock-jwt-token");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt-token"))
                .andExpect(jsonPath("$.email").value("user@example.com"));
    }
}