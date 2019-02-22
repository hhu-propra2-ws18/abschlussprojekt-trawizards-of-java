package de.trawizardsOfJava.security;

import de.trawizardsOfJava.data.BenutzerRepository;
import de.trawizardsOfJava.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SecurityPersonenService implements UserDetailsService {
	@Autowired
	private BenutzerRepository personen;

	@Override
	public UserDetails loadUserByUsername(String benutzername) throws UsernameNotFoundException {
		Optional<Person> user = this.personen.findByBenutzername(benutzername);
		if(user.isPresent()) {
			Person u = user.get();
			return User.builder()
				.username(u.getBenutzername())
				.password(u.getPasswort())
				.authorities(u.getRolle())
				.build();
		}
		throw new UsernameNotFoundException("Invalid Username");
	}
}