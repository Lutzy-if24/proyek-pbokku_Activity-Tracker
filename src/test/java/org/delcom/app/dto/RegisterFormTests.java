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

class RegisterFormTests {

    private static Validator validator;

    // Menyiapkan validator satu kali sebelum test dimulai
    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // 1. Test Getter & Setter
    @Test
    void testSettersAndGetters() {
        RegisterForm form = new RegisterForm();
        
        String name = "Mahasiswa Del";
        String email = "mahasiswa@del.ac.id";
        String password = "passwordAman123";

        form.setName(name);
        form.setEmail(email);
        form.setPassword(password);

        assertEquals(name, form.getName());
        assertEquals(email, form.getEmail());
        assertEquals(password, form.getPassword());
    }

    // 2. Test Validasi: Skenario Sukses
    @Test
    void testValidation_Success() {
        RegisterForm form = new RegisterForm();
        form.setName("User Valid");
        form.setEmail("valid@example.com");
        form.setPassword("pass123");

        // Validasi
        Set<ConstraintViolation<RegisterForm>> violations = validator.validate(form);

        // Harusnya tidak ada error
        assertTrue(violations.isEmpty(), "Data valid tidak boleh menghasilkan error");
    }

    // 3. Test Validasi: Skenario Kolom Kosong
    @Test
    void testValidation_Fail_BlankFields() {
        RegisterForm form = new RegisterForm();
        form.setName("");     // Error 1
        form.setEmail("");    // Error 2
        form.setPassword(""); // Error 3

        Set<ConstraintViolation<RegisterForm>> violations = validator.validate(form);

        // Harusnya ada 3 error violation
        assertEquals(3, violations.size(), "Harusnya ada 3 field yang error");
    }

    // 4. Test Validasi: Skenario Email Salah Format
    @Test
    void testValidation_Fail_InvalidEmail() {
        RegisterForm form = new RegisterForm();
        form.setName("User Test");
        form.setPassword("pass123");
        form.setEmail("email-ngawur-tanpa-at"); // Format salah

        Set<ConstraintViolation<RegisterForm>> violations = validator.validate(form);

        assertFalse(violations.isEmpty());
        
        // Cek apakah pesan error-nya benar
        boolean hasEmailError = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Format email tidak valid"));
        
        assertTrue(hasEmailError, "Harus ada error message tentang format email");
    }
}