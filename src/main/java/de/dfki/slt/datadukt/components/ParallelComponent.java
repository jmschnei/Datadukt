package de.dfki.slt.datadukt.components;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import de.dfki.slt.datadukt.exceptions.WorkflowException;
import de.dfki.slt.datadukt.persistence.DataManager;
import de.dfki.slt.datadukt.persistence.tasks.Task;

public class ParallelComponent extends WorkflowComponent{

	SplitComponent input;
	WorkflowComponent output;
	List<WorkflowComponent> componentsList;
	
	public ParallelComponent(){
	}
	
	public ParallelComponent(JSONObject jsonDefinition, String workflowId, DataManager dataManager) throws Exception{
//	public ParallelComponent(JSONObject jsonDefinition, RabbitMQManager rabbitMQManager, String workflowId) throws Exception{
//		super(jsonDefinition.getString("componentName"), jsonDefinition.getString("componentId"),jsonDefinition.getString("component_type"),workflowId);
		super("Parallel_"+(new Date()).getTime(), "ParallelComponent_"+(new Date()).getTime(),jsonDefinition.getString("component_type"),workflowId);
		try{
//			System.out.println(jsonDefinition.toString(1));
			componentsList = new LinkedList<WorkflowComponent>();
//			System.out.println("Parallel constructor: "+this.workflowId);
			input = new SplitComponent(jsonDefinition.getJSONObject("input"), dataManager, workflowId);
			output = WorkflowComponent.defineComponent(jsonDefinition.getJSONObject("output"), dataManager, workflowId);
			//output = new WaitComponent(jsonDefinition.getJSONObject("output"), rabbitMQManager, workflowId);
			
			JSONArray arrayTasks = jsonDefinition.getJSONArray("tasks");
			for (int i = 0; i < arrayTasks.length(); i++) {
				JSONObject json = arrayTasks.getJSONObject(i);
//				Task t = new Task(json, rabbitMQManager);
				Task t = dataManager.taskManager.findOneByTaskId(json.getString("taskId"));
				if(t==null) {
					throw new Exception("The Task ["+json.getString("taskId")+"] is NOT available in Parallel Component.");
				}
//				System.out.println(t.getJSONRepresentation().toString(1));
//				System.out.println("TASK ID generating workflow template: " + json.getString("taskId"));				
				WorkflowComponent wc = WorkflowComponent.defineComponent(new JSONObject(t.getTaskDescription()), dataManager, workflowId);
				componentsList.add(wc);
			}			
//			System.out.println("LIST OF COMPOENENTS IN PARALLEL COMPONENT: "+componentsList.size());
			input.setComponentsList(componentsList);
			output.setComponentsList(componentsList);
		}
		catch(Exception e){
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public String executeComponentSynchronous(String document, HashMap<String, String> parameters, boolean priority, DataManager manager, String outputCallback, String statusCallback, boolean persist, boolean isContent) throws WorkflowException{
		System.out.println("Method not supported in Parallel Component.");
		return null;
	}

	@Override
	public String executeComponent(String document, HashMap<String, String> parameters, boolean priority, DataManager manager, String outputCallback, String statusCallback, boolean persist, boolean isContent) throws WorkflowException{
//		input.setComponentsList(componentsList.subList(0, componentsList.size()));
		if(output.startExecuteComponent(document, priority,manager,outputCallback, statusCallback, persist, isContent)!=null) {
			if(input.executeComponent(document, parameters, priority, manager,outputCallback, statusCallback, persist, isContent)!=null) {
	//			output.setComponentsList(componentsList.subList(0, componentsList.size()));
				String result = output.executeComponent(document, parameters, priority,manager,outputCallback, statusCallback, persist, isContent);
				if(result!=null) {
					
					//TODO if content is not an URI, it has to be combined by the outputComponent.
					
					System.out.println("Result of parallel component: "+result);
					
					return result;
				}
				else {
					String msg = "Error executing OUTPUT in ParallelComponent.";
					throw new WorkflowException(msg);
				}
			}
			else {
				String msg = "Error executing INPUT in ParallelComponent.";
				throw new WorkflowException(msg);
			}
		}
		else {
			String msg = "Error executing OUTPUT Start in ParallelComponent.";
			throw new WorkflowException(msg);
		}

	}

	@Override
	public String executeComponent(String content, boolean priority, DataManager manager, String outputCallback, String statusCallback, boolean persist, boolean isContent) throws WorkflowException {
//		input.setComponentsList(componentsList.subList(0, componentsList.size()));
		if(output.startExecuteComponent(content, priority,manager,outputCallback, statusCallback, persist, isContent)!=null) {
			if(input.executeComponent(content, priority,manager,outputCallback, statusCallback, persist, isContent)!=null) {
	//			output.setComponentsList(componentsList.subList(0, componentsList.size()));
				String result = output.executeComponent(content, priority,manager,outputCallback, statusCallback, persist, isContent);
				if(result!=null) {
					
					//TODO if content is not an URI, it has to be combined by the outputComponent.
					
					System.out.println("Result of parallel component: "+result);
					
					return result;
				}
				else {
					String msg = "Error executing OUTPUT in ParallelComponent.";
					throw new WorkflowException(msg);
				}
			}
			else {
				String msg = "Error executing INPUT in ParallelComponent.";
				throw new WorkflowException(msg);
			}
		}
		else {
			String msg = "Error executing OUTPUT Start in ParallelComponent.";
			throw new WorkflowException(msg);
		}
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


	public void setComponentsList(List<WorkflowComponent> componentsList) {
	}

}
