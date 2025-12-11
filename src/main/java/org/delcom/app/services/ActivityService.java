package org.delcom.app.services;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.delcom.app.dto.ActivityForm;
import org.delcom.app.entities.ActivityLog;
import org.delcom.app.repositories.ActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActivityService {

    @Autowired
    private ActivityLogRepository activityRepo;

    @Autowired
    private FileStorageService fileStorageService;

    // 1. Ambil Semua Data Activity milik User
    public List<ActivityLog> getAllActivities(UUID userId) {
        return activityRepo.findByUserId(userId);
    }

    // 2. Tambah Data Activity Baru (+ Upload Gambar)
    public ActivityLog createActivity(UUID userId, ActivityForm form) {
        ActivityLog activity = new ActivityLog();
        activity.setUserId(userId);
        
        // Convert Date dari String (HTML) ke LocalDate (Java)
        if (form.getDate() != null && !form.getDate().isEmpty()) {
            activity.setDate(LocalDate.parse(form.getDate()));
        } else {
            activity.setDate(LocalDate.now());
        }

        activity.setSteps(form.getSteps());
        activity.setCalories(form.getCalories());
        activity.setDistanceKm(form.getDistanceKm());
        activity.setActiveMinutes(form.getActiveMinutes());

        // LOGIKA UPLOAD GAMBAR BARU
        if (form.getProofImage() != null && !form.getProofImage().isEmpty()) {
            String filename = fileStorageService.storeFile(form.getProofImage());
            activity.setProofImage(filename); // Simpan nama file di database
        }

        return activityRepo.save(activity);
    }

    // --- FITUR BARU: 3. Update Data ---
    public ActivityLog updateActivity(UUID id, UUID userId, ActivityForm form) {
        // A. Cari data lama berdasarkan ID
        ActivityLog activity = activityRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Activity not found with id: " + id));

        // B. Update data dasar
        if (form.getDate() != null && !form.getDate().isEmpty()) {
            activity.setDate(LocalDate.parse(form.getDate()));
        }
        activity.setSteps(form.getSteps());
        activity.setCalories(form.getCalories());
        activity.setDistanceKm(form.getDistanceKm());
        activity.setActiveMinutes(form.getActiveMinutes());

        // C. Cek Upload Foto: Hanya ganti jika user upload file baru
        if (form.getProofImage() != null && !form.getProofImage().isEmpty()) {
            // Simpan foto baru
            String newFilename = fileStorageService.storeFile(form.getProofImage());
            // Update nama file di database
            activity.setProofImage(newFilename);
        }
        // Jika form.getProofImage() kosong, berarti user tidak ganti foto (pakai foto lama)

        // D. Simpan perubahan
        return activityRepo.save(activity);
    }

    // 4. Hapus Data
    public void deleteActivity(UUID activityId) {
        activityRepo.deleteById(activityId);
    }
}