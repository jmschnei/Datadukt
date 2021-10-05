package de.dfki.cwm.components.input;

import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import de.dfki.cwm.components.WorkflowComponent;
import de.dfki.cwm.data.Format;
import de.dfki.cwm.exceptions.WorkflowException;
import de.dfki.cwm.persistence.DataManager;

/**
 * @author julianmorenoschneider
 * @project CurationWorkflowManager
 * @date 07.02.2020
 * @company DFKI
 * @description Class developed for the management of input URIs
 * 
 */
public class EmptyInputComponent extends InputComponent {

	Format inputFormat;
	
	public EmptyInputComponent(Format inputFormat) {
		this.inputFormat = inputFormat;
	}

	@Override
	public String executeComponentSynchronous(String document, HashMap<String, String> parameters, boolean priority, DataManager manager, String outputCallback, String statusCallback, boolean persist, boolean isContent) throws WorkflowException{
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
			throw new WorkflowException(e.getMessage());
		}
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

	public JSONObject getJSONRepresentation() throws Exception {
		JSONObject json = new JSONObject();
		json.put("componentName", getWorkflowComponentName());
		json.put("componentId", getWorkflowComponentId());
		return json;
	}

	public String startExecuteComponent(String documentId, boolean priority, DataManager dataManager, String outputCallback, String statusCallback, boolean persist, boolean isContent) throws WorkflowException {
		return "DONE";
	}

	public void setComponentsList(List<WorkflowComponent> componentsList) {
	}


}
