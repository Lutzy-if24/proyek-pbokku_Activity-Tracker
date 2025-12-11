package org.delcom.app.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class FileStorageServiceTest {

    @InjectMocks
    private FileStorageService fileStorageService;

    // 1. Test Sukses Simpan File
    @Test
    void testStoreFile_Success() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getOriginalFilename()).thenReturn("test-image.jpg");
        // Mock getInputStream agar tidak error beneran saat Files.copy dipanggil
        when(mockFile.getInputStream()).thenReturn(new java.io.ByteArrayInputStream("test data".getBytes()));

        String filename = fileStorageService.storeFile(mockFile);

        assertNotNull(filename);
        assertTrue(filename.contains("test-image.jpg"));
    }

    // 2. Test Gagal - File Kosong
    @Test
    void testStoreFile_Fail_EmptyFile() {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(true);

        assertThrows(RuntimeException.class, () -> {
            fileStorageService.storeFile(mockFile);
        }, "Harus error jika file kosong");
    }

    // 3. Test Gagal - IOException saat menyimpan (Menghijaukan Catch kedua)
    @Test
    void testStoreFile_Fail_IOException() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getOriginalFilename()).thenReturn("test.jpg");

        // TRIK UTAMA: Paksa getInputStream melempar IOException
        when(mockFile.getInputStream()).thenThrow(new IOException("Disk Full / Permission Denied"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            fileStorageService.storeFile(mockFile);
        });
        
        assertTrue(exception.getMessage().contains("Gagal menyimpan file"));
    }

    // 4. Test Gagal - Constructor Gagal Buat Folder (Menghijaukan Catch pertama)
    @Test
    void testConstructor_Fail_CreateDirectory() {
        // Kita gunakan MockedStatic untuk memanipulasi class Files (java.nio.file.Files)
        // Ini fitur advanced Mockito untuk memock method static
        try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
            
            // Skenario: Saat Files.createDirectories dipanggil, lempar IOException
            filesMock.when(() -> Files.createDirectories(any(Path.class)))
                     .thenThrow(new IOException("Tidak bisa bikin folder"));

            // Kita harus membuat instance baru secara manual agar constructor berjalan
            // di dalam blok try-mock ini
            assertThrows(RuntimeException.class, () -> {
                new FileStorageService();
            });
        }
    }
}