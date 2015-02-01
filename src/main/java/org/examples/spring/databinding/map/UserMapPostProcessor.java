package org.examples.spring.databinding.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Assigns new ids for new Users and removes deleted users from the Map
 * @author mefernandez
 */
public class UserMapPostProcessor {
	
	private long userIdSequence = 1L;

	public void process(Map<Long, User> users) {
		List<Long> keys = new ArrayList<Long>(users.keySet());
		for (Long key : keys) {
			if (key < 0L) {
				User user = users.remove(key);
				long nextId = this.userIdSequence++;
				user.setId(nextId);
				users.put(nextId, user);
			} else {
				User user = users.get(key);
				if (user.getId() == null) {
					users.remove(key);
				}
			}
		}
	}

	public void startIdSequenceAt(long userIdSequence) {
		this.userIdSequence = userIdSequence;
	}
}
