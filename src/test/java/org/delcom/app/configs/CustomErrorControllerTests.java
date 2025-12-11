package org.delcom.app.configs;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.webmvc.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.ServletWebRequest;

@ExtendWith(MockitoExtension.class)
class CustomErrorControllerTests {

    @Mock
    private ErrorAttributes errorAttributes;

    @Mock
    private ServletWebRequest webRequest;

    @InjectMocks
    private CustomErrorController customErrorController;

    @Test
    void testHandleError_500() {
        // 1. Arrange (Siapkan simulasi error 500)
        Map<String, Object> mockAttributes = new HashMap<>();
        mockAttributes.put("status", 500);
        mockAttributes.put("error", "Internal Server Error");
        mockAttributes.put("path", "/api/error-path");

        // Ketika errorAttributes dipanggil, kembalikan map di atas
        when(errorAttributes.getErrorAttributes(any(ServletWebRequest.class), any(ErrorAttributeOptions.class)))
                .thenReturn(mockAttributes);

        // 2. Act
        ResponseEntity<Map<String, Object>> response = customErrorController.handleError(webRequest);

        // 3. Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("error", body.get("status"), "Jika code 500, status text harus 'error'");
        assertEquals("/api/error-path", body.get("path"));
        assertNotNull(body.get("timestamp"), "Timestamp harus ada");
    }

    @Test
    void testHandleError_404() {
        // 1. Arrange (Siapkan simulasi error 404 Not Found)
        Map<String, Object> mockAttributes = new HashMap<>();
        mockAttributes.put("status", 404);
        mockAttributes.put("error", "Not Found");
        mockAttributes.put("path", "/api/unknown");

        when(errorAttributes.getErrorAttributes(any(ServletWebRequest.class), any(ErrorAttributeOptions.class)))
                .thenReturn(mockAttributes);

        // 2. Act
        ResponseEntity<Map<String, Object>> response = customErrorController.handleError(webRequest);

        // 3. Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        
        // Sesuai logika: status == 500 ? "error" : "fail"
        assertEquals("fail", body.get("status"), "Jika code bukan 500 (misal 404), status text harus 'fail'");
        assertEquals("Endpoint tidak ditemukan atau terjadi error", body.get("message"));
    }
}