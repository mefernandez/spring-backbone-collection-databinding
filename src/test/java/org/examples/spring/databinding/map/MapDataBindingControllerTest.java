package org.examples.spring.databinding.map;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashMap;
import java.util.Map;

import org.examples.spring.databinding.Application;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class MapDataBindingControllerTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;
	
	@Autowired
	private MapDataBindingController controller;

	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void itAddsOneUserToAnEmptyMapWithKeyEqualToMinus1() throws Exception {
		// Initialize the users to an empty Map
		Map<Long, User> users = new HashMap<Long, User>();
		controller.setUsers(users);
		
		// Now POST one user data to be binded and added to the Map
		mockMvc.perform(post("/map")
				.param("users[-1].name", "John")
				.param("users[-1].email", "john@mail.com"))
				.andExpect(status().is3xxRedirection());
		
		// Let's check that's true
		assertEquals(1, controller.getUsers().size());
		assertEquals("John", controller.getUsers().get(-1L).getName());
		assertEquals("john@mail.com", controller.getUsers().get(-1L).getEmail());
	}

	@Test
	public void itAddsTwoUsersToAnEmptyMapWithKeysEqualToMinus1AndMinus2() throws Exception {
		// Initialize the users to an empty Map
		Map<Long, User> users = new HashMap<Long, User>();
		controller.setUsers(users);
		
		// Now POST one user data to be binded and added to the Map
		mockMvc.perform(post("/map")
				.param("users[-1].name", "John")
				.param("users[-1].email", "john@mail.com")
				.param("users[-2].name", "Mike")
				.param("users[-2].email", "mike@mail.com"))
				.andExpect(status().is3xxRedirection());
		
		// Let's check that's true
		assertEquals(2, controller.getUsers().size());
		assertEquals("John", controller.getUsers().get(-1L).getName());
		assertEquals("john@mail.com", controller.getUsers().get(-1L).getEmail());
		assertEquals("Mike", controller.getUsers().get(-2L).getName());
		assertEquals("mike@mail.com", controller.getUsers().get(-2L).getEmail());
	}

	@Test
	public void itAddsANewUserToAMapHoldingOneWithKeyEqualTo1() throws Exception {
		// Initialize a Map of users
		Map<Long, User> users = new HashMap<Long, User>();
		User user = new User();
		user.setId(1L);
		user.setName("Mike");
		user.setEmail("mike@mail.com");
		users.put(1L, user);
		controller.setUsers(users);
		
		// Let's add the new user at key -1
		mockMvc.perform(post("/map")
				.param("users[-1].name", "John")
				.param("users[-1].email", "john@mail.com"))
				.andExpect(status().is3xxRedirection());
		
		// Let's check that's true
		assertEquals(2, controller.getUsers().size());
		assertEquals("Mike", controller.getUsers().get(1L).getName());
		assertEquals("mike@mail.com", controller.getUsers().get(1L).getEmail());
		assertEquals("John", controller.getUsers().get(-1L).getName());
		assertEquals("john@mail.com", controller.getUsers().get(-1L).getEmail());
	}

	@Test
	public void itModifiesAUser() throws Exception {
		// Initialize a Map of users
		Map<Long, User> users = new HashMap<Long, User>();
		User user = new User();
		user.setId(1L);
		user.setName("Mike");
		user.setEmail("mike@mail.com");
		users.put(1L, user);
		controller.setUsers(users);
		
		// Modify John's email
		mockMvc.perform(post("/map")
				.param("users[1].name", "John")
				.param("users[1].email", "john@foo.bar"))
				.andExpect(status().is3xxRedirection());
		
		// Let's check that's true
		assertEquals(1, controller.getUsers().size());
		assertEquals("John", controller.getUsers().get(1L).getName());
		assertEquals("john@foo.bar", controller.getUsers().get(1L).getEmail());
	}
}
