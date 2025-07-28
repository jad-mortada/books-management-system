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
				.authorizeHttpRequests(authorize -> authorize.requestMatchers("/api/auth/**").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/customers").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/schools").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/schools/*").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/classes").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/classes/*").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/books").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/schools").hasRole("ADMIN")
						.requestMatchers(HttpMethod.PUT, "/api/schools/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/api/schools/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.PUT, "/api/classes/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/api/classes/**").hasRole("ADMIN").anyRequest()
						.authenticated());

		http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}
}
