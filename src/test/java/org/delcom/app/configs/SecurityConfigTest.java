package org.delcom.app.configs;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

// KITA TAMBAHKAN PROPERTI H2 DISINI AGAR TIDAK MENCARI POSTGRESQL
@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    "spring.datasource.driverClassName=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.sql.init.mode=never"
})
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