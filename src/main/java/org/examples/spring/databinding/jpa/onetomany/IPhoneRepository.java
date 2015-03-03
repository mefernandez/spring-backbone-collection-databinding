package org.examples.spring.databinding.jpa.onetomany;

import org.springframework.data.repository.CrudRepository;

public interface IPhoneRepository extends CrudRepository<Phone, Long> {

}
