package org.examples.spring.databinding.list;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ListDataBindingController {
	
	// This stands for a repository of users. Using a List for simplicity.
	private List<User> users;
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String getHome() {
		return "list";
	}
	
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public String updateUsers(@ModelAttribute("form") Form form) {
		this.users = form.getUsers();
		return "redirect:/list";
	}
	
	@ModelAttribute("form")
	public Form getForm() {
		Form form = new Form();
		form.setUsers(this.users);
		return form;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

}
