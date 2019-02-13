package de.trawizardsOfJava.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private SecurityPersonenService userDetailsService;

	@Bean
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/").permitAll();
		http.authorizeRequests().antMatchers("/registrierung").permitAll();
		http.formLogin().permitAll();
		http.logout().permitAll();
		http.csrf().disable();		//Das Erlaubt POSTS (von der Deutschen Post)
		//TODO: Auf Das Profil eines Accounts, darf nur der Account und ein Admin zugreifen.
		http.authorizeRequests().anyRequest().authenticated();
		http.userDetailsService(userDetailsService);
	}
}
