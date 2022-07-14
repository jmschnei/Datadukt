package de.dfki.slt.datadukt.persistence.tasks;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Julian Moreno Schneider jumo04@dfki.de
 */
@Repository
public interface TaskRepository extends CrudRepository<Task, Long> {

	public List<Task> findAll();

	@Transactional
	public Task findOneByTaskId(String taskId);

	@Transactional
	public void deleteByTaskId(String taskId);
}
