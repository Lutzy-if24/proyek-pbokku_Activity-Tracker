package org.delcom.app.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.delcom.app.configs.ApiResponse;
import org.delcom.app.configs.AuthContext;
import org.delcom.app.entities.AuthToken;
import org.delcom.app.entities.User;
import org.delcom.app.services.AuthTokenService;
import org.delcom.app.services.UserService;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private AuthTokenService authTokenService;

    @Mock
    private AuthContext authContext;

    @InjectMocks
    private UserController userController;

    private User mockUser;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(UUID.randomUUID());
        mockUser.setName("Test User");
        mockUser.setEmail("test@example.com");
        mockUser.setPassword(encoder.encode("password123")); 

        ReflectionTestUtils.setField(userController, "authContext", authContext);
    }

    // ==========================================
    // 1. TEST REGISTER (Exhaustive Branches)
    // ==========================================

    @Test
    void testRegister_Success() {
        User reqUser = new User("New User", "new@example.com", "pass");
        when(userService.getUserByEmail(anyString())).thenReturn(null);
        when(userService.createUser(anyString(), anyString(), anyString())).thenReturn(mockUser);

        ResponseEntity<ApiResponse<Map<String, UUID>>> response = userController.registerUser(reqUser);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    // --- VALIDASI NAME ---
    @Test
    void testRegister_Fail_NameNull() {
        User reqUser = new User(null, "valid@email.com", "pass");
        ResponseEntity<ApiResponse<Map<String, UUID>>> response = userController.registerUser(reqUser);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("nama"));
    }

    @Test
    void testRegister_Fail_NameEmpty() {
        User reqUser = new User("", "valid@email.com", "pass");
        ResponseEntity<ApiResponse<Map<String, UUID>>> response = userController.registerUser(reqUser);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("nama"));
    }

    // --- VALIDASI EMAIL (Name harus valid dulu) ---
    @Test
    void testRegister_Fail_EmailNull() {
        User reqUser = new User("Valid Name", null, "pass");
        ResponseEntity<ApiResponse<Map<String, UUID>>> response = userController.registerUser(reqUser);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("email"));
    }

    @Test
    void testRegister_Fail_EmailEmpty() {
        User reqUser = new User("Valid Name", "", "pass");
        ResponseEntity<ApiResponse<Map<String, UUID>>> response = userController.registerUser(reqUser);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("email"));
    }

    // --- VALIDASI PASSWORD (Name & Email harus valid dulu) ---
    @Test
    void testRegister_Fail_PassNull() {
        User reqUser = new User("Valid Name", "valid@email.com", null);
        ResponseEntity<ApiResponse<Map<String, UUID>>> response = userController.registerUser(reqUser);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("password"));
    }

    @Test
    void testRegister_Fail_PassEmpty() {
        User reqUser = new User("Valid Name", "valid@email.com", "");
        ResponseEntity<ApiResponse<Map<String, UUID>>> response = userController.registerUser(reqUser);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("password"));
    }

    @Test
    void testRegister_Fail_EmailExists() {
        User reqUser = new User("User", "exist@example.com", "pass");
        when(userService.getUserByEmail("exist@example.com")).thenReturn(mockUser);

        ResponseEntity<ApiResponse<Map<String, UUID>>> response = userController.registerUser(reqUser);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // ==========================================
    // 2. TEST LOGIN (Exhaustive Branches)
    // ==========================================

    @Test
    void testLogin_Success() {
        User reqUser = new User(null, "test@example.com", "password123"); 
        when(userService.getUserByEmail("test@example.com")).thenReturn(mockUser);
        when(authTokenService.createAuthToken(any(AuthToken.class))).thenReturn(new AuthToken());

        ResponseEntity<ApiResponse<Map<String, String>>> response = userController.loginUser(reqUser);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    // --- VALIDASI EMAIL ---
    @Test
    void testLogin_Fail_EmailNull() {
        User reqUser = new User(null, null, "pass");
        ResponseEntity<ApiResponse<Map<String, String>>> response = userController.loginUser(reqUser);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testLogin_Fail_EmailEmpty() {
        User reqUser = new User(null, "", "pass");
        ResponseEntity<ApiResponse<Map<String, String>>> response = userController.loginUser(reqUser);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // --- VALIDASI PASSWORD (Email harus valid dulu) ---
    @Test
    void testLogin_Fail_PassNull() {
        User reqUser = new User(null, "valid@email.com", null);
        ResponseEntity<ApiResponse<Map<String, String>>> response = userController.loginUser(reqUser);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testLogin_Fail_PassEmpty() {
        User reqUser = new User(null, "valid@email.com", "");
        ResponseEntity<ApiResponse<Map<String, String>>> response = userController.loginUser(reqUser);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // --- LOGIC LAINNYA ---
    @Test
    void testLogin_Success_OverwriteToken() {
        User reqUser = new User(null, "test@example.com", "password123");
        when(userService.getUserByEmail("test@example.com")).thenReturn(mockUser);
        
        // Token lama ada
        when(authTokenService.findUserToken(eq(mockUser.getId()), anyString())).thenReturn(new AuthToken());
        when(authTokenService.createAuthToken(any(AuthToken.class))).thenReturn(new AuthToken());

        userController.loginUser(reqUser);
        verify(authTokenService).deleteAuthToken(mockUser.getId());
    }

    @Test
    void testLogin_Fail_UserNotFound() {
        User reqUser = new User(null, "unknown@example.com", "pass");
        when(userService.getUserByEmail(anyString())).thenReturn(null);
        ResponseEntity<ApiResponse<Map<String, String>>> response = userController.loginUser(reqUser);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testLogin_Fail_WrongPassword() {
        User reqUser = new User(null, "test@example.com", "WRONG");
        when(userService.getUserByEmail("test@example.com")).thenReturn(mockUser);
        ResponseEntity<ApiResponse<Map<String, String>>> response = userController.loginUser(reqUser);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testLogin_Fail_TokenError() {
        User reqUser = new User(null, "test@example.com", "password123");
        when(userService.getUserByEmail("test@example.com")).thenReturn(mockUser);
        when(authTokenService.createAuthToken(any())).thenReturn(null);
        
        ResponseEntity<ApiResponse<Map<String, String>>> response = userController.loginUser(reqUser);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    // ==========================================
    // 3. TEST UPDATE USER (Exhaustive Branches)
    // ==========================================

    @Test
    void testUpdateUser_Success() {
        User reqUpdate = new User("New Name", "new@email.com", null);
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        when(userService.updateUser(any(), anyString(), anyString())).thenReturn(mockUser);

        ResponseEntity<ApiResponse<User>> response = userController.updateUser(reqUpdate);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    // --- VALIDASI NAME ---
    @Test
    void testUpdateUser_Fail_NameNull() {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        
        User reqUpdate = new User(null, "valid@email.com", null);
        ResponseEntity<ApiResponse<User>> response = userController.updateUser(reqUpdate);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("nama"));
    }

    @Test
    void testUpdateUser_Fail_NameEmpty() {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        
        User reqUpdate = new User("", "valid@email.com", null);
        ResponseEntity<ApiResponse<User>> response = userController.updateUser(reqUpdate);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("nama"));
    }

    // --- VALIDASI EMAIL (Name harus valid) ---
    @Test
    void testUpdateUser_Fail_EmailNull() {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        
        User reqUpdate = new User("Valid Name", null, null);
        ResponseEntity<ApiResponse<User>> response = userController.updateUser(reqUpdate);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("email"));
    }

    @Test
    void testUpdateUser_Fail_EmailEmpty() {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        
        User reqUpdate = new User("Valid Name", "", null);
        ResponseEntity<ApiResponse<User>> response = userController.updateUser(reqUpdate);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("email"));
    }

    @Test
    void testUpdateUser_Fail_Unauthenticated() {
        when(authContext.isAuthenticated()).thenReturn(false);
        userController.updateUser(new User());
    }

    @Test
    void testUpdateUser_Fail_NotFound() {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        when(userService.updateUser(any(), anyString(), anyString())).thenReturn(null);
        
        User reqUpdate = new User("Ok", "ok@email.com", null);
        ResponseEntity<ApiResponse<User>> response = userController.updateUser(reqUpdate);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // ==========================================
    // 4. TEST UPDATE PASSWORD (Exhaustive Branches)
    // ==========================================

    @Test
    void testUpdatePassword_Success() {
        Map<String, String> payload = Map.of("password", "password123", "newPassword", "new123");
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        when(userService.updatePassword(any(), anyString())).thenReturn(mockUser);

        ResponseEntity<ApiResponse<Void>> response = userController.updateUserPassword(payload);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    // --- VALIDASI OLD PASSWORD ---
    @Test
    void testUpdatePassword_Fail_OldPassNull() {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        
        Map<String, String> payload = new HashMap<>();
        payload.put("password", null); // NULL
        payload.put("newPassword", "valid");
        
        ResponseEntity<ApiResponse<Void>> response = userController.updateUserPassword(payload);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testUpdatePassword_Fail_OldPassEmpty() {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        
        Map<String, String> payload = Map.of("password", "", "newPassword", "valid");
        
        ResponseEntity<ApiResponse<Void>> response = userController.updateUserPassword(payload);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // --- VALIDASI NEW PASSWORD (Old Pass harus valid) ---
    @Test
    void testUpdatePassword_Fail_NewPassNull() {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        
        Map<String, String> payload = new HashMap<>();
        payload.put("password", "valid");
        payload.put("newPassword", null); // NULL
        
        ResponseEntity<ApiResponse<Void>> response = userController.updateUserPassword(payload);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testUpdatePassword_Fail_NewPassEmpty() {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        
        Map<String, String> payload = Map.of("password", "valid", "newPassword", "");
        
        ResponseEntity<ApiResponse<Void>> response = userController.updateUserPassword(payload);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testUpdatePassword_Fail_Unauth() {
        when(authContext.isAuthenticated()).thenReturn(false);
        userController.updateUserPassword(Map.of());
    }
    
    @Test
    void testUpdatePassword_Fail_WrongPass() {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        
        Map<String, String> payload = Map.of("password", "WRONG", "newPassword", "new");
        ResponseEntity<ApiResponse<Void>> response = userController.updateUserPassword(payload);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @Test
    void testUpdatePassword_Fail_UserNotFound() {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        when(userService.updatePassword(any(), anyString())).thenReturn(null);
        
        Map<String, String> payload = Map.of("password", "password123", "newPassword", "new");
        ResponseEntity<ApiResponse<Void>> response = userController.updateUserPassword(payload);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetUserInfo_Success() {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        ResponseEntity<ApiResponse<Map<String, User>>> response = userController.getUserInfo();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetUserInfo_Fail() {
        when(authContext.isAuthenticated()).thenReturn(false);
        userController.getUserInfo();
    }
}