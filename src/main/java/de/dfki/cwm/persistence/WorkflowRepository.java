package de.dfki.cwm.persistence;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Julian Moreno Schneider jumo04@dfki.de
 */
@Repository
public interface WorkflowRepository extends CrudRepository<Workflow, Long> {

	public List<Workflow> findAll();

	public Workflow findOneByWorkflowId(String workflowId);

	public void deleteByWorkflowId(String workflowId);
}
