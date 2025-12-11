package org.delcom.app;

import org.junit.jupiter.api.Test;

class ApplicationTest {

    @Test
    void testMain() {
        // Memanggil method main secara manual agar baris SpringApplication.run() ter-cover
        // Kita mengirim array string kosong sebagai argumen
        Application.main(new String[] {});
    }
}