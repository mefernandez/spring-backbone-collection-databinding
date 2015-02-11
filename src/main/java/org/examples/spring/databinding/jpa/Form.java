package org.examples.spring.databinding.jpa;

import java.util.Map;


public class Form {

	private Map<Long, User> users;

	public Map<Long, User> getUsers() {
		return users;
	}

	public void setUsers(Map<Long, User> users) {
		this.users = users;
	}

}
