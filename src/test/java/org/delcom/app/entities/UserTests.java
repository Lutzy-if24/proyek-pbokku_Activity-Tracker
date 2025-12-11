package org.delcom.app.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class UserTests {

    @Test
    void testConstructors() {
        // 1. Test Constructor 3 Parameter (Name, Email, Pass)
        User userFull = new User("Budi", "budi@del.ac.id", "pass123");
        assertEquals("Budi", userFull.getName());
        assertEquals("budi@del.ac.id", userFull.getEmail());
        assertEquals("pass123", userFull.getPassword());

        // 2. Test Constructor 2 Parameter (Email, Pass) -> Name default kosong ""
        User userSimple = new User("simple@del.ac.id", "rahasia");
        assertEquals("", userSimple.getName(), "Constructor 2 param harus set name jadi empty string");
        assertEquals("simple@del.ac.id", userSimple.getEmail());
        assertEquals("rahasia", userSimple.getPassword());

        // 3. Test Constructor Kosong
        User userEmpty = new User();
        assertNull(userEmpty.getName());
    }

    @Test
    void testGettersAndSetters() {
        // 1. Arrange
        User user = new User();
        UUID id = UUID.randomUUID();
        String name = "Mahasiswa";
        String email = "mhs@del.ac.id";
        String password = "hashedPassword";

        // 2. Act
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);

        // 3. Assert
        assertEquals(id, user.getId());
        assertEquals(name, user.getName());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
    }

    @Test
    void testPrePersist_OnCreate() {
        // Test method @PrePersist protected void onCreate()
        
        User user = new User();
        
        // Panggil manual (karena Unit Test tidak pakai Hibernate/DB)
        user.onCreate();

        assertNotNull(user.getCreatedAt(), "CreatedAt harus terisi");
        assertNotNull(user.getUpdatedAt(), "UpdatedAt harus terisi");
        
        // Pastikan waktunya baru saja dibuat
        assertTrue(user.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void testPreUpdate_OnUpdate() {
        // Test method @PreUpdate protected void onUpdate()
        
        User user = new User();
        user.onCreate(); // Init awal
        
        LocalDateTime createdTime = user.getCreatedAt();

        // Simulasi delay sedikit (biar waktu update beda)
        // user.onUpdate(); -> update 'updatedAt' saja

        user.onUpdate();

        assertNotNull(user.getUpdatedAt());
        // createdAt tidak boleh berubah saat update
        assertEquals(createdTime, user.getCreatedAt());
    }
}