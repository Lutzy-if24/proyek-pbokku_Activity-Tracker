package org.delcom.app.configs;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

@ExtendWith(MockitoExtension.class)
class StartupInfoLoggerTests {

    @Mock
    private ApplicationReadyEvent event;

    @Mock
    private ConfigurableApplicationContext context;

    @Mock
    private ConfigurableEnvironment environment;

    @InjectMocks
    private StartupInfoLogger startupInfoLogger;

    // Variabel untuk menangkap output konsol
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    void testLogStartupInfo_DefaultValues() {
        // 1. Arrange
        when(event.getApplicationContext()).thenReturn(context);
        when(context.getEnvironment()).thenReturn(environment);

        // Mock nilai environment dasar
        // Menggunakan any() untuk parameter kedua (defaultValue) agar cocok dengan pemanggilan getProperty(key, default)
        when(environment.getProperty(eq("server.port"), any(String.class))).thenReturn("8080");
        when(environment.getProperty(eq("server.servlet.context-path"), any(String.class))).thenReturn("/");
        when(environment.getProperty(eq("server.address"), any(String.class))).thenReturn("localhost");
        
        // Mock LiveReload (boolean) -> False
        when(environment.getProperty(eq("spring.devtools.livereload.enabled"), eq(Boolean.class), eq(false))).thenReturn(false);

        // PERBAIKAN DI SINI:
        // Walaupun disabled, kode asli tetap memanggil getProperty untuk port. Kita harus mock ini juga.
        when(environment.getProperty(eq("spring.devtools.livereload.port"), any(String.class))).thenReturn("35729");

        // 2. Act
        startupInfoLogger.onApplicationEvent(event);

        // 3. Assert
        String output = outContent.toString();
        assertTrue(output.contains("http://localhost:8080"), "Output harus mengandung URL default");
        assertTrue(output.contains("LiveReload: DISABLED"), "LiveReload harusnya disabled");
    }

    @Test
    void testLogStartupInfo_CustomValues() {
        // 1. Arrange
        when(event.getApplicationContext()).thenReturn(context);
        when(context.getEnvironment()).thenReturn(environment);

        // Mock nilai custom
        when(environment.getProperty(eq("server.port"), any(String.class))).thenReturn("9090");
        when(environment.getProperty(eq("server.servlet.context-path"), any(String.class))).thenReturn("/api");
        when(environment.getProperty(eq("server.address"), any(String.class))).thenReturn("127.0.0.1");
        
        // Mock LiveReload -> True
        when(environment.getProperty(eq("spring.devtools.livereload.enabled"), eq(Boolean.class), eq(false))).thenReturn(true);
        
        // Mock LiveReload Port Custom
        when(environment.getProperty(eq("spring.devtools.livereload.port"), any(String.class))).thenReturn("35729");

        // 2. Act
        startupInfoLogger.onApplicationEvent(event);

        // 3. Assert
        String output = outContent.toString();
        assertTrue(output.contains("http://127.0.0.1:9090/api"), "Output harus sesuai konfigurasi custom");
        assertTrue(output.contains("LiveReload: ENABLED"), "LiveReload harusnya enabled");
    }
}