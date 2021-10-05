package de.dfki.cwm.persistence.workflowinstances;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Julian Moreno Schneider jumo04@dfki.de
 */
@Repository
public interface WorkflowInstanceRepository extends CrudRepository<WorkflowInstance, Long> {

	public List<WorkflowInstance> findAll();

	@Transactional
	public WorkflowInstance findOneByWorkflowInstanceId(String workflowInstanceId);

	@Transactional
	public void deleteByWorkflowInstanceId(String workflowInstanceId);
}
