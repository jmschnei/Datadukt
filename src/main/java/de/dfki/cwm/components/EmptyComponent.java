package de.dfki.cwm.components;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import de.dfki.cwm.exceptions.WorkflowException;
import de.dfki.cwm.persistence.DataManager;

/**
 * @author julianmorenoschneider
 * @project CurationWorkflowManager
 * @date 07.02.2020
 * @company DFKI
 * @description Class that represents a WaitComponent, that does not continue until all the connected services answer
 * 
 */
public class EmptyComponent extends WorkflowComponent{
	
	// Logger object
	Logger logger = Logger.getLogger(EmptyComponent.class);

	public EmptyComponent(JSONObject jsonDefinition, DataManager dataManager, String workflowExecutionId) throws Exception{
		super(jsonDefinition.getString("componentName"), jsonDefinition.getString("componentId"),jsonDefinition.getString("component_type"),workflowExecutionId);
		try{
		}
		catch(Exception e){
			e.printStackTrace();
			throw e;
		}
	}
		
	@Override
	public String executeComponent(String document, HashMap<String, String> parameters, boolean priority, DataManager manager, String outputCallback, String statusCallback, boolean persist, boolean isContent) throws WorkflowException{
		return executeComponent(document, priority, manager, outputCallback, statusCallback, persist, isContent);
	}

	@Override
	public String executeComponentSynchronous(String document, HashMap<String, String> parameters, boolean priority, DataManager manager, String outputCallback, String statusCallback, boolean persist, boolean isContent) throws WorkflowException{
		return executeComponent(document, priority, manager, outputCallback, statusCallback, persist, isContent);
	}

	@Override
	public String executeComponent(String document, boolean priority, DataManager manager, String outputCallback, String statusCallback, boolean persist, boolean isContent) throws WorkflowException {
		try{
//			System.out.println("[Empty Controller ["+workflowComponentName+"]] executed correctly.");
			logger.info("[Empty Controller ["+workflowComponentName+"]] executed correctly.");
			return document;
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("unused")
	public String startExecuteComponent(String documentId, boolean priority, DataManager dataManager, String outputCallback, String statusCallback, boolean persist, boolean isContent) throws WorkflowException {
		try{
//			System.out.println("[Empty Controller ["+workflowComponentName+"]] executed correctly.");
			logger.info("[Empty Controller ["+workflowComponentName+"]] executed correctly.");
			return "DONE";
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public JSONObject getJSONRepresentation() throws Exception {
		JSONObject json = new JSONObject();
		json.put("name", workflowComponentName);
		json.put("id", workflowComponentId);
		return json;
	}

	@Override
	public void setComponentsList(List<WorkflowComponent> componentsList) {
	}

}
