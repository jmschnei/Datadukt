package de.dfki.slt.datadukt.persistence.workflowtemplates;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import de.dfki.slt.datadukt.communication.rabbitmq.RabbitMQManager;
import de.dfki.slt.datadukt.persistence.tasks.TaskManager;

@Component
public class WorkflowTemplateManager {

	Logger logger = Logger.getLogger(WorkflowTemplateManager.class);

	@Autowired
	WorkflowTemplateRepository workflowTemplateRepository;
	
	@Autowired
	RabbitMQManager rabbitMQManager;
	
	@Autowired
	TaskManager taskManager;
	
    public HashMap<String, WorkflowTemplate> workflowTemplates = new HashMap<String, WorkflowTemplate>();

    public WorkflowTemplate save(WorkflowTemplate wfe) {
    	WorkflowTemplate wfe_tosave = wfe;
    	workflowTemplateRepository.save(wfe_tosave);
		workflowTemplates.put(wfe.workflowTemplateId, wfe);
		return wfe;
    }
    
	public List<WorkflowTemplate> findAll(){
		Collection<WorkflowTemplate> collection = workflowTemplates.values();
		List<WorkflowTemplate> list = new LinkedList<WorkflowTemplate>();
		for (WorkflowTemplate we : collection) {
			we.reestablishComponents(rabbitMQManager, taskManager);
			list.add(we);
		}
		return list;
	}

	@Transactional
	public WorkflowTemplate findOneByWorkflowTemplateId(String workflowExecutionId) throws Exception {
		WorkflowTemplate wfe = null;
		if(workflowTemplates.containsKey(workflowExecutionId)) {
//			System.out.println("RETURNING WFE FROM HASHMAP");
			return workflowTemplates.get(workflowExecutionId);
		}
		else if ((wfe=workflowTemplateRepository.findOneByWorkflowTemplateId(workflowExecutionId))!=null) {
//			System.out.println("RETURNING WFE FROM REPOSITORY");
			wfe.reestablishComponents(rabbitMQManager, taskManager);
			return wfe;
		}
		else {
			logger.warn("The WorkflowExecution ["+workflowExecutionId+"] does not exist.");
			return null;
		}
	}

	@Transactional
	public void deleteByWorkflowTemplateId(String workflowExecutionId) {
		WorkflowTemplate wfe = null;
		if ((wfe=workflowTemplateRepository.findOneByWorkflowTemplateId(workflowExecutionId))!=null) {
			workflowTemplateRepository.deleteByWorkflowTemplateId(workflowExecutionId);
		}
		if(workflowTemplates.containsKey(workflowExecutionId)) {
			workflowTemplates.remove(workflowExecutionId);
		}
	}

}
