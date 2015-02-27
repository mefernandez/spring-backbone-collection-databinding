package org.examples.spring.databinding.jpa.onetomany;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.examples.spring.databinding.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@Transactional
public class JPADataBindingControllerTest {

	@Autowired
	private IOneToManyUserRepository repository;

	@Test
	public void itSavesANewUserWithANewlyAddedPhone() throws Exception {
		// Set it up
		User user = new User();
		user.setName("Mike");
		user.setEmail("mike@mail.com");
		Map<Long, Phone> phones = new HashMap<Long, Phone>();
		Phone phone = new Phone();
		phone.setNumber("555");
		phones.put(-1L, phone);
		user.setPhones(phones);

		// Test it
		repository.save(user);
		
		// Check it
		Iterable<Object> users = (Iterable<Object>)(Iterable)repository.findAll();
		assertThat(users, hasItem(hasProperty("name", equalTo("Mike"))));
		assertThat(users, hasItem(hasProperty("email", equalTo("mike@mail.com"))));
		assertThat(users, hasItem(hasProperty("phones", hasValue(hasProperty("number", equalTo("555"))))));
	}

}
