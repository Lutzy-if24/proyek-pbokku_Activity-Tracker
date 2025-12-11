package org.delcom.app.services;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.delcom.app.dto.ActivityForm;
import org.delcom.app.entities.ActivityLog;
import org.delcom.app.repositories.ActivityLogRepository;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class ActivityServiceTests {

    @Mock
    private ActivityLogRepository activityRepo;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private ActivityService activityService;

    // ==========================================
    // 1. Test Get All Activities
    // ==========================================
    @Test
    void testGetAllActivities() {
        UUID userId = UUID.randomUUID();
        List<ActivityLog> mockList = Arrays.asList(new ActivityLog(), new ActivityLog());

        when(activityRepo.findByUserId(userId)).thenReturn(mockList);

        List<ActivityLog> result = activityService.getAllActivities(userId);

        assertEquals(2, result.size());
        verify(activityRepo).findByUserId(userId);
    }

    // ==========================================
    // 2. Test Create Activity (Lengkap)
    // ==========================================
    @Test
    void testCreateActivity_FullData() {
        UUID userId = UUID.randomUUID();
        
        ActivityForm form = new ActivityForm();
        form.setSteps(5000);
        form.setDate("2023-10-10");
        
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false); // File Ada
        form.setProofImage(mockFile);

        when(fileStorageService.storeFile(mockFile)).thenReturn("file.jpg");
        when(activityRepo.save(any(ActivityLog.class))).thenAnswer(i -> i.getArgument(0));

        ActivityLog result = activityService.createActivity(userId, form);

        assertEquals(LocalDate.of(2023, 10, 10), result.getDate());
        assertEquals("file.jpg", result.getProofImage());
        verify(fileStorageService).storeFile(mockFile);
    }

    // --- TEST BARU: Create dengan NULL values (Untuk Branch Coverage) ---
    @Test
    void testCreateActivity_WithNulls() {
        UUID userId = UUID.randomUUID();
        ActivityForm form = new ActivityForm();
        form.setSteps(100);
        
        // SET EKSPLISIT NULL
        form.setDate(null); 
        form.setProofImage(null);

        when(activityRepo.save(any(ActivityLog.class))).thenAnswer(i -> i.getArgument(0));

        ActivityLog result = activityService.createActivity(userId, form);

        // Assert: Masuk ke blok else (LocalDate.now) dan skip upload
        assertEquals(LocalDate.now(), result.getDate());
        assertNull(result.getProofImage());
        verifyNoInteractions(fileStorageService);
    }

    // --- TEST BARU: Create dengan EMPTY values (Untuk Branch Coverage) ---
    @Test
    void testCreateActivity_WithEmptyValues() {
        UUID userId = UUID.randomUUID();
        ActivityForm form = new ActivityForm();
        form.setSteps(100);
        
        // SET EMPTY STRING
        form.setDate(""); 
        
        // Mock File tapi isEmpty = true
        MultipartFile emptyFile = mock(MultipartFile.class);
        when(emptyFile.isEmpty()).thenReturn(true);
        form.setProofImage(emptyFile);

        when(activityRepo.save(any(ActivityLog.class))).thenAnswer(i -> i.getArgument(0));

        ActivityLog result = activityService.createActivity(userId, form);

        // Assert: Masuk ke blok else date dan skip upload image
        assertEquals(LocalDate.now(), result.getDate());
        assertNull(result.getProofImage());
        verify(fileStorageService, never()).storeFile(any());
    }

    // ==========================================
    // 3. Test Update Activity
    // ==========================================

    @Test
    void testUpdateActivity_Success_FullUpdate() {
        UUID activityId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        
        ActivityLog existingLog = new ActivityLog();
        existingLog.setId(activityId);
        existingLog.setDate(LocalDate.of(2000, 1, 1));

        ActivityForm form = new ActivityForm();
        form.setDate("2025-12-31");
        form.setSteps(9999);
        
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);
        form.setProofImage(mockFile);

        when(activityRepo.findById(activityId)).thenReturn(Optional.of(existingLog));
        when(fileStorageService.storeFile(mockFile)).thenReturn("new.jpg");
        when(activityRepo.save(any(ActivityLog.class))).thenAnswer(i -> i.getArgument(0));

        ActivityLog result = activityService.updateActivity(activityId, userId, form);

        assertEquals(LocalDate.of(2025, 12, 31), result.getDate());
        assertEquals("new.jpg", result.getProofImage());
    }

    // --- TEST BARU: Update dengan NULL values (Untuk Branch Coverage) ---
    @Test
    void testUpdateActivity_WithNulls() {
        UUID activityId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        
        ActivityLog existingLog = new ActivityLog();
        existingLog.setId(activityId);
        existingLog.setDate(LocalDate.of(2020, 1, 1));
        existingLog.setProofImage("old.jpg");

        ActivityForm form = new ActivityForm();
        form.setSteps(500);
        // NULL EKSPLISIT
        form.setDate(null);
        form.setProofImage(null);

        when(activityRepo.findById(activityId)).thenReturn(Optional.of(existingLog));
        when(activityRepo.save(any(ActivityLog.class))).thenAnswer(i -> i.getArgument(0));

        ActivityLog result = activityService.updateActivity(activityId, userId, form);

        // Assert: Tanggal LAMA tidak berubah, Gambar LAMA tidak berubah
        assertEquals(LocalDate.of(2020, 1, 1), result.getDate());
        assertEquals("old.jpg", result.getProofImage());
        verifyNoInteractions(fileStorageService);
    }

    // --- TEST BARU: Update dengan EMPTY values (Untuk Branch Coverage) ---
    @Test
    void testUpdateActivity_WithEmptyValues() {
        UUID activityId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        
        ActivityLog existingLog = new ActivityLog();
        existingLog.setId(activityId);
        existingLog.setDate(LocalDate.of(2020, 1, 1));

        ActivityForm form = new ActivityForm();
        form.setSteps(500);
        // EMPTY STRING
        form.setDate("");
        
        // Mock Empty File
        MultipartFile emptyFile = mock(MultipartFile.class);
        when(emptyFile.isEmpty()).thenReturn(true);
        form.setProofImage(emptyFile);

        when(activityRepo.findById(activityId)).thenReturn(Optional.of(existingLog));
        when(activityRepo.save(any(ActivityLog.class))).thenAnswer(i -> i.getArgument(0));

        ActivityLog result = activityService.updateActivity(activityId, userId, form);

        // Assert: Tidak berubah
        assertEquals(LocalDate.of(2020, 1, 1), result.getDate());
        verify(fileStorageService, never()).storeFile(any());
    }

    @Test
    void testUpdateActivity_Fail_NotFound() {
        UUID activityId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        ActivityForm form = new ActivityForm();

        when(activityRepo.findById(activityId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            activityService.updateActivity(activityId, userId, form);
        });
    }

    // ==========================================
    // 4. Test Delete Activity
    // ==========================================
    @Test
    void testDeleteActivity() {
        UUID activityId = UUID.randomUUID();
        activityService.deleteActivity(activityId);
        verify(activityRepo, times(1)).deleteById(activityId);
    }
}