package de.dfki.slt.datadukt.persistence.workflowexecutions;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class WorkflowExecutionManager {

	Logger logger = Logger.getLogger(WorkflowExecutionManager.class);

	@Autowired
	WorkflowExecutionRepository workflowExecutionRepository;
	
    public HashMap<String, WorkflowExecution> workflowExecutions = new HashMap<String, WorkflowExecution>();

    public WorkflowExecution save(WorkflowExecution wfe) {
    	WorkflowExecution wfe_tosave = wfe;
    	workflowExecutionRepository.save(wfe_tosave);
		workflowExecutions.put(wfe.workflowExecutionId, wfe);
		return wfe;
    }
    
	public List<WorkflowExecution> findAll(){
		Collection<WorkflowExecution> collection = workflowExecutions.values();
		List<WorkflowExecution> list = new LinkedList<WorkflowExecution>();
		for (WorkflowExecution we : collection) {
			list.add(we);
		}
		return list;
	}

	@Transactional
	public WorkflowExecution findOneByWorkflowExecutionId(String workflowExecutionId) throws Exception {
		WorkflowExecution wfe = null;
		if(workflowExecutions.containsKey(workflowExecutionId)) {
			logger.debug("RETURNING WFE FROM HASHMAP");
			return workflowExecutions.get(workflowExecutionId);
		}
		else if ((wfe=workflowExecutionRepository.findOneByWorkflowExecutionId(workflowExecutionId))!=null) {
			logger.debug("RETURNING WFE FROM REPOSITORY");
			return wfe;
		}
		else {
			logger.error("The WorkflowExecution ["+workflowExecutionId+"] does not exist.");
			return null;
		}
	}

	@Transactional
	public void deleteByWorkflowExecutionId(String workflowExecutionId) {
		WorkflowExecution wfe = null;
		if ((wfe=workflowExecutionRepository.findOneByWorkflowExecutionId(workflowExecutionId))!=null) {
			workflowExecutionRepository.deleteByWorkflowExecutionId(workflowExecutionId);
		}
		if(workflowExecutions.containsKey(workflowExecutionId)) {
			workflowExecutions.remove(workflowExecutionId);
		}
	}

}
