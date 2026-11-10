package innowise.bankingApp.auth.config;

import innowise.bankingApp.auth.security.AccountJwtAuthenticationConverter;
import innowise.bankingApp.auth.security.SecurityErrorHandler;
import jakarta.servlet.DispatcherType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AccountJwtAuthenticationConverter converter,
            SecurityErrorHandler errors
    ) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .requestCache(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )
                .authorizeHttpRequests(authorize -> authorize
                        .dispatcherTypeMatchers(DispatcherType.ERROR)
                        .permitAll()
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/auth/login",
                                "/api/auth/refresh"
                        )
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auth/validate")
                        .authenticated()
                        .requestMatchers("/api/me", "/api/me/**")
                        .hasAnyRole("ADMIN", "USER")
                        .anyRequest()
                        .hasRole("ADMIN")
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(errors)
                        .accessDeniedHandler(errors)
                )
                .oauth2ResourceServer(resource -> resource
                        .jwt(jwt ->
                                jwt.jwtAuthenticationConverter(converter)
                        )
                        .authenticationEntryPoint(errors)
                        .accessDeniedHandler(errors)
                )
                .build();
    }
}