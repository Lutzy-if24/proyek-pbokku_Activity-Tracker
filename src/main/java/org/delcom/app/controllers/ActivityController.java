package org.delcom.app.controllers;

import java.util.List;
import java.util.UUID;

import org.delcom.app.configs.ApiResponse;
import org.delcom.app.dto.ActivityForm;
import org.delcom.app.entities.ActivityLog;
import org.delcom.app.services.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @Autowired
    

    // 1. GET: Ambil semua data aktivitas milik user yang login
    @GetMapping
    public ResponseEntity<ApiResponse<List<ActivityLog>>> getAllActivities() {
        // COMMENT UNTUK TESTING
        /*
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(401).body(new ApiResponse<>("fail", "Harus login dulu", null));
        }
        User user = authContext.getAuthUser();
        */
        
        // HARDCODE USER ID - Ganti dengan UUID user Anda di database
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        
        List<ActivityLog> data = activityService.getAllActivities(userId);
        return ResponseEntity.ok(new ApiResponse<>("success", "Berhasil ambil data", data));
    }

    // 2. POST: Tambah Data Baru + Upload Gambar
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ActivityLog>> createActivity(@ModelAttribute ActivityForm form) {
        
        // COMMENT UNTUK TESTING
        /*
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(401).body(new ApiResponse<>("fail", "Harus login dulu", null));
        }
        User user = authContext.getAuthUser();
        */
        
        // HARDCODE USER ID - Ganti dengan UUID user Anda di database
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000001");

        // Validasi sederhana
        if (form.getSteps() == null) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Jumlah langkah wajib diisi", null));
        }

        // Panggil Service untuk simpan data & file
        try {
            ActivityLog savedData = activityService.createActivity(userId, form);
            return ResponseEntity.ok(new ApiResponse<>("success", "Data berhasil disimpan", savedData));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse<>("error", "Gagal simpan data: " + e.getMessage(), null));
        }
    }

    // 3. DELETE: Hapus Data
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteActivity(@PathVariable UUID id) {
        // COMMENT UNTUK TESTING
        /*
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(401).body(new ApiResponse<>("fail", "Harus login dulu", null));
        }
        */

        activityService.deleteActivity(id);
        return ResponseEntity.ok(new ApiResponse<>("success", "Data berhasil dihapus", null));
    }
    // 4. PUT: Update Data (FITUR BARU)
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ActivityLog>> updateActivity(
            @PathVariable UUID id, 
            @ModelAttribute ActivityForm form) {
        
        // COMMENT UNTUK TESTING
        /*
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(401).body(new ApiResponse<>("fail", "Harus login dulu", null));
        }
        */

        // HARDCODE USER ID - Samakan dengan UUID user Anda
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000001");

        try {
            // Panggil Service Update
            // Pastikan Anda nanti membuat method 'updateActivity' ini di ActivityService
            ActivityLog updatedData = activityService.updateActivity(id, userId, form);
            
            return ResponseEntity.ok(new ApiResponse<>("success", "Data berhasil diupdate", updatedData));
        } catch (Exception e) {
            e.printStackTrace(); // Agar error terlihat di console
            return ResponseEntity.status(500).body(new ApiResponse<>("error", "Gagal update data: " + e.getMessage(), null));
        }
    }
}