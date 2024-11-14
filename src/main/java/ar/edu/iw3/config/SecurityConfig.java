package ar.edu.iw3.config;

import ar.edu.iw3.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ar.edu.iw3.auth.model.business.interfaces.IUserAuthBusiness;
import ar.edu.iw3.auth.custom.CustomAuthenticationManager;
import ar.edu.iw3.auth.filters.JWTAuthorizationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private IUserAuthBusiness userBusiness;

    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

//	@Bean
//	WebMvcConfigurer corsConfigurer() {
//		// CORS: https://developer.mozilla.org/es/docs/Web/HTTP/CORS
//		return new WebMvcConfigurer() {
//			@Override
//			public void addCorsMappings(CorsRegistry registry) {
//				registry.addMapping("/**").allowedMethods("*").allowedHeaders("*").allowedOrigins("*");
//			}
//		};
//	}

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }

    @Bean
    AuthenticationManager authenticationManager() {
        return new CustomAuthenticationManager(bCryptPasswordEncoder(), userBusiness);
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(auth -> auth.requestMatchers(HttpMethod.POST, Constants.URL_LOGIN).permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll().requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll().requestMatchers("/ui/**").permitAll()
                        .requestMatchers("/demo/**").permitAll().anyRequest().authenticated())
                .exceptionHandling(e -> e.accessDeniedHandler(accessDeniedHandler));
        http.httpBasic(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilter(new JWTAuthorizationFilter(authenticationManager()));
        return http.build();
    }

}