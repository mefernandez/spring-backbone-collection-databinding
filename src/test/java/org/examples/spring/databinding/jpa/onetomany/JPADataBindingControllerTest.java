package org.examples.spring.databinding.jpa.onetomany;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashMap;
import java.util.Map;

import org.examples.spring.databinding.Application;
import org.examples.spring.databinding.jpa.JPADataBindingController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@Transactional
public class JPADataBindingControllerTest {

	@Autowired
	private IOneToManyUserRepository repository;

	@Autowired
	private IPhoneRepository phoneRepository;

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;
	
	@Autowired
	private JPADataBindingController controller;

	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void itSavesANewUserWithANewlyAddedPhone() throws Exception {
		// Test it
		// POST one user data to be binded and added to the Map
		mockMvc.perform(post("/onetomany")
				.param("user.name", "Mike")
				.param("user.email", "mike@mail.com")
				.param("user.phones[-1].number", "555-5555"))
				.andExpect(status().is3xxRedirection());
		
		// Check it
		Iterable<Object> users = (Iterable<Object>)(Iterable)repository.findAll();
		assertThat(users, hasItem(hasProperty("name", equalTo("Mike"))));
		assertThat(users, hasItem(hasProperty("email", equalTo("mike@mail.com"))));
		assertThat(users, hasItem(hasProperty("phones", hasValue(hasProperty("number", equalTo("555-5555"))))));
	}

	@Test
	public void itDeletesThe2ndPhoneOfAUserWith3Phones() throws Exception {
		// Set it up
		User user = new User();
		user.setName("John");
		user.setEmail("john@mail.com");
		Map<Long, Phone> phones = new HashMap<Long, Phone>();
		Phone phone1 = new Phone();
		phone1.setNumber("555-5551");
		phoneRepository.save(phone1);
		phones.put(phone1.getId(), phone1);
		Phone phone2 = new Phone();
		phone2.setNumber("555-5552");
		phoneRepository.save(phone2);
		phones.put(phone2.getId(), phone2);
		Phone phone3 = new Phone();
		phone3.setNumber("555-5553");
		phoneRepository.save(phone3);
		phones.put(phone3.getId(), phone3);
		user.setPhones(phones);
		repository.save(user);
		
		// Test it
		mockMvc.perform(post("/onetomany")
				.param(String.format("user.phones[%d].id", phone2.getId()), ""))
				.andExpect(status().is3xxRedirection());
		
		// Check it
		Iterable<Object> users = (Iterable<Object>)(Iterable)repository.findAll();
		assertThat(users, hasItem(hasProperty("name", equalTo("John"))));
		assertThat(users, hasItem(hasProperty("email", equalTo("john@mail.com"))));
		assertThat(users, hasItem(hasProperty("phones", hasValue(hasProperty("number", equalTo("555-5551"))))));
		assertThat(users, hasItem(hasProperty("phones", hasValue(hasProperty("number", equalTo("555-5553"))))));
		assertThat(users, hasItem(hasProperty("phones", not(hasValue(hasProperty("number", equalTo("555-5552")))))));
	}

	@Test
	public void itAddsAPhoneToAUserWithAlreadyOnePhone() throws Exception {
		// Set it up
		User user = new User();
		user.setName("John");
		user.setEmail("john@mail.com");
		Map<Long, Phone> phones = new HashMap<Long, Phone>();
		Phone phone1 = new Phone();
		phone1.setNumber("555-5551");
		phoneRepository.save(phone1);
		phones.put(phone1.getId(), phone1);
		user.setPhones(phones);
		repository.save(user);

		// Test it
		// POST one user data to be binded and added to the Map
		mockMvc.perform(post("/onetomany")
				.param("user.name", "John")
				.param("user.email", "john@mail.com")
				.param("user.phones[-1].number", "555-5552"))
				.andExpect(status().is3xxRedirection());
		
		// Check it
		Iterable<Object> users = (Iterable<Object>)(Iterable)repository.findAll();
		assertThat(users, hasItem(hasProperty("name", equalTo("John"))));
		assertThat(users, hasItem(hasProperty("email", equalTo("john@mail.com"))));
		assertThat(users, hasItem(hasProperty("phones", hasValue(hasProperty("number", equalTo("555-5551"))))));
		assertThat(users, hasItem(hasProperty("phones", hasValue(hasProperty("number", equalTo("555-5552"))))));
	}

}
