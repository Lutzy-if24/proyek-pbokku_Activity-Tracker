package org.delcom.app.configs;

import org.delcom.app.interceptors.AuthInterceptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

@ExtendWith(MockitoExtension.class)
class WebMvcConfigTest {

    @Mock
    private AuthInterceptor authInterceptor;

    @InjectMocks
    private WebMvcConfig webMvcConfig;

    @Test
    void testAddInterceptors() {
        // 1. Arrange
        InterceptorRegistry registry = mock(InterceptorRegistry.class);
        InterceptorRegistration registration = mock(InterceptorRegistration.class);

        // Mock chaining .addInterceptor()
        when(registry.addInterceptor(any(AuthInterceptor.class))).thenReturn(registration);
        
        // Mock .addPathPatterns() - pakai anyString() aman
        when(registration.addPathPatterns(anyString())).thenReturn(registration);
        
        // PERBAIKAN: Gunakan nilai Eksak/Pasti agar Mockito tidak bingung dengan Varargs
        when(registration.excludePathPatterns(
            "/api/auth/**", 
            "/api/public/**", 
            "/api/activities/**"
        )).thenReturn(registration);

        // 2. Act
        webMvcConfig.addInterceptors(registry);

        // 3. Assert
        verify(registry).addInterceptor(authInterceptor);
        verify(registration).addPathPatterns("/api/**");
        
        // Verifikasi dengan nilai yang sama persis
        verify(registration).excludePathPatterns(
            "/api/auth/**", 
            "/api/public/**", 
            "/api/activities/**"
        );
    }

    @Test
    void testAddCorsMappings() {
        // 1. Arrange
        CorsRegistry registry = mock(CorsRegistry.class);
        CorsRegistration registration = mock(CorsRegistration.class);

        // Mock chaining
        when(registry.addMapping(anyString())).thenReturn(registration);
        when(registration.allowedOriginPatterns(anyString())).thenReturn(registration);
        
        // PERBAIKAN: Gunakan nilai Eksak untuk Varargs
        when(registration.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")).thenReturn(registration);
        
        when(registration.allowedHeaders(anyString())).thenReturn(registration);
        when(registration.allowCredentials(anyBoolean())).thenReturn(registration);
        when(registration.maxAge(anyLong())).thenReturn(registration);

        // 2. Act
        webMvcConfig.addCorsMappings(registry);

        // 3. Assert
        verify(registry).addMapping("/**");
        verify(registration).allowedOriginPatterns("*");
        
        // Verifikasi harus sama persis dengan Stubbing di atas
        verify(registration).allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
        
        verify(registration).allowCredentials(true);
        verify(registration).maxAge(3600);
    }

    @Test
    void testAddResourceHandlers() {
        // 1. Arrange
        ResourceHandlerRegistry registry = mock(ResourceHandlerRegistry.class);
        ResourceHandlerRegistration registration = mock(ResourceHandlerRegistration.class);

        // Mock chaining
        when(registry.addResourceHandler(anyString())).thenReturn(registration);
        // Kita gunakan any() disini karena argumennya dinamis (path user.dir)
        when(registration.addResourceLocations(any(String.class))).thenReturn(registration);

        // 2. Act
        webMvcConfig.addResourceHandlers(registry);

        // 3. Assert
        verify(registry).addResourceHandler("/uploads/**");
        
        // Menggunakan argThat untuk mengecek apakah path mengandung folder yang benar
        verify(registration, atLeastOnce()).addResourceLocations(
            argThat((String path) -> path != null && path.contains("/src/main/resources/static/uploads/"))
        );
        
        verify(registry).addResourceHandler("/static/**");
        verify(registration).addResourceLocations("classpath:/static/");
    }
}