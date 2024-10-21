package frgp.utn.edu.ar.quepasa.config;

import frgp.utn.edu.ar.quepasa.model.enums.Role;
import frgp.utn.edu.ar.quepasa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;


@Configuration
@EnableWebSecurity
@EnableTransactionManagement
public class SecurityConfig {

    private JwtAuthenticationFilter jwtAuthenticationFilter;
    private UserService userService;

    @Autowired @Lazy
    public void setJwtAuthenticationFilter(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Autowired @Lazy
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> {
                    request.requestMatchers(
                                    "/api/signup",
                                    "/api/login",
                                    "/api/login/totp",
                                    "/api/recover",
                                    "/api/recover/reset"
                            ).permitAll();

                    // Sección usuarios
                    request.requestMatchers(HttpMethod.PATCH, "/api/users/me")
                                    .authenticated();
                    request.requestMatchers(HttpMethod.GET, "/api/users", "/api/users/**")
                            .hasAuthority(Role.NEIGHBOUR.name());
                    request.requestMatchers(HttpMethod.PATCH, "/api/users/**")
                            .hasAuthority(Role.ADMIN.name());

                    request.requestMatchers(HttpMethod.GET, "/api/countries", "/api/countries/**")
                                    .permitAll();
                    request.requestMatchers(HttpMethod.POST, "/api/states")
                                    .hasAuthority(Role.ADMIN.name());
                    request.requestMatchers(HttpMethod.PATCH, "/api/states/**")
                            .hasAuthority(Role.ADMIN.name());
                    request.requestMatchers(HttpMethod.DELETE, "/api/states/**")
                            .hasAuthority(Role.ADMIN.name());
                    // Fin sección usuarios

                    // Sección tipos de publicaciones
                    request.requestMatchers(HttpMethod.POST, "/api/post-types", "/api/post-types/**")
                            .hasAuthority(Role.ADMIN.name());
                    request.requestMatchers(HttpMethod.PATCH, "/api/post-types/**")
                            .hasAuthority(Role.ADMIN.name());
                    request.requestMatchers(HttpMethod.DELETE, "/api/post-types/**")
                            .hasAuthority(Role.ADMIN.name());
                    // Fin sección tipos de publicaciones

                    // Sección subtipos de publicaciones
                    request.requestMatchers(HttpMethod.POST, "/api/post-subtypes", "/api/post-subtypes/**")
                            .hasAuthority(Role.ADMIN.name());
                    request.requestMatchers(HttpMethod.PATCH, "/api/post-subtypes/**")
                            .hasAuthority(Role.ADMIN.name());
                    request.requestMatchers(HttpMethod.DELETE, "/api/post-subtypes/**")
                            .hasAuthority(Role.ADMIN.name());
                    // Fin sección subtipos de publicaciones

                    // Sección publicaciones
                    request.requestMatchers(HttpMethod.GET, "/api/posts/op/{id}")
                            .hasAuthority(Role.NEIGHBOUR.name());
                    request.requestMatchers(HttpMethod.GET, "/api/posts/me")
                            .hasAuthority(Role.NEIGHBOUR.name());
                    request.requestMatchers(HttpMethod.POST, "/api/posts", "api/posts/**")
                            .hasAuthority(Role.NEIGHBOUR.name());
                    request.requestMatchers(HttpMethod.GET, "/api/posts/**", "/api/posts/{id}/votes", "/api/posts/{id}/votes/**")
                            .hasAuthority(Role.NEIGHBOUR.name());
                    request.requestMatchers(HttpMethod.POST,  "/api/posts/{id}/votes", "/api/posts/{id}/votes/**")
                            .hasAuthority(Role.NEIGHBOUR.name());
                    // Fin sección publicaciones

                    // Seccion eventos
                    request.requestMatchers(HttpMethod.GET, "/api/events/**")
                            .hasAuthority(Role.USER.name());
                    request.requestMatchers(HttpMethod.GET, "/api/events/op/{id}")
                            .hasAuthority(Role.NEIGHBOUR.name());
                    request.requestMatchers(HttpMethod.GET, "/api/events/me")
                            .hasAuthority(Role.NEIGHBOUR.name());
                    request.requestMatchers(HttpMethod.POST, "/api/events", "api/events/**")
                            .hasAuthority(Role.NEIGHBOUR.name());

                    // Resto de endpoints.
                    request.anyRequest().authenticated();
                })
                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*@Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }*/

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

}