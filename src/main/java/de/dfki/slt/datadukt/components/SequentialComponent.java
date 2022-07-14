package de.dfki.slt.datadukt.components;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import de.dfki.slt.datadukt.exceptions.WorkflowException;
import de.dfki.slt.datadukt.persistence.DataManager;
import de.dfki.slt.datadukt.persistence.tasks.Task;

public class SequentialComponent extends WorkflowComponent{

	// Logger object
	Logger logger = Logger.getLogger(SequentialComponent.class);

	WorkflowComponent input;
	WorkflowComponent output;
	List<WorkflowComponent> componentsList;
	
	public SequentialComponent(){
	}
	
	public SequentialComponent(JSONObject jsonDefinition, String workflowId, DataManager dataManager) throws Exception{
		super("Sequential_"+(new Date()).getTime(), "SequentialComponent_"+(new Date()).getTime(),jsonDefinition.getString("component_type"),workflowId);
		try{
//			System.out.println(jsonDefinition.toString(1));
			componentsList = new LinkedList<WorkflowComponent>();
//			System.out.println("Parallel constructor: "+this.workflowId);
			input = WorkflowComponent.defineComponent(jsonDefinition.getJSONObject("input"), dataManager, workflowId);
			//new SplitComponent(jsonDefinition.getJSONObject("input"), dataManager, workflowId);
			output = WorkflowComponent.defineComponent(jsonDefinition.getJSONObject("output"), dataManager, workflowId);
			//output = new WaitComponent(jsonDefinition.getJSONObject("output"), rabbitMQManager, workflowId);
			
			JSONArray arrayTasks = jsonDefinition.getJSONArray("tasks");
			for (int i = 0; i < arrayTasks.length(); i++) {
				JSONObject json = arrayTasks.getJSONObject(i);				
//				System.out.println("DEBUG: SequentialComponent: adding task: "+json.getString("taskId"));
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
		return executeComponent(document, parameters, priority, manager, outputCallback, statusCallback, persist, isContent);
	}

	@Override
	public String executeComponent(String document, HashMap<String, String> parameters, boolean priority, DataManager manager, String outputCallback, String statusCallback, boolean persist, boolean isContent) throws WorkflowException{
		String intermediateResult = input.executeComponentSynchronous(document, parameters, priority, manager, outputCallback, statusCallback, persist, isContent);
		String intermediateResult2; 
		for (WorkflowComponent workflowComponent : componentsList) {
			intermediateResult2 = workflowComponent.executeComponentSynchronous(intermediateResult, parameters, priority, manager, outputCallback, statusCallback, persist, isContent);			
			/** ERROR management when a component has not worked. */
			if(intermediateResult2!=null) {
				intermediateResult = intermediateResult2;
			}
//			System.out.println("===================================\n===================================\n===================================");
//			System.out.println(workflowComponent.workflowComponentId);
//			System.out.println("===================================\n===================================\n===================================");
//			System.out.println("===================================\n===================================\n===================================");
//			System.out.println(intermediateResult);
		}
		intermediateResult = output.executeComponentSynchronous(intermediateResult, parameters, priority, manager, outputCallback, statusCallback, persist, isContent);
		return intermediateResult;
	}

	@Override
	public String executeComponent(String content, boolean priority, DataManager manager, String outputCallback, String statusCallback, boolean persist, boolean isContent) throws WorkflowException {
		return executeComponent(content, null, priority, manager, outputCallback, statusCallback, persist, isContent);
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
