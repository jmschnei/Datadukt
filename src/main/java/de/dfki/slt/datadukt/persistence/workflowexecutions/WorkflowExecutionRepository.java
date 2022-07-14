package de.dfki.slt.datadukt.persistence.workflowexecutions;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Julian Moreno Schneider jumo04@dfki.de
 */
@Repository
public interface WorkflowExecutionRepository extends CrudRepository<WorkflowExecution, Long> {

	public List<WorkflowExecution> findAll();

	@Transactional
	public WorkflowExecution findOneByWorkflowExecutionId(String workflowExecutionId);

	@Transactional
	public void deleteByWorkflowExecutionId(String workflowExecutionId);
}
