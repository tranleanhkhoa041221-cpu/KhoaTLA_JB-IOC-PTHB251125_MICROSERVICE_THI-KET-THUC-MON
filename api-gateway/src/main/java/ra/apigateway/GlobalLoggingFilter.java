package ra.apigateway;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@Slf4j
public class GlobalLoggingFilter extends OncePerRequestFilter implements Ordered {

    @Override
    public int getOrder() {
        return -1;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("Time={}, Method={}, URI={}", LocalDateTime.now(), request.getMethod(), request.getRequestURI());
        filterChain.doFilter(request, response);
    }
}
