package io.github._2don.api.security;

import io.github._2don.api.repositories.AccountJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

  @Autowired
  private JWTConfig jwtConfig;
  @Autowired
  private AccountJPA accountJPA;
  @Autowired
  private JWTAuthenticationProvider jwtAuthenticationProvider;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
      // add cors headers
      .cors().and()
      // disable default csrf as it will not be used
      .csrf().disable()
      // authorize requests that don't need auth
      .authorizeRequests().antMatchers(HttpMethod.POST, "/accounts/sign-up").permitAll()
      .antMatchers(HttpMethod.GET, "/accounts/exists/{email}").permitAll()
      // set all other requests for authenticated users only
      .anyRequest().authenticated().and()
      // add sign-in route
      .addFilterAt(
        new JWTAuthenticationFilter(jwtConfig, authenticationManager(), "/accounts/sign-in"),
        UsernamePasswordAuthenticationFilter.class)
      // check if users are logged in
      .addFilterAt(
        new JWTAuthorizationFilter(jwtConfig, accountJPA, authenticationManager()),
        BasicAuthenticationFilter.class)
      // disable token storage because jwt does not need it
      .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.authenticationProvider(jwtAuthenticationProvider);
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    final var source = new UrlBasedCorsConfigurationSource();
    var corsConfig = new CorsConfiguration().applyPermitDefaultValues();
    corsConfig.addAllowedMethod(CorsConfiguration.ALL);
    source.registerCorsConfiguration("/**", corsConfig);
    return source;
  }
}
