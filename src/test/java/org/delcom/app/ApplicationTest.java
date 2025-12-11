package org.delcom.app;

import org.junit.jupiter.api.Test;

class ApplicationTest {

    @Test
    void testMain() {
        // Kita kirim argumen "--server.port=0"
        // Ini menyuruh Spring Boot mencari port acak yang kosong (misal: 54321),
        // sehingga tidak bentrok dengan port 8080 yang sedang dipakai.
        Application.main(new String[] {
            "--server.port=0",  // <--- TAMBAHAN PENTING DISINI
            "--spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
            "--spring.datasource.driverClassName=org.h2.Driver",
            "--spring.datasource.username=sa",
            "--spring.datasource.password=",
            "--spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
            "--spring.jpa.hibernate.ddl-auto=create-drop",
            "--spring.sql.init.mode=never"
        });
    }
}