package cn.demo.gateway.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * @author Administrator
 * @date 2022/8/9
 */
@Configuration
public class CustomCorsConfiguration {
    private static final String ALL = "*";
    private static final Long MAX_AGE = 18000L;

    @Bean
    public CorsWebFilter corsFilter() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedMethod(ALL);
        configuration.addAllowedOrigin(ALL);
        configuration.addAllowedHeader(ALL);
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(MAX_AGE);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return new CorsWebFilter(source);
    }
}