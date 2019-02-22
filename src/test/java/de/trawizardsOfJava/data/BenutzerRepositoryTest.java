package de.trawizardsOfJava.data;

import de.trawizardsOfJava.model.Person;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class BenutzerRepositoryTest {
	@Autowired
	private BenutzerRepository personen;

	@Test
	public void speicherPerson(){
		Person test = new Person();
		test.setBenutzername("foo");
		test.setName("foo");
		test.setEmail("foo");
		test.setPasswort("foo");
		test.setRolle("ROLE_USER");
		personen.save(test);

		Person person = this.personen.findByBenutzername(test.getBenutzername()).get();

		Assertions.assertThat(person.getName()).isEqualTo("foo");
	}

	@Test
	public void mehrerePersonen() {
		Person test = new Person();
		test.setBenutzername("foo");
		test.setName("foo");
		test.setEmail("foo");
		test.setPasswort("foo");
		test.setRolle("ROLE_USER");
		personen.save(test);

		Person test2 = new Person();
		test2.setBenutzername("bar");
		test2.setName("bar");
		test2.setEmail("bar");
		test2.setPasswort("bar");
		test2.setRolle("ROLE_USER");
		personen.save(test2);

		Person person = this.personen.findByBenutzername(test.getBenutzername()).get();
		Person person2 = this.personen.findByBenutzername(test2.getBenutzername()).get();

		Assertions.assertThat(person.getName()).isEqualTo("foo");
		Assertions.assertThat(person2.getName()).isEqualTo("bar");
	}
}