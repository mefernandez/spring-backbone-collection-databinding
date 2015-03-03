package org.examples.spring.databinding.jpa.onetomany;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Assigns new ids for new Users and removes deleted users from the Map
 * 
 * @author mefernandez
 */
@Component
public class UserOneToManyPhonePostProcessor {
	
	@Autowired
	IPhoneRepository phoneRepository;

	public void process(Map<Long, Phone> phones) {
		List<Long> keys = new ArrayList<Long>(phones.keySet());
		for (Long key : keys) {
			Phone phone = phones.get(key);
			if (key > 0 && phone.getId() == null) {
				phones.remove(key);
				phoneRepository.delete(phone);
			}
		}
	}
}
