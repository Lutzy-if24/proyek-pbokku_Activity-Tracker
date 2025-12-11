package org.delcom.app.configs;

import org.delcom.app.entities.User;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class AuthContextTests {

    @Test
    void testDefaultState() {
        // 1. Saat objek baru dibuat, user harusnya null
        AuthContext authContext = new AuthContext();

        assertNull(authContext.getAuthUser(), "User awal harus null");
        assertFalse(authContext.isAuthenticated(), "Harusnya belum terautentikasi");
    }

    @Test
    void testSetAuthUserAndIsAuthenticated() {
        // 1. Arrange
        AuthContext authContext = new AuthContext();
        User user = new User(); // Pastikan class User punya constructor kosong (default)
      
        // 2. Act
        authContext.setAuthUser(user);

        // 3. Assert
        assertNotNull(authContext.getAuthUser(), "User tidak boleh null setelah di-set");
        assertEquals(user, authContext.getAuthUser(), "Object user harus sama dengan yang di-set");
        assertTrue(authContext.isAuthenticated(), "Harusnya return true jika user ada");
    }

    @Test
    void testLogoutOrClearUser() {
        // 1. Arrange
        AuthContext authContext = new AuthContext();
        authContext.setAuthUser(new User());
        assertTrue(authContext.isAuthenticated());

        // 2. Act (Simulasi logout/clear context)
        authContext.setAuthUser(null);

        // 3. Assert
        assertNull(authContext.getAuthUser());
        assertFalse(authContext.isAuthenticated(), "Harusnya false setelah user di-null-kan");
    }
}