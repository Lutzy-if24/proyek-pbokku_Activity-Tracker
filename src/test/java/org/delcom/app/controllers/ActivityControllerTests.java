package org.delcom.app.controllers;

import java.util.List;
import java.util.UUID;

import org.delcom.app.configs.ApiResponse;
import org.delcom.app.dto.ActivityForm;
import org.delcom.app.entities.ActivityLog;
import org.delcom.app.services.ActivityService;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ActivityControllerTest {

    @Mock
    private ActivityService activityService;

    @InjectMocks
    private ActivityController activityController;

    // ==========================================
    // 1. TEST GET ALL
    // ==========================================
    @Test
    void testGetAllActivities() {
        // Arrange
        ActivityLog log = new ActivityLog();
        when(activityService.getAllActivities(any(UUID.class))).thenReturn(List.of(log));

        // Act
        ResponseEntity<ApiResponse<List<ActivityLog>>> response = activityController.getAllActivities();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getData().size());
    }

    // ==========================================
    // 2. TEST CREATE
    // ==========================================
    @Test
    void testCreateActivity_Success() {
        // Arrange
        ActivityForm form = new ActivityForm();
        form.setSteps(1000); // Set steps agar lolos validasi

        ActivityLog createdLog = new ActivityLog();
        createdLog.setSteps(1000);

        when(activityService.createActivity(any(UUID.class), any(ActivityForm.class)))
            .thenReturn(createdLog);

        // Act
        ResponseEntity<ApiResponse<ActivityLog>> response = activityController.createActivity(form);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Data berhasil disimpan", response.getBody().getMessage());
    }

    @Test
    void testCreateActivity_ValidationError() {
        // Arrange
        ActivityForm form = new ActivityForm();
        form.setSteps(null); // Steps null trigger validasi controller

        // Act
        ResponseEntity<ApiResponse<ActivityLog>> response = activityController.createActivity(form);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("wajib diisi"));
    }

    @Test
    void testCreateActivity_ServiceError() {
        // Test ini untuk menghijaukan blok CATCH di createActivity
        ActivityForm form = new ActivityForm();
        form.setSteps(1000);

        // Simulasi Service Error
        when(activityService.createActivity(any(), any()))
            .thenThrow(new RuntimeException("Database Error"));

        // Act
        ResponseEntity<ApiResponse<ActivityLog>> response = activityController.createActivity(form);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode()); // Expect 500
        assertTrue(response.getBody().getMessage().contains("Gagal simpan data"));
    }

    // ==========================================
    // 3. TEST UPDATE (INI YANG MEMBUAT MERAH JADI HIJAU)
    // ==========================================
    
    @Test
    void testUpdateActivity_Success() {
        // Arrange
        UUID id = UUID.randomUUID();
        ActivityForm form = new ActivityForm();
        form.setSteps(5000);

        ActivityLog updatedLog = new ActivityLog();
        updatedLog.setId(id);
        updatedLog.setSteps(5000);

        // Mock service return data
        when(activityService.updateActivity(eq(id), any(UUID.class), any(ActivityForm.class)))
            .thenReturn(updatedLog);

        // Act
        ResponseEntity<ApiResponse<ActivityLog>> response = activityController.updateActivity(id, form);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Data berhasil diupdate", response.getBody().getMessage());
        assertNotNull(response.getBody().getData());
    }

    @Test
    void testUpdateActivity_ServiceError() {
        // Test ini untuk menghijaukan blok CATCH di updateActivity
        UUID id = UUID.randomUUID();
        ActivityForm form = new ActivityForm();

        // Simulasi Error (misal ID tidak ketemu atau DB mati)
        when(activityService.updateActivity(any(), any(), any()))
            .thenThrow(new RuntimeException("Activity not found"));

        // Act
        ResponseEntity<ApiResponse<ActivityLog>> response = activityController.updateActivity(id, form);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode()); // Expect 500
        assertTrue(response.getBody().getMessage().contains("Gagal update data"));
    }

    // ==========================================
    // 4. TEST DELETE
    // ==========================================
    @Test
    void testDeleteActivity() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        ResponseEntity<ApiResponse<Void>> response = activityController.deleteActivity(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(activityService).deleteActivity(id);
    }
}