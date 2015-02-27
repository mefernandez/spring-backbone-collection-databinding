package org.examples.spring.databinding.jpa.onetomany;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Transactional
@Controller
public class OneToManyDataBindingController {

	@Autowired
	private IOneToManyUserRepository repository;

	private UserMapPostProcessor processor = new UserMapPostProcessor();
	
	@RequestMapping(value = "/onetomany", method = RequestMethod.GET)
	public String getHome() {
		return "onetomany";
	}

	@RequestMapping(value = "/onetomany", method = RequestMethod.POST)
	public String updateUsers(@ModelAttribute("form") Form form) {
		// Process the Users in this Map according to 
		// the databinding convention for Map about keys and ids
		//processor.process(form.getUser().getPhones());
		
		// Save the binded data to our "Repository"
		User user = form.getUser();
		this.repository.save(user);
		return "redirect:/onetomany";
	}

	@ModelAttribute("form")
	public Form getForm(HttpServletRequest request) {
		Form form = new Form();
		if (repository.count() > 0) {
			User user = repository.findAll().iterator().next();
			form.setUser(user);
		} else {
			form.setUser(new User());
		}
		return form;
	}

	public IOneToManyUserRepository getRepository() {
		return repository;
	}

	public void setRepository(IOneToManyUserRepository repository) {
		this.repository = repository;
	}


}
