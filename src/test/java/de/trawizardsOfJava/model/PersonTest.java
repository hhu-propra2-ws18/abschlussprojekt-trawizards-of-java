package de.trawizardsOfJava.model;

import org.junit.Assert;
import org.junit.Test;

public class PersonTest {
	@Test
	public static void foo() {
		Person person = new Person();
		person.setBenutzername("Peter");
		Assert.assertEquals("Peter", person.getBenutzername());
	}
}
