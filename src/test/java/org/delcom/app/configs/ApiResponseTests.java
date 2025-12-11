package org.delcom.app.configs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

class ApiResponseTests {

    @Test
    void testApiResponseConstructorAndGetters() {
        // 1. Arrange (Siapkan data)
        String status = "success";
        String message = "Operation successful";
        String data = "Test Data String";

        // 2. Act (Eksekusi)
        ApiResponse<String> response = new ApiResponse<>(status, message, data);

        // 3. Assert (Verifikasi)
        assertEquals(status, response.getStatus(), "Status harus sesuai");
        assertEquals(message, response.getMessage(), "Message harus sesuai");
        assertEquals(data, response.getData(), "Data harus sesuai");
    }

    @Test
    void testApiResponseWithNullData() {
        // Test kasus jika data null (misal error response)
        String status = "error";
        String message = "Resource not found";

        ApiResponse<Object> response = new ApiResponse<>(status, message, null);

        assertEquals(status, response.getStatus());
        assertEquals(message, response.getMessage());
        assertNull(response.getData(), "Data harus null");
    }

    @Test
    void testApiResponseWithIntegerType() {
        // Test kasus jika data berupa angka (Generic check)
        ApiResponse<Integer> response = new ApiResponse<>("ok", "Number data", 100);

        assertEquals(100, response.getData());
    }
}