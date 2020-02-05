package de.dfki.cwm.controllers;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Julian Moreno Schneider jumo04@dfki.de
 */
@Repository
public interface ControllerRepository extends CrudRepository<Controller, Long> {

	public List<Controller> findAll();

	@Transactional
	public Controller findOneByControllerId(String controllerId);

	@Transactional
	public void deleteByControllerId(String controllerId);
}
