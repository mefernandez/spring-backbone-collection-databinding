package org.examples.spring.databinding.jpa.onetomany;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

@Entity(name = "OneToManyUser")
public class User implements Serializable {

	/**
	 * Eclipse's auto serial id.
	 */
	private static final long serialVersionUID = -5031104439936653984L;

	@Id
	@GeneratedValue
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String email;
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name="id_user")
	private Map<Long, Phone> phones;

	public User() {

	}

	public User(String name, String email) {
		this.name = name;
		this.email = email;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Map<Long, Phone> getPhones() {
		return phones;
	}

	public void setPhones(Map<Long, Phone> phones) {
		this.phones = phones;
	}

}
