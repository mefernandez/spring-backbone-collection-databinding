package org.examples.spring.databinding.jpa.onetomany;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Assigns new ids for new Users and removes deleted users from the Map
 * 
 * @author mefernandez
 */
public class UserMapPostProcessor {

	public void process(Map<Long, Phone> phones) {
		List<Long> keys = new ArrayList<Long>(phones.keySet());
		for (Long key : keys) {
			Phone phone = phones.get(key);
			if (phone.getId() == null) {
				phones.remove(key);
			} else if (phone.getId() < 0L) {
				phone.setId(null);
			}
		}
	}
}
