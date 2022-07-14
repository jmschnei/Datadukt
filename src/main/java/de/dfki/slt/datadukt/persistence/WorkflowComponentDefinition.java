package de.dfki.slt.datadukt.persistence;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.json.JSONObject;

@Embeddable
public class WorkflowComponentDefinition {

	@Id
	@GeneratedValue
	String id;
	
	String workflowComponentType;
	String workflowComponentId;
	String workflowComponentName;
	
	public WorkflowComponentDefinition(){
	}
	
	public WorkflowComponentDefinition(String name, String id, String type, JSONObject json) {
		super();
		this.workflowComponentName = name;
		this.workflowComponentId = id;
		this.workflowComponentType = type;
	}

	public String getWorkflowComponentId() {
		return workflowComponentId;
	}

	public void setWorkflowComponentId(String workflowComponentId) {
		this.workflowComponentId = workflowComponentId;
	}

	public String getWorkflowComponentName() {
		return workflowComponentName;
	}

	public void setWorkflowComponentName(String workflowComponentName) {
		this.workflowComponentName = workflowComponentName;
	}
	
}
