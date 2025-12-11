package org.delcom.app.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class AuthTokenTests {

    @Test
    void testCustomConstructor() {
        // Test constructor: new AuthToken(userId, token)
        
        // 1. Arrange
        UUID userId = UUID.randomUUID();
        String tokenStr = "eyJhbGciOiJIUzI1NiJ9.dummy.token";

        // 2. Act
        AuthToken authToken = new AuthToken(userId, tokenStr);

        // 3. Assert
        assertEquals(userId, authToken.getUserId());
        assertEquals(tokenStr, authToken.getToken());
        assertNotNull(authToken.getCreatedAt(), "CreatedAt harus otomatis terisi di constructor");
        
        // Cek apakah tanggalnya barusan dibuat (validasi sederhana)
        assertTrue(authToken.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void testGetterSetterAndDefaultConstructor() {
        // Test constructor kosong dan setter manual
        
        // 1. Arrange
        AuthToken authToken = new AuthToken();
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        // 2. Act
        authToken.setId(id);
        authToken.setUserId(userId);
        authToken.setToken("token-manual");

        // 3. Assert
        assertEquals(id, authToken.getId());
        assertEquals(userId, authToken.getUserId());
        assertEquals("token-manual", authToken.getToken());
        
        // CreatedAt harusnya masih null karena pakai default constructor dan belum dipersist
        assertNull(authToken.getCreatedAt()); 
    }

    @Test
    void testPrePersist_OnCreate() {
        // Test logika method @PrePersist protected void onCreate()
        
        // 1. Arrange
        AuthToken authToken = new AuthToken();
        
        // 2. Act
        // Kita panggil manual karena ini Unit Test (JPA tidak jalan otomatis disini)
        authToken.onCreate();

        // 3. Assert
        assertNotNull(authToken.getCreatedAt(), "onCreate() harus mengisi createdAt");
    }
}