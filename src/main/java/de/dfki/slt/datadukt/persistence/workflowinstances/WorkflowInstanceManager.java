package de.dfki.slt.datadukt.persistence.workflowinstances;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class WorkflowInstanceManager {

	Logger logger = Logger.getLogger(WorkflowInstanceManager.class);

	@Autowired
	WorkflowInstanceRepository workflowInstanceRepository;
	
    public HashMap<String, WorkflowInstance> workflowInstances = new HashMap<String, WorkflowInstance>();

    public WorkflowInstance save(WorkflowInstance wfe) {
    	WorkflowInstance wfe_tosave = wfe;
    	workflowInstanceRepository.save(wfe_tosave);
		workflowInstances.put(wfe.workflowInstanceId, wfe);
		return wfe;
    }
    
	public List<WorkflowInstance> findAll(){
		Collection<WorkflowInstance> collection = workflowInstances.values();
		List<WorkflowInstance> list = new LinkedList<WorkflowInstance>();
		for (WorkflowInstance we : collection) {
			list.add(we);
		}
		return list;
	}

	@Transactional
	public WorkflowInstance findOneByWorkflowInstanceId(String workflowInstanceId) throws Exception {
		WorkflowInstance wfe = null;
		if(workflowInstances.containsKey(workflowInstanceId)) {
			logger.debug("RETURNING WFI FROM HASHMAP");
			return workflowInstances.get(workflowInstanceId);
		}
		else if ((wfe=workflowInstanceRepository.findOneByWorkflowInstanceId(workflowInstanceId))!=null) {
			logger.debug("RETURNING WFI FROM REPOSITORY");
			return wfe;
		}
		else {
			logger.error("The WorkflowInstance ["+workflowInstanceId+"] does not exist.");
			return null;
		}
	}

	@Transactional
	public void deleteByWorkflowInstanceId(String workflowInstanceId) {
		WorkflowInstance wfe = null;
		if ((wfe=workflowInstanceRepository.findOneByWorkflowInstanceId(workflowInstanceId))!=null) {
			workflowInstanceRepository.deleteByWorkflowInstanceId(workflowInstanceId);
		}
		if(workflowInstances.containsKey(workflowInstanceId)) {
			workflowInstances.remove(workflowInstanceId);
		}
	}

}
