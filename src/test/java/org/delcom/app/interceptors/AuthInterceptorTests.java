package org.delcom.app.interceptors;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.UUID;

import org.delcom.app.configs.AuthContext;
import org.delcom.app.entities.AuthToken;
import org.delcom.app.entities.User;
import org.delcom.app.services.AuthTokenService;
import org.delcom.app.services.UserService;
import org.delcom.app.utils.JwtUtil;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class AuthInterceptorTest {

    @Mock
    private AuthContext authContext;

    @Mock
    private AuthTokenService authTokenService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthInterceptor authInterceptor;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @BeforeEach
    void setUp() throws Exception {
        // Menggunakan lenient() agar tidak error jika test case tertentu tidak memanggil getWriter()
        lenient().when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));
        
        ReflectionTestUtils.setField(authInterceptor, "authContext", authContext);
        ReflectionTestUtils.setField(authInterceptor, "authTokenService", authTokenService);
        ReflectionTestUtils.setField(authInterceptor, "userService", userService);
    }

    // 1. Test Endpoint Public: /api/auth
    @Test
    void testPreHandle_PublicEndpoint_Auth() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/auth/login");
        boolean result = authInterceptor.preHandle(request, response, new Object());
        assertTrue(result);
    }

    // --- TEST BARU: Endpoint Error (Untuk menghijaukan Wajik Kuning) ---
    @Test
    void testPreHandle_PublicEndpoint_Error() throws Exception {
        when(request.getRequestURI()).thenReturn("/error");
        boolean result = authInterceptor.preHandle(request, response, new Object());
        assertTrue(result);
    }
    // -------------------------------------------------------------------

    // 2. Test Sukses
    @Test
    void testPreHandle_Success() throws Exception {
        UUID userId = UUID.randomUUID();
        when(request.getRequestURI()).thenReturn("/api/activities");
        when(request.getHeader("Authorization")).thenReturn("Bearer valid_token");

        try (MockedStatic<JwtUtil> jwtMock = mockStatic(JwtUtil.class)) {
            jwtMock.when(() -> JwtUtil.validateToken(anyString(), anyBoolean())).thenReturn(true);
            jwtMock.when(() -> JwtUtil.extractUserId(anyString())).thenReturn(userId);

            AuthToken validToken = new AuthToken();
            validToken.setUserId(userId); 
            
            when(authTokenService.findUserToken(eq(userId), anyString())).thenReturn(validToken);
            when(userService.getUserById(userId)).thenReturn(new User());

            boolean result = authInterceptor.preHandle(request, response, new Object());

            assertTrue(result);
            verify(authContext).setAuthUser(any(User.class));
        }
    }

    // 3. Test Fail - Token Kosong (Null)
    @Test
    void testPreHandle_Fail_TokenEmpty() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/activities");
        when(request.getHeader("Authorization")).thenReturn(null);

        boolean result = authInterceptor.preHandle(request, response, new Object());

        assertFalse(result);
        verify(response).setStatus(401);
    }

    // --- TEST BARU: Token Ada tapi Salah Format (Bukan Bearer) ---
    @Test
    void testPreHandle_Fail_TokenNotBearer() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/activities");
        // Header ada, tapi formatnya Basic, bukan Bearer
        when(request.getHeader("Authorization")).thenReturn("Basic dXNlcjpwYXNz");

        boolean result = authInterceptor.preHandle(request, response, new Object());

        assertFalse(result);
        verify(response).setStatus(401);
    }
    // -------------------------------------------------------------

    // 4. Test Fail - Token Invalid Format (Jwt Validation False)
    @Test
    void testPreHandle_Fail_TokenInvalidFormat() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/activities");
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid_token");

        try (MockedStatic<JwtUtil> jwtMock = mockStatic(JwtUtil.class)) {
            jwtMock.when(() -> JwtUtil.validateToken(anyString(), anyBoolean())).thenReturn(false);

            boolean result = authInterceptor.preHandle(request, response, new Object());

            assertFalse(result);
            verify(response).setStatus(401);
        }
    }

    // 5. Test Fail - UserId Null (Jwt Extraction Fail)
    @Test
    void testPreHandle_Fail_UserIdNull() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/activities");
        when(request.getHeader("Authorization")).thenReturn("Bearer weird_token");

        try (MockedStatic<JwtUtil> jwtMock = mockStatic(JwtUtil.class)) {
            jwtMock.when(() -> JwtUtil.validateToken(anyString(), anyBoolean())).thenReturn(true);
            jwtMock.when(() -> JwtUtil.extractUserId(anyString())).thenReturn(null);

            boolean result = authInterceptor.preHandle(request, response, new Object());

            assertFalse(result);
            verify(response).setStatus(401); 
        }
    }

    // 6. Test Fail - User Not Found In DB
    @Test
    void testPreHandle_Fail_UserNotFoundInDB() throws Exception {
        UUID userId = UUID.randomUUID();
        when(request.getRequestURI()).thenReturn("/api/activities");
        when(request.getHeader("Authorization")).thenReturn("Bearer valid_token");

        try (MockedStatic<JwtUtil> jwtMock = mockStatic(JwtUtil.class)) {
            jwtMock.when(() -> JwtUtil.validateToken(anyString(), anyBoolean())).thenReturn(true);
            jwtMock.when(() -> JwtUtil.extractUserId(anyString())).thenReturn(userId);

            AuthToken validToken = new AuthToken();
            validToken.setUserId(userId);

            when(authTokenService.findUserToken(eq(userId), anyString())).thenReturn(validToken);
            
            // User tidak ditemukan (Null)
            when(userService.getUserById(userId)).thenReturn(null);

            boolean result = authInterceptor.preHandle(request, response, new Object());

            assertFalse(result);
            verify(response).setStatus(404);
        }
    }
    
    // 7. Test Fail - Token Expired/Logout (Tidak ada di DB)
    @Test
    void testPreHandle_Fail_TokenNotFoundInDB() throws Exception {
        UUID userId = UUID.randomUUID();
        when(request.getRequestURI()).thenReturn("/api/activities");
        when(request.getHeader("Authorization")).thenReturn("Bearer expired_token");

        try (MockedStatic<JwtUtil> jwtMock = mockStatic(JwtUtil.class)) {
            jwtMock.when(() -> JwtUtil.validateToken(anyString(), anyBoolean())).thenReturn(true);
            jwtMock.when(() -> JwtUtil.extractUserId(anyString())).thenReturn(userId);

            // Token tidak ada di DB
            when(authTokenService.findUserToken(eq(userId), anyString())).thenReturn(null);

            boolean result = authInterceptor.preHandle(request, response, new Object());

            assertFalse(result);
            verify(response).setStatus(401);
        }
    }
       @Test
    void testPreHandle_Fail_TokenJustPrefix() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/activities");
        
        // Header cuma "Bearer " (panjang 7 char), jadi substring(7) hasilnya "" (Empty String)
        when(request.getHeader("Authorization")).thenReturn("Bearer ");

        boolean result = authInterceptor.preHandle(request, response, new Object());

        // Hasilnya harus false karena token kosong
        assertFalse(result);
        verify(response).setStatus(401);
    }
}