package org.delcom.app.configs;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
// @AutoConfigureMockMvc <-- Kita hapus dulu baris yang bikin error
class SecurityConfigTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void testPasswordEncoder() {
        // 1. Arrange
        String rawPassword = "password123";

        // 2. Act
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // 3. Assert
        assertNotNull(encodedPassword);
        assertNotEquals(rawPassword, encodedPassword, "Password tidak boleh plain text");
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword), "Password harus cocok setelah di-hash");
    }
}