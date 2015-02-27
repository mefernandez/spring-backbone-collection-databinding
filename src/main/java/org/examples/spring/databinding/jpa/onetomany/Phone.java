package org.examples.spring.databinding.jpa.onetomany;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Phone implements Serializable {
	
	private static final long serialVersionUID = -3670276061545258435L;

	@Id
	@GeneratedValue
	private Long id;

	@Column(nullable = false)
	private String number;

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
