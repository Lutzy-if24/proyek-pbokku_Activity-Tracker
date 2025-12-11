package org.delcom.app.services;

import org.delcom.app.entities.AuthToken;
import org.delcom.app.repositories.AuthTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthTokenServiceTest {

    @Mock
    private AuthTokenRepository authTokenRepository;

    @InjectMocks
    private AuthTokenService authTokenService;

    @Test
    void testFindUserToken() {
        // 1. Arrange
        UUID userId = UUID.randomUUID();
        String tokenStr = "sample-jwt-token";
        AuthToken mockToken = new AuthToken(userId, tokenStr);

        // Mock repo agar mengembalikan object token saat dicari
        when(authTokenRepository.findUserToken(userId, tokenStr)).thenReturn(mockToken);

        // 2. Act
        AuthToken result = authTokenService.findUserToken(userId, tokenStr);

        // 3. Assert
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(tokenStr, result.getToken());
        
        // Verifikasi repo dipanggil dengan parameter yang benar
        verify(authTokenRepository).findUserToken(userId, tokenStr);
    }

    @Test
    void testCreateAuthToken() {
        // 1. Arrange
        UUID userId = UUID.randomUUID();
        AuthToken newToken = new AuthToken(userId, "new-token");

        // Mock repo save
        when(authTokenRepository.save(any(AuthToken.class))).thenReturn(newToken);

        // 2. Act
        AuthToken result = authTokenService.createAuthToken(newToken);

        // 3. Assert
        assertNotNull(result);
        assertEquals("new-token", result.getToken());
        verify(authTokenRepository).save(newToken);
    }

    @Test
    void testDeleteAuthToken() {
        // 1. Arrange
        UUID userId = UUID.randomUUID();

        // 2. Act
        authTokenService.deleteAuthToken(userId);

        // 3. Assert
        // Verifikasi bahwa method delete di repo dipanggil 1 kali
        verify(authTokenRepository, times(1)).deleteByUserId(userId);
    }
}