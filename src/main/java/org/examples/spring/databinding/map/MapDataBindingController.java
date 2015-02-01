package org.examples.spring.databinding.map;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class MapDataBindingController {
	
	// This stands for a repository of users. Using a List for simplicity.
	private Map<Long, User> users = new HashMap<Long, User>();
	
	private UserMapPostProcessor processor = new UserMapPostProcessor();
	
	@RequestMapping(value = "/map", method = RequestMethod.GET)
	public String getHome() {
		return "map";
	}
	
	@RequestMapping(value = "/map", method = RequestMethod.POST)
	public String updateUsers(@ModelAttribute("form") Form form) {
		processor.process(form.getUsers());
		// Save the binded data to our "Repository"
		this.users.putAll(form.getUsers());
		return "redirect:/map";
	}
	
	@ModelAttribute("form")
	public Form getForm() {
		Form form = new Form();
		form.setUsers(this.users);
		return form;
	}

	public Map<Long, User> getUsers() {
		return users;
	}

	public void setUsers(Map<Long, User> users) {
		this.users = users;
	}

	public UserMapPostProcessor getProcessor() {
		return processor;
	}

	public void setProcessor(UserMapPostProcessor processor) {
		this.processor = processor;
	}

}
