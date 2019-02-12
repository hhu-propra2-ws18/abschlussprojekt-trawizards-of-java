package de.trawizardsOfJava.model;

import de.trawizardsOfJava.data.BenutzerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class Personendetails implements UserDetailsService {

	@Autowired
	private BenutzerRepository users;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<Person> user = users.findByBenutzername(username);
		if (user.isPresent()) {
			Person u = user.get();
			UserDetails userdetails = User.builder()
					.username(u.getBenutzername())
					.password(u.getPasswort())
					.build();
			return userdetails;
		}
		throw new UsernameNotFoundException("Invalid Username");
	}
}
