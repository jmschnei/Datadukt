package de.dfki.cwm.components.output;

import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import de.dfki.cwm.components.WorkflowComponent;
import de.dfki.cwm.exceptions.WorkflowException;
import de.dfki.cwm.persistence.DataManager;

public class URIOutputComponent extends OutputComponent {

	
	public URIOutputComponent() {
	}

	@Override
	public String executeComponentSynchronous(String document, HashMap<String, String> parameters, boolean priority, DataManager manager, String outputCallback, String statusCallback, boolean persist, boolean isContent) throws WorkflowException{
		return executeComponent(document, parameters, priority, manager, outputCallback, statusCallback, persist, isContent);
	}

	@Override
	public String executeComponent(String document, HashMap<String, String> parameters, boolean priority, DataManager manager, String outputCallback, String statusCallback, boolean persist, boolean isContent) throws WorkflowException{
		try {
			return document;
		}
		catch(Exception e) {
			e.printStackTrace();
			throw new WorkflowException(e.getMessage());
		}		
	}

	@Override
	public String executeComponent(String documentURI, boolean priority, DataManager manager, String outputCallback, String statusCallback, boolean persist, boolean isContent) throws WorkflowException {
		try {
			return documentURI;
		}
		catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public JSONObject getJSONRepresentation() throws Exception {
		JSONObject json = new JSONObject();
		json.put("componentName", getWorkflowComponentName());
		json.put("componentId", getWorkflowComponentId());
		return json;
	}

	public String startExecuteComponent(String documentId, boolean priority, DataManager datasManager, String outputCallback, String statusCallback, boolean persist, boolean isContent) throws WorkflowException {
		return "DONE";
	}

	public void setComponentsList(List<WorkflowComponent> componentsList) {
	}


}
