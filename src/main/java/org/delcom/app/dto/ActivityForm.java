package org.delcom.app.dto;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile; // Jangan lupa import ini

import lombok.Data; 

@Data
public class ActivityForm {
    
    // --- TAMBAHAN PENTING (Agar support Edit) ---
    private UUID id; 
    // --------------------------------------------

    // Inputan Data Angka & Tanggal
    private String date;           
    private Integer steps;
    private Integer activeMinutes;
    private Integer calories;
    private Double distanceKm;

    // Inputan File Gambar
    private MultipartFile proofImage; 
}