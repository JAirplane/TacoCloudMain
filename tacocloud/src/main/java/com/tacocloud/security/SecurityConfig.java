package com.tacocloud.security;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    UserDetailsService userDetailsService(UserRepository userRepository) {
//        return username -> {
//            TacoUser user = userRepository.findByUsername(username);
//            if(user != null) return user;
//            throw new UsernameNotFoundException("User ‘" + username + "’ not found");
//        };
//    }

//    @Bean
//    SecurityFilterChain oauth2FilterChain(HttpSecurity httpSecurity) throws Exception {
//        httpSecurity.authorizeHttpRequests((authorizationManagerRequestMatcherRegistry ->
//                authorizationManagerRequestMatcherRegistry
//                        .requestMatchers(HttpMethod.POST, "/api/ingredients")
//                            .hasAuthority("SCOPE_writeIngredients")
//                        .requestMatchers(HttpMethod.DELETE, "/api/ingredients")
//                        .hasAuthority("SCOPE_deleteIngredients")
//                        .requestMatchers("/api/tacos", "/api/orders/**")
//                        .permitAll()));
//        httpSecurity.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
//        return httpSecurity.build();
//    }


    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(authorization ->
                authorization
                        .requestMatchers("/images/**").permitAll()
                        .requestMatchers("/design", "/orders", "/").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/api/ingredients")
                            .hasAuthority("SCOPE_writeIngredients")
                        .requestMatchers(HttpMethod.DELETE, "/api/ingredients")
                            .hasAuthority("SCOPE_deleteIngredients")
                        .requestMatchers((HttpMethod) null, "/api/orders")
                            .hasAuthority("SCOPE_orders")
                        .requestMatchers((HttpMethod) null, "/api/orders/*")
                            .hasAuthority("SCOPE_orders")
                        .requestMatchers("/**").permitAll()
                        .anyRequest().authenticated());
        httpSecurity.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        httpSecurity.csrf(csrf ->
                csrf.ignoringRequestMatchers(
                        new AntPathRequestMatcher("/api/**")
                )
        );
        httpSecurity.headers((headers) -> headers
                .frameOptions(
                        HeadersConfigurer.FrameOptionsConfig::disable
                )
        );
        httpSecurity.formLogin(login -> login.loginPage("/login"));
        return httpSecurity.build();
    }

}
