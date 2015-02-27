package org.examples.spring.databinding.jpa.onetomany;

import org.springframework.data.repository.CrudRepository;

public interface IOneToManyUserRepository extends CrudRepository<User, Long> {

}
