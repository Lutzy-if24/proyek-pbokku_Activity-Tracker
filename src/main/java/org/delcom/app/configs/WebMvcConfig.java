package org.delcom.app.configs;

import org.delcom.app.interceptors.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    // 1. Ini Konfigurasi agar folder 'uploads' bisa diakses browser
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Mendapatkan lokasi folder project saat ini
        String projectDir = System.getProperty("user.dir");
        
        // Mengarahkan URL localhost:8080/uploads/... ke folder fisik di komputer
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + projectDir + "/src/main/resources/static/uploads/");
                
        // Mengarahkan file statis lainnya (css/js)
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }

    // 2. Ini Konfigurasi Interceptor (Sudah ada sebelumnya, JANGAN DIHAPUS)
 @Override
public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(authInterceptor)
            .addPathPatterns("/api/**") 
            .excludePathPatterns(
                "/api/auth/**",
                "/api/public/**",
                "/api/activities/**"   // ✅ TAMBAHKAN INI
            );
}


    // 3. Konfigurasi CORS (SUDAH DIPERBAIKI!)
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")  // GANTI allowedOrigins jadi allowedOriginPatterns
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}