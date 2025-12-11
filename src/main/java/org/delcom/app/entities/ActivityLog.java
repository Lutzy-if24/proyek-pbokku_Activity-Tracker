package org.delcom.app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "activity_logs") // Nama tabel di database nanti
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLog {

    // --- 1. ATRIBUT UTAMA (Syarat Wajib PDF) ---
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId; // Penanda data ini milik siapa

    // --- 2. DATA AKTIVITAS (Sesuai Screenshot HP kamu) ---
    // Kita butuh minimal 4 tambahan biar total > 8 atribut
    
    @Column(nullable = false)
    private LocalDate date;        // Tanggal aktivitas (misal: 2023-12-01)

    private Integer steps;         // Jumlah Langkah (misal: 5286)
    
    private Integer activeMinutes; // Waktu Aktif (misal: 54 menit)
    
    private Integer calories;      // Kalori (misal: 191 kkal)
    
    private Double distanceKm;     // Jarak (misal: 4.06 km)

    // --- 3. ATRIBUT GAMBAR (Syarat Wajib Fitur Upload) ---
    private String proofImage;     // Menyimpan nama file gambar (misal: "bukti-123.jpg")

    // --- 4. TIMESTAMP (Syarat Wajib PDF) ---
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // --- LOGIKA OTOMATIS (Biar gak repot isi manual) ---
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.date == null) {
            this.date = LocalDate.now(); // Default tanggal hari ini
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}