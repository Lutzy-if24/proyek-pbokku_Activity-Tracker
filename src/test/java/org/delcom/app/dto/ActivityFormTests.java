package org.delcom.app.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;

class ActivityFormTests {

    @Test
    void testActivityFormSettersAndGetters() {
        // 1. Arrange (Siapkan Data)
        ActivityForm form = new ActivityForm();
        
        String dateString = "2025-10-20";
        Integer steps = 10000;
        Integer activeMinutes = 60;
        Integer calories = 500;
        Double distanceKm = 7.5;
        
        // Mocking MultipartFile karena dia Interface (bukan class biasa)
        MultipartFile mockImage = Mockito.mock(MultipartFile.class);

        // 2. Act (Isi Data)
        form.setDate(dateString);
        form.setSteps(steps);
        form.setActiveMinutes(activeMinutes);
        form.setCalories(calories);
        form.setDistanceKm(distanceKm);
        form.setProofImage(mockImage);

        // 3. Assert (Cek Data)
        assertEquals(dateString, form.getDate());
        assertEquals(steps, form.getSteps());
        assertEquals(activeMinutes, form.getActiveMinutes());
        assertEquals(calories, form.getCalories());
        assertEquals(distanceKm, form.getDistanceKm());
        
        // Pastikan file yang dimasukkan sama dengan yang diambil
        assertEquals(mockImage, form.getProofImage());
    }
    
    @Test
    void testLombokMethods() {
        // Test tambahan untuk memastikan @Data (Equals & HashCode) jalan
        ActivityForm form1 = new ActivityForm();
        form1.setSteps(100);
        
        ActivityForm form2 = new ActivityForm();
        form2.setSteps(100);
        
        assertEquals(form1, form2, "Object dengan isi sama harusnya dianggap equals");
        assertNotNull(form1.toString(), "ToString tidak boleh null");
    }
}