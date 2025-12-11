package org.delcom.app.dto;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class LoginFormTests {

    private static Validator validator;

    // Menyiapkan validator sebelum semua test berjalan
    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // 1. Test Getter & Setter Biasa
    @Test
    void testSettersAndGetters() {
        LoginForm form = new LoginForm();
        
        form.setEmail("test@del.ac.id");
        form.setPassword("rahasia123");
        form.setRememberMe(true);

        assertEquals("test@del.ac.id", form.getEmail());
        assertEquals("rahasia123", form.getPassword());
        assertTrue(form.isRememberMe());
    }

    // 2. Test Validasi: Jika data benar
    @Test
    void testValidation_Success() {
        LoginForm form = new LoginForm();
        form.setEmail("valid@email.com");
        form.setPassword("password123");

        // Validate
        Set<ConstraintViolation<LoginForm>> violations = validator.validate(form);

        // Harusnya tidak ada error
        assertTrue(violations.isEmpty(), "Harusnya tidak ada error validasi");
    }

    // 3. Test Validasi: Jika Email & Password Kosong
    @Test
    void testValidation_Fail_BlankFields() {
        LoginForm form = new LoginForm();
        form.setEmail("");   // Kosong
        form.setPassword(""); // Kosong

        Set<ConstraintViolation<LoginForm>> violations = validator.validate(form);

        // Harusnya ada 2 error (Email kosong & Password kosong)
        assertFalse(violations.isEmpty());
        assertEquals(2, violations.size());
    }

    // 4. Test Validasi: Jika Format Email Salah
    @Test
    void testValidation_Fail_InvalidEmail() {
        LoginForm form = new LoginForm();
        form.setEmail("bukan-email"); // Tidak ada @ atau domain
        form.setPassword("password123");

        Set<ConstraintViolation<LoginForm>> violations = validator.validate(form);

        // Harusnya ada 1 error (Format email)
        assertFalse(violations.isEmpty());
        
        // Cek pesan errornya (Opsional)
        boolean hasEmailError = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Format email tidak valid"));
        assertTrue(hasEmailError, "Pesan error email harus sesuai");
    }
}