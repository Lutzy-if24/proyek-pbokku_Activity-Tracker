package org.delcom.app.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

class ActivityLogTests {

    @Test
    void testGetterSetterAndNoArgsConstructor() {
        // 1. Arrange
        ActivityLog log = new ActivityLog();
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String proofImage = "bukti.jpg";

        // 2. Act
        log.setId(id);
        log.setUserId(userId);
        log.setSteps(5000);
        log.setCalories(200);
        log.setDistanceKm(3.5);
        log.setActiveMinutes(45);
        log.setProofImage(proofImage);

        // 3. Assert
        assertEquals(id, log.getId());
        assertEquals(userId, log.getUserId());
        assertEquals(5000, log.getSteps());
        assertEquals(proofImage, log.getProofImage());
    }

    @Test
    void testPrePersist_OnCreate() {
        // Test logika method onCreate()
        
        // 1. Arrange
        ActivityLog log = new ActivityLog();
        // Biarkan date null untuk mengetes default value

        // 2. Act
        // Karena ini Unit Test (bukan Integration Test dengan DB),
        // kita panggil method protected ini secara manual.
        // Bisa dipanggil karena Test class berada di package yang sama (org.delcom.app.entities)
        log.onCreate();

        // 3. Assert
        assertNotNull(log.getCreatedAt(), "CreatedAt harus terisi otomatis");
        assertNotNull(log.getUpdatedAt(), "UpdatedAt harus terisi otomatis");
        assertNotNull(log.getDate(), "Date harus default ke hari ini jika null");
        assertEquals(LocalDate.now(), log.getDate());
    }

    @Test
    void testPrePersist_OnCreate_WithExistingDate() {
        // Test jika tanggal sudah diisi manual, jangan ditimpa
        
        // 1. Arrange
        ActivityLog log = new ActivityLog();
        LocalDate customDate = LocalDate.of(2020, 1, 1);
        log.setDate(customDate);

        // 2. Act
        log.onCreate();

        // 3. Assert
        assertEquals(customDate, log.getDate(), "Tanggal manual tidak boleh berubah");
        assertNotNull(log.getCreatedAt());
    }

    @Test
    void testPreUpdate_OnUpdate() throws InterruptedException {
        // Test logika method onUpdate()
        
        // 1. Arrange
        ActivityLog log = new ActivityLog();
        
        // 2. Act
        log.onUpdate();

        // 3. Assert
        assertNotNull(log.getUpdatedAt(), "UpdatedAt harus terisi saat update");
    }

    @Test
    void testAllArgsConstructor() {
        // Test Constructor Lombok @AllArgsConstructor
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        LocalDate now = LocalDate.now();
        LocalDateTime time = LocalDateTime.now();

        ActivityLog log = new ActivityLog(
            id, 
            userId, 
            now, 
            100, 
            10, 
            50, 
            1.0, 
            "img.jpg", 
            time, 
            time
        );

        assertNotNull(log);
        assertEquals(id, log.getId());
        assertEquals(100, log.getSteps());
    }
}