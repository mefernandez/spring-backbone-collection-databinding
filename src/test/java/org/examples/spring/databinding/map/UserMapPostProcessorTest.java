package org.examples.spring.databinding.map;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class UserMapPostProcessorTest {

	@Test
	public void itAssignsNewIdToNewUserAndStoresNewUserInMapWhereKeyEqualsId() {
		UserMapPostProcessor processor = new UserMapPostProcessor();
		processor.startIdSequenceAt(2L);
		Map<Long, User> users = new HashMap<Long, User>();
		{
			User user = new User();
			user.setId(1L);
			user.setName("John");
			user.setEmail("john@mail.com");
			users.put(1L, user );
		}
		{
			User user = new User();
			user.setName("Mike");
			user.setEmail("mike@mail.com");
			users.put(-1L, user );
		}
		processor.process(users);
		assertTrue(users.containsKey(2L));
		assertEquals(2L, users.get(2L).getId().longValue());
		assertEquals("Mike", users.get(2L).getName());
		assertEquals("mike@mail.com", users.get(2L).getEmail());
	}

	@Test
	public void itRemovesDeletedUserFromMap() {
		UserMapPostProcessor processor = new UserMapPostProcessor();
		Map<Long, User> users = new HashMap<Long, User>();
		{
			User user = new User();
			user.setId(1L);
			user.setName("John");
			user.setEmail("john@mail.com");
			users.put(1L, user );
		}
		{
			User user = new User();
			user.setId(null);
			user.setName("Mike");
			user.setEmail("mike@mail.com");
			users.put(2L, user );
		}
		processor.process(users);
		assertEquals(1, users.size());
		assertTrue(users.containsKey(1L));
		assertTrue(!users.containsKey(2L));
	}

}
