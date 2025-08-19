package com.bookstore.booksmanagementsystem.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	private final UserDetailsServiceImpl userDetailsService;
	private final JwtAuthenticationEntryPoint unauthorizedHandler;
	private final JwtTokenProvider tokenProvider;

	public SecurityConfig(UserDetailsServiceImpl userDetailsService, JwtAuthenticationEntryPoint unauthorizedHandler,
			JwtTokenProvider tokenProvider) {
		this.userDetailsService = userDetailsService;
		this.unauthorizedHandler = unauthorizedHandler;
		this.tokenProvider = tokenProvider;
	}

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter(tokenProvider);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.cors(cors -> {
		}).csrf(csrf -> csrf.disable()).exceptionHandling(
				exception -> exception.authenticationEntryPoint((request, response, authException) -> {
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					response.setContentType(MediaType.APPLICATION_JSON_VALUE);
					response.getWriter().write("{\"error\": \"Unauthorized: " + authException.getMessage() + "\"}");
				}).accessDeniedHandler((request, response, accessDeniedException) -> {
					response.setStatus(HttpServletResponse.SC_FORBIDDEN);
					response.setContentType(MediaType.APPLICATION_JSON_VALUE);
					String message = accessDeniedException.getMessage();
					if (message == null || message.isEmpty() || message.contains("Access Denied")) {
						message = "Only admins can perform this action";
					}
					response.getWriter().write("{\"error\": \"Access denied: " + message + "\"}");
				})).sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/favicon.ico", "/", "/index.html", "/static/**", "/assets/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/customers").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/schools").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/schools/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/classes").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/classes/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/books").permitAll()
                        // Allow customers to read lists and list-books
                        .requestMatchers(HttpMethod.GET, "/api/lists").hasAnyRole("USER","ADMIN","SUPER_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/lists/**").hasAnyRole("USER","ADMIN","SUPER_ADMIN")
                        // Temp orders (customer draft CRUD and submit)
                        .requestMatchers(HttpMethod.GET, "/api/temp-orders/me").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/api/temp-orders/items").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT, "/api/temp-orders/items/**").hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/temp-orders/items/**").hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/temp-orders/**").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/api/temp-orders/*/submit").hasRole("USER")
                        // Temp orders admin
                        .requestMatchers(HttpMethod.GET, "/api/temp-orders").hasAnyRole("ADMIN","SUPER_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/temp-orders/*/approve").hasAnyRole("ADMIN","SUPER_ADMIN")
                        // Admin profile and admin management endpoints
                        .requestMatchers(HttpMethod.GET, "/api/admins/**").hasRole("SUPER_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/admins").hasRole("SUPER_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/admins/**").hasRole("SUPER_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/admins/**").hasRole("SUPER_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/schools").hasAnyRole("ADMIN","SUPER_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/schools/**").hasAnyRole("ADMIN","SUPER_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/schools/**").hasAnyRole("ADMIN","SUPER_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/classes/**").hasAnyRole("ADMIN","SUPER_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/classes/**").hasAnyRole("ADMIN","SUPER_ADMIN").anyRequest()
                        .authenticated());

		http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}
}
