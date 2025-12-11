package org.delcom.app.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    // Lokasi penyimpanan file (sesuai WebMvcConfig tadi)
    private final Path rootLocation = Paths.get(System.getProperty("user.dir") + "/src/main/resources/static/uploads");

    public FileStorageService() {
        try {
            // Buat folder uploads jika belum ada
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Gagal membuat folder upload!", e);
        }
    }

    // Fungsi utama: Menerima file -> Menyimpan -> Mengembalikan nama file acak
    public String storeFile(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Gagal menyimpan file kosong.");
            }

            // Generate nama file unik (biar kalau ada nama file sama gak bentrok)
            // Contoh: "foto-kucing.jpg" -> "abcd-1234-foto-kucing.jpg"
            String filename = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
            
            Path destinationFile = this.rootLocation.resolve(Paths.get(filename)).normalize().toAbsolutePath();

            // Copy file ke folder tujuan
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Gagal menyimpan file.", e);
        }
    }
}