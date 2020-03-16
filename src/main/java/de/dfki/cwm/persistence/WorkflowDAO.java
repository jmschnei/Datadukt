package de.dfki.cwm.persistence;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import de.dfki.cwm.communication.rabbitmq.RabbitMQManager;

/**
 * Complex database functionality for workflowsDefinition
 * 
 * @author Julian Moreno Schneider jumo04@dfki.de
 */
@Component
public class WorkflowDAO {

	@Autowired
	WorkflowRepository workflowRepository;

	@Autowired
	EntityManager entityManager;

	@Transactional
	public Workflow findOneByWorkflowId(String workflowId, RabbitMQManager rabbitMQManager) throws Exception {
		Workflow workflow = workflowRepository.findOneByWorkflowId(workflowId);
		if(workflow==null){
			String msg = String.format("The workflow \"%s\" does not exist.",workflowId);
        	throw new Exception(msg);
		}
//		workflow.reestablishComponents(rabbitMQManager);
		return workflow;
	}

	@Transactional
	public void deleteByWorkflowId(String workflowId) {
		Workflow workflow = workflowRepository.findOneByWorkflowId(workflowId);
		entityManager.remove(workflow);
	}
}
