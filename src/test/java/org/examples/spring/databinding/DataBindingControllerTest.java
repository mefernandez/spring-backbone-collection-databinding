package org.examples.spring.databinding;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.Assert.*;

import java.util.ArrayList;

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
public class DataBindingControllerTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;
	
	@Autowired
	private DataBindingController controller;

	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void addOneUserToAnEmptyList() throws Exception {
		// Initialize the users to an empty List
		ArrayList<User> users = new ArrayList<User>();
		controller.setUsers(users);
		
		// Now POST one user data to be binded and added to the List
		mockMvc.perform(post("/")
				.param("users[0].name", "John")
				.param("users[0].email", "john@mail.com"))
				.andExpect(status().is3xxRedirection());
		
		// Let's check that's true
		assertEquals(1, controller.getUsers().size());
		assertEquals("John", controller.getUsers().get(0).getName());
		assertEquals("john@mail.com", controller.getUsers().get(0).getEmail());
	}

}
