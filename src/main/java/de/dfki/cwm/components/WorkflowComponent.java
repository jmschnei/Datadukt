package de.dfki.cwm.components;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.json.JSONObject;

import com.rabbitmq.client.DeliverCallback;

import de.dfki.cwm.communication.rabbitmq.RabbitMQManager;
import de.dfki.cwm.exceptions.WorkflowException;
import de.dfki.cwm.persistence.DataManager;
import de.dfki.cwm.persistence.tasks.Task;

@Entity
public abstract class WorkflowComponent {

	@Id
	@GeneratedValue
	String id;
	
	String workflowComponentType;
	String workflowComponentId;
	String workflowComponentName;

	String workflowExecutionId;
	
	public WorkflowComponent(){
	}
	
	public WorkflowComponent(String name, String id, String type, String workflowId) {
		super();
		this.workflowComponentName = name;
		this.workflowComponentId = id;
		this.workflowComponentType = type;
		this.workflowExecutionId = workflowId;
	}

	public String getName() {
		return workflowComponentName;
	}

	public void setName(String name) {
		this.workflowComponentName = name;
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

	public abstract String executeComponent(String content, boolean priority, DataManager dataManager, String outputCallback, String statusCallback, boolean persist, boolean isContent) throws WorkflowException;

	public abstract String executeComponent(String document, HashMap<String, String> parameters, boolean priority, DataManager manager, String outputCallback, String statusCallback, boolean persist, boolean isContent) throws WorkflowException;

	public static WorkflowComponent defineComponent(JSONObject object, DataManager dataManager, String workflowId) throws Exception {
		try{
			String type = object.getString("component_type");
			if(type.equalsIgnoreCase("split")){
				return new SplitComponent(object, dataManager, workflowId);
			}
			else if(type.equalsIgnoreCase("wait")){
				return new WaitComponent(object, dataManager, workflowId);
			}
			else if(type.equalsIgnoreCase("waitcombiner")){
				return new WaitCombinerComponent(object, dataManager, workflowId);
			}
			else if(type.equalsIgnoreCase("rabbitmqrestapi")){
				return new RabbitMQRestApiComponent(object, dataManager, workflowId);
			}
//			else if(type.equalsIgnoreCase("")){
//				
//			}
			else if(type.equalsIgnoreCase("parallelcomponent")){
				return new ParallelComponent(object, workflowId, dataManager);
			}
			throw new Exception("component_type not supported: "+type);
		}
		catch(Exception e){
			e.printStackTrace();
			throw e;
		}
	}

	public static WorkflowComponent defineComponent(Task t, DataManager dataManager, String workflowId) throws Exception {
		try{
			JSONObject object = new JSONObject(t.getTaskDescription());
			String type = object.getString("component_type");
			if(type.equalsIgnoreCase("split")){
				return new SplitComponent(object, dataManager, workflowId);
			}
			else if(type.equalsIgnoreCase("wait")){
				return new WaitComponent(object, dataManager, workflowId);
			}
			else if(type.equalsIgnoreCase("rabbitmqrestapi")){
				return new RabbitMQRestApiComponent(object, dataManager, workflowId);
			}
//			else if(type.equalsIgnoreCase("")){
//				
//			}
			else if(type.equalsIgnoreCase("parallelcomponent")){
				return new ParallelComponent(object, workflowId, dataManager);
			}
			return null;
		}
		catch(Exception e){
			e.printStackTrace();
			throw e;
		}
	}
	

//	public JSONObject getJSONRepresentation() throws Exception {
//		JSONObject json = new JSONObject();
//		json.put("name", workflowComponentName);
//		json.put("id", workflowComponentId);
//		return json;
//	}
	public abstract JSONObject getJSONRepresentation() throws Exception;

	public String getWorkflowComponentType() {
		return workflowComponentType;
	}

	public void setWorkflowComponentType(String workflowComponentType) {
		this.workflowComponentType = workflowComponentType;
	}

	public String getWorkflowExecutionId() {
		return workflowExecutionId;
	}

	public void setWorkflowExecutionId(String workflowExecutionId) {
		this.workflowExecutionId = workflowExecutionId;
	}
	
	public abstract String startExecuteComponent(String documentId, boolean priority, DataManager dataManager, String outputCallback, String statusCallback, boolean persist, boolean isContent) throws WorkflowException;

	public abstract void setComponentsList(List<WorkflowComponent> componentsList);

}
