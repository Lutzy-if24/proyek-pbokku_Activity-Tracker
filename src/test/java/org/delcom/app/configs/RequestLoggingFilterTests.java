package org.delcom.app.configs;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class RequestLoggingFilterTests {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private RequestLoggingFilter requestLoggingFilter;

    @Test
    void testFilterLogsRequest_Success() throws Exception {
        // 1. Arrange
        // Simulasi data request
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        
        // Simulasi response status 200 (OK)
        when(response.getStatus()).thenReturn(200);

        // 2. Act
        // Panggil method doFilter (method publik dari OncePerRequestFilter yang akan memanggil doFilterInternal)
        requestLoggingFilter.doFilter(request, response, filterChain);

        // 3. Assert
        // Pastikan filterChain.doFilter dipanggil 1 kali (Request tidak nyangkut/berhenti)
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testFilterLogsRequest_ErrorStatus() throws Exception {
        // Test untuk memastikan logika warna (RED/YELLOW) tidak bikin error
        
        // 1. Arrange
        when(request.getRequestURI()).thenReturn("/api/error");
        when(request.getMethod()).thenReturn("POST");
        when(request.getRemoteAddr()).thenReturn("192.168.1.5");
        
        // Simulasi response status 500 (Internal Server Error)
        when(response.getStatus()).thenReturn(500);

        // 2. Act
        requestLoggingFilter.doFilter(request, response, filterChain);

        // 3. Assert
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testFilterIgnoredUri() throws Exception {
        // Test logika pengecualian /.well-known (seharusnya tidak di-print, tapi chain tetap jalan)
        
        // 1. Arrange
        when(request.getRequestURI()).thenReturn("/.well-known/pki-validation");
        when(request.getMethod()).thenReturn("GET");
        // RemoteAddr tetap dipanggil di kodinganmu sebelum if, jadi perlu di-mock
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(response.getStatus()).thenReturn(200);

        // 2. Act
        requestLoggingFilter.doFilter(request, response, filterChain);

        // 3. Assert
        verify(filterChain, times(1)).doFilter(request, response);
    }
}