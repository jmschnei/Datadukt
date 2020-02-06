package de.dfki.cwm.persistence.workflowtemplates;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Julian Moreno Schneider jumo04@dfki.de
 */
@Repository
public interface WorkflowTemplateRepository extends CrudRepository<WorkflowTemplate, Long> {

	public List<WorkflowTemplate> findAll();

	@Transactional
	public WorkflowTemplate findOneByWorkflowTemplateId(String workflowTemplateId);

	@Transactional
	public void deleteByWorkflowTemplateId(String workflowId);
}
