package de.dfki.slt.datadukt.components;

import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import de.dfki.slt.datadukt.exceptions.WorkflowException;
import de.dfki.slt.datadukt.persistence.DataManager;

/**
 * @author julianmorenoschneider
 * @project CurationWorkflowManager
 * @date 07.02.2020
 * @company DFKI
 * @description Class that represents a split point in a Workflow Manager diagram. The input is redirected to
 * 				all the connected components.
 */
public class SplitComponent extends WorkflowComponent{

	/**
	 * Components connected at the output of the SplitComponent
	 */
	List<WorkflowComponent> componentsList;

	public SplitComponent(JSONObject jsonDefinition, DataManager dataManager, String workflowId) throws Exception{
		super(jsonDefinition.getString("componentName"), jsonDefinition.getString("componentId"),jsonDefinition.getString("component_type"),workflowId);
		try{
		}
		catch(Exception e){
			e.printStackTrace();
			throw e;
		}
	}


	@Override
	public String executeComponentSynchronous(String document, HashMap<String, String> parameters, boolean priority, DataManager manager, String outputCallback, String statusCallback, boolean persist, boolean isContent) throws WorkflowException{
		String msg = "Method not supported in SplitComponent.";
		System.out.println(msg);
		throw new WorkflowException(msg);
	}

	@Override
	public String executeComponent(String document, HashMap<String, String> parameters, boolean priority, DataManager manager, String outputCallback, String statusCallback, boolean persist, boolean isContent) throws WorkflowException{
		if(componentsList==null) {
			String msg = "Components List in SplitComponent has to be established [not NULL] for execution.";
			throw new WorkflowException(msg);
		}
		if(componentsList.isEmpty()) {
			String msg = "Components List in SplitComponent has to be established [not empty] for execution.";
			throw new WorkflowException(msg);
		}
		for (WorkflowComponent wfc : componentsList) {
			String partialResult = wfc.executeComponent(document,parameters,priority,manager,outputCallback,statusCallback, persist, isContent);
			if(partialResult==null){
				return null;
			}
		}
		System.out.println("[Split Controller ["+workflowComponentName+"]] Executed correctly.");
		return "DONE";
	}

	@Override
	public String executeComponent(String document, boolean priority, DataManager manager, String outputCallback, String statusCallback, boolean persist, boolean isContent) throws WorkflowException {
		return executeComponent(document, null, priority, manager, outputCallback, statusCallback, persist, isContent);
	}

	public List<WorkflowComponent> getComponentsList() {
		return componentsList;
	}

	public void setComponentsList(List<WorkflowComponent> componentsList) {
		this.componentsList = componentsList;
	}

	public JSONObject getJSONRepresentation() throws Exception {
		JSONObject json = new JSONObject();
		json.put("name", workflowComponentName);
		json.put("id", workflowComponentId);
		return json;
	}

	public String startExecuteComponent(String documentId, boolean priority, DataManager dataManager, String outputCallback, String statusCallback, boolean persist, boolean isContent) throws WorkflowException {
		return "DONE";
	}


}
