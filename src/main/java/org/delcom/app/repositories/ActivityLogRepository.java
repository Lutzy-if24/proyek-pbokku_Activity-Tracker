package org.delcom.app.repositories;

import java.util.List;
import java.util.UUID;

import org.delcom.app.entities.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, UUID> {
    
    // Fitur 1: Mengambil semua aktivitas milik user tertentu
    // (Biar data User A tidak dilihat User B)
    List<ActivityLog> findByUserId(UUID userId);

    // Fitur 2 (Opsional tapi berguna): Mengambil data urut dari tanggal terbaru
    // Berguna buat ditampilkan di halaman dashboard paling atas
    List<ActivityLog> findByUserIdOrderByDateDesc(UUID userId);
}