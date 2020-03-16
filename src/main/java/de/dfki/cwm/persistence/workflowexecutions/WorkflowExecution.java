package de.dfki.cwm.persistence.workflowexecutions;

import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Transient;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import de.dfki.cwm.components.WorkflowComponent;
import de.dfki.cwm.components.input.InputComponent;
import de.dfki.cwm.components.output.OutputComponent;
import de.dfki.cwm.data.Format;
import de.dfki.cwm.exceptions.WorkflowException;
import de.dfki.cwm.persistence.DataManager;
import de.dfki.cwm.persistence.tasks.Task;
import de.dfki.cwm.persistence.workflowtemplates.WorkflowTemplate;

/**
 * @author Julian Moreno Schneider jumo04@dfki.de
 *
 */
@Entity
public class WorkflowExecution {

	public enum Status {
		CREATED, RUNNING, FINISHED, PAUSED
	}

	@Id
	public String workflowExecutionId;
	String workflowExecutionName;

	@Column(columnDefinition="LONGVARCHAR")
	String workflowExecutionDescription;

	String userId;

	String projectId;

	//	@OneToMany(cascade=CascadeType.PERSIST)
	//	@ElementCollection
	//	List<WorkflowComponent> components;
	@ElementCollection
	@Lob
	private List<String> componentsDefinitions;

	Date creationTime;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Transient
	List<WorkflowComponent> components;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	//	@Transient
	@Transient
	List<Task> tasks;

	@ElementCollection
	List<String> tasksIds;

	@Transient
	Status status = Status.CREATED;

	@Transient
	HashMap<String, String> documents;

	@Transient
	List<JSONObject> documentsAll = new LinkedList<JSONObject>();
	@Transient
	List<JSONObject> documentsResults = new LinkedList<JSONObject>();
	@Transient
	List<JSONObject> documentsToExecute = new LinkedList<JSONObject>();
	@Transient
	List<JSONObject> documentsRunning = new LinkedList<JSONObject>();
	@Transient
	List<JSONObject> documentsDone = new LinkedList<JSONObject>();

	@Transient
	HashMap<String, HashMap<String,List<JSONObject>>> componentsToDocuments = new HashMap<String, HashMap<String,List<JSONObject>>>();

	@Transient
	CompletableFuture<String> future = new CompletableFuture<String>();

	@Transient
	String outputCallback = "";
	@Transient
	String statusCallback = "";

	@Transient
	String sOutputFormat = "";
	@Transient
	Format outputFormat = Format.UNK;
	@Transient
	String sInputFormat = "";
	@Transient
	Format inputFormat = Format.UNK;
	@Transient
	String inputLanguage = "";
	@Transient
	String inputURL = "";
	@Transient
	boolean inputPersist = false;
	@Transient
	boolean inputContent = true;

	@Transient
	boolean priority;

	@Transient
	JSONObject jsonParameters = null;
	
	@Transient
	HashMap<String, String> parameters = null;
	
	public WorkflowExecution() {
	}

	public WorkflowExecution(JSONObject workflowExecutionDescription, DataManager dataManager, WorkflowTemplate wt) throws Exception {
		super();
		this.workflowExecutionDescription = workflowExecutionDescription.toString();
		componentsDefinitions = new LinkedList<String>();
		components = new LinkedList<WorkflowComponent>();
		tasks = new LinkedList<Task>();
		tasksIds = new LinkedList<String>();
		try{
			this.workflowExecutionName = workflowExecutionDescription.getString("workflowExecutionName");
			if(workflowExecutionDescription.has("workflowExecutionId")) {
				this.workflowExecutionId = workflowExecutionDescription.getString("workflowExecutionId");
//				this.workflowExecutionId += "_"+(new Date()).getTime();
			}
			else {
				this.workflowExecutionId = wt.getWorkflowTemplateId()+"_"+(new Date()).getTime();
			}

			this.statusCallback = workflowExecutionDescription.has("statusCallback") ? workflowExecutionDescription.getString("statusCallback") : null;
			this.outputCallback = workflowExecutionDescription.has("outputCallback") ? workflowExecutionDescription.getString("outputCallback") : null;

			if(workflowExecutionDescription.has("parameters")) {
				parameters = new HashMap<String,String>();
				jsonParameters = workflowExecutionDescription.getJSONObject("parameters");
				Iterator<String> it = jsonParameters.keys();
				while(it.hasNext()) {
					String key = it.next();
					parameters.put(key, jsonParameters.getString(key));
				}
			}
			
			try {
				sOutputFormat = workflowExecutionDescription.getString("output");
				outputFormat = Format.getFormat(sOutputFormat);
			}
			catch(JSONException e) {
				throw new Exception("Output value is not defined in Workflow Description.");
			}
			try {
				sInputFormat = workflowExecutionDescription.getString("input");
				inputFormat = Format.getFormat(sInputFormat);
				inputPersist = workflowExecutionDescription.getBoolean("persist");
				inputContent = workflowExecutionDescription.getBoolean("isContent");
			}
			catch(JSONException e) {
				e.printStackTrace();
				throw new Exception("Input value is not (properly) defined in Workflow Description: "+e.getMessage());
			}
			try {
				inputLanguage = workflowExecutionDescription.getString("language");
			}
			catch(JSONException e) {
				inputLanguage = null;
			}

			//			System.out.println("WorkflowId (constructor with json): "+this.workflowId);

			InputComponent inputComp = InputComponent.defineInput(sInputFormat, inputLanguage, inputPersist, inputContent);
			components.add(inputComp);

//			System.out.println(wt.getWorkflowTemplateDescription());
			JSONObject workflowDescription = new JSONObject(wt.getWorkflowTemplateDescription());
			JSONArray arrayTasks = workflowDescription.getJSONArray("tasks");
			for (int i = 0; i < arrayTasks.length(); i++) {
				JSONObject json = arrayTasks.getJSONObject(i);
//				Task t = new Task(json, rabbitMQManager);
//				System.out.println(json.toString(1));
				Task t = dataManager.taskManager.findOneByTaskId(json.getString("taskId"));
				if(t==null) {
					throw new Exception("The Task ["+json.getString("taskId")+"] used in template ["+workflowExecutionId+"] is NOT available.");
				}
				
				JSONObject object = new JSONObject(t.getTaskDescription());
				if(json.has("features")) {
					JSONObject features = json.getJSONObject("features");
//					System.out.println("FEATURES: "+features.toString(1));
					Iterator<String> it = features.keys();
					while (it.hasNext()) {
						String s = (String) it.next();
						object.put(s, features.get(s));
					}
				}
//				System.out.println(object.toString(1));
				WorkflowComponent wc = WorkflowComponent.defineComponent(object, dataManager, workflowExecutionId);
				components.add(wc);
				HashMap<String, List<JSONObject>> componentHash = new HashMap<String, List<JSONObject>>();
				componentHash.put("waiting", new LinkedList<JSONObject>());
				componentHash.put("running", new LinkedList<JSONObject>());
				componentHash.put("done", new LinkedList<JSONObject>());
				componentsToDocuments.put(wc.getWorkflowComponentId(), componentHash);
			}
			
//			List<Task> tasks = wt.getTasks();
//			for (Task task : tasks) {
//				System.out.println("Adding TASK: " +task.getTaskId());
//				System.out.println(task.getTaskDescription());
//				WorkflowComponent wc = WorkflowComponent.defineComponent(new JSONObject(task.getTaskDescription()), rabbitMQManager, workflowExecutionId);
//				components.add(wc);
//				HashMap<String, List<JSONObject>> componentHash = new HashMap<String, List<JSONObject>>();
//				componentHash.put("waiting", new LinkedList<JSONObject>());
//				componentHash.put("running", new LinkedList<JSONObject>());
//				componentHash.put("done", new LinkedList<JSONObject>());
//				componentsToDocuments.put(wc.getWorkflowComponentId(), componentHash);
//			}

			OutputComponent outputComp = OutputComponent.defineOutput(sOutputFormat);
			components.add(outputComp);

			System.out.println("NUMBER OF COMPONENTS in workflow: "+components.size());

		}
		catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		//		System.out.println("COMPONENTS SIZE IN WORKFLOW CONSTRUCTOR: "+components.size());
	}

	public WorkflowExecution(String workflowString, DataManager dataManager, WorkflowTemplate wt) throws Exception {
		this(new JSONObject(workflowString),dataManager, wt);
	}

	public void reestablishComponents(DataManager dataManager) {
		try {
			components = new LinkedList<WorkflowComponent>();
			//			System.out.println("Workflow reestablishment: "+workflowId);
			for (String wcd : componentsDefinitions) {
				WorkflowComponent wc = WorkflowComponent.defineComponent(new JSONObject(wcd),dataManager, this.workflowExecutionId);
				components.add(wc);
				HashMap<String, List<JSONObject>> componentHash = new HashMap<String, List<JSONObject>>();
				componentHash.put("waiting", new LinkedList<JSONObject>());
				componentHash.put("running", new LinkedList<JSONObject>());
				componentHash.put("done", new LinkedList<JSONObject>());
				componentsToDocuments.put(wc.getWorkflowComponentId(), componentHash);
			}
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println("----------------------------");
			System.out.println("ERROR in reestablishing componentss");
			System.out.println("----------------------------");
		}
	}

	public String executeComponents(String input, DataManager manager) {
		String serviceResult = input;
		try {
			int counter = 0;
			for (WorkflowComponent wfc : components) {
				counter++;
				String auxStatus = "RUNNING --> "+counter+"/"+components.size()+" component: "+wfc.getWorkflowComponentId();
				System.out.println(auxStatus);
//				System.out.println(wfc.getClass());
//				System.out.println(wfc.getJSONRepresentation().toString(1));
				status = Status.RUNNING;
				//					System.out.println(wfc.getWorkflowComponentId());
				//					System.out.println(wfc.getClass());
				//					wfc.executeComponent(documentId, priority);
				System.out.println("Service input ("+wfc.getName()+"--"+wfc.getWorkflowComponentName()+") result: "+serviceResult);
				if(parameters!=null && !parameters.isEmpty()) {
					serviceResult = wfc.executeComponent(serviceResult, parameters, priority, manager, outputCallback, statusCallback, inputPersist, inputContent);
				}
				else {
					serviceResult = wfc.executeComponent(serviceResult, priority, manager, outputCallback, statusCallback, inputPersist, inputContent);
				}
				System.out.println("Service output ("+wfc.getName()+"--"+wfc.getWorkflowComponentName()+") result: "+serviceResult);
				if(serviceResult==null){
					return null;
				}
				//					if(partialResult==false){
				//						return "ERROR";
				//					}
				//					System.out.println("Registering status change: ");
				if(statusCallback!=null && !statusCallback.equalsIgnoreCase("")) {
					System.out.println(statusCallback);
					HttpResponse<String> statusCallbackResponse = Unirest.post(statusCallback).queryString("status", auxStatus).body(auxStatus).asString();
					if(statusCallbackResponse.getStatus()!=200) {
						throw new Exception("Error reporting statusCallback from workflow [" + workflowExecutionId + "] with ERROR: " + statusCallbackResponse.getStatus() + " ["+statusCallbackResponse.getBody()+"]");
					}
					//						System.out.println(statusCallbackResponse.getBody());
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		status = Status.FINISHED;
		return serviceResult;
	}

	public void notifyOutput(String s) throws Exception {
		future.complete(s);
		status = Status.FINISHED;
		if(outputCallback!=null) {
			HttpResponse<String> callbackResponse = Unirest.post(outputCallback).body(s).asString();
			if(callbackResponse.getStatus()!=200) {
				throw new Exception("Error reporting outputCallback from workflow [" + workflowExecutionId + "] with ERROR: " + callbackResponse.getStatus() + " ["+callbackResponse.getBody()+"]");
			}
		}
	}

	public String execute(boolean priority, DataManager manager) throws Exception {
		if(components==null) {
			String msg = "Components List in Workflow Execution has to be established [not NULL] for execution.";
			throw new WorkflowException(msg);
		}
		if(components.isEmpty()) {
			String msg = "Components List in Workflow Execution has to be established [not empty] for execution.";
			throw new WorkflowException(msg);
		}
		status = Status.RUNNING;
		System.out.println("EXECUTING THE WORKFLOW.");

//		CompletableFuture<String> contentFuture = CompletableFuture.supplyAsync(() -> {
//			String fileContent = "";
//			try {
//				fileContent = IOUtils.toString(new URL(inputURL), "utf-8");
//				System.out.println("WE REACH AFTER READING THE FILE: "+fileContent);
//			} catch (Exception e) {
//				e.printStackTrace();
//				fileContent = null;
//			}
//			return fileContent;
//		});
//
//		String fileContent3 = ""; 
//		try {
//			fileContent3 = contentFuture.get(5, TimeUnit.SECONDS);
//		}
//		catch(TimeoutException e) {
//			throw new Exception("EXCEPTION: Reading file ["+inputURL+"] failed.");
//		}
//
//		String fileContent2 = fileContent3;
		
		String fileContent2 = inputURL;
		CompletableFuture.supplyAsync(() -> {
			String s = "";
			try {
				System.out.println("EXEUTING THE FIRST METHOD IN ASYNCHRONOUS MODE.");
				s = executeComponents(fileContent2,manager);
			} catch (Exception e) {
				e.printStackTrace();
				s = "EXCEPTION";
			}
			return s;
		}).thenAccept(t -> {
			try {
				notifyOutput(t);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		return status.toString();
	}

	public String execute(String content, boolean priority, DataManager manager) throws Exception {
		if(components==null) {
			String msg = "Components List in Workflow Execution has to be established [not NULL] for execution.";
			throw new WorkflowException(msg);
		}
		if(components.isEmpty()) {
			String msg = "Components List in Workflow Execution has to be established [not empty] for execution.";
			throw new WorkflowException(msg);
		}
		status = Status.RUNNING;
//		System.out.println("EXECUTING THE WORKFLOW.");

		String fileContent2 = content;
		CompletableFuture.supplyAsync(() -> {
			String s = "";
			try {
				System.out.println("EXEUTING THE FIRST METHOD IN ASYNCHRONOUS MODE.");
//				System.out.println(fileContent2);
				s = executeComponents(fileContent2,manager);
			} catch (Exception e) {
				e.printStackTrace();
				s = "EXCEPTION";
			}
			return s;
		}).thenAccept(t -> {
			try {
				notifyOutput(t);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		return status.toString();
	}

	public JSONObject getJSONRepresentation() throws Exception{
		JSONObject json = new JSONObject();
		json.put("workflowExecutionId", workflowExecutionId);
		json.put("workflowExecutionName", workflowExecutionName);
		JSONArray array = new JSONArray();
		for (String string: componentsDefinitions) {
			array.put(new JSONObject(string));
		}
		json.put("components", array);
		json.put("creationTime", creationTime);
		json.put("status", status);
		JSONObject inputJSON = new JSONObject();
		inputJSON.put("format", sInputFormat);
		inputJSON.put("url", inputURL);
		json.put("input", inputJSON);
		json.put("output", sOutputFormat);
		json.put("statusCallback", statusCallback);
		json.put("outputCallback", outputCallback);
		return json;
	}

	public String getStatus() throws Exception {
//		JSONObject status = new JSONObject();
//		JSONObject workflowstatus = new JSONObject();
//		workflowstatus.put("status", this.status.name());
//		status.put("workflowexecution", workflowstatus);
//		return status.toString();
		return this.status.name();
	}

	public String getOutput() {
		String outputResult = null;
		try {
			outputResult = future.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return outputResult;
	}

	public static void main(String[] args) {
		String nerTrainingData = 
				"The Oxford Companion to Philosophy says , \" there is no single defining position that all anarchists hold , and those considered anarchists at best share a certain family resemblance . \"\n" +
						"In the end , for anarchist historian <START:PER> Daniel Guerin <END> \" Some anarchists are more individualistic than social , some more social than individualistic .\n" +
						"From this climate <START:PER> William Godwin <END> developed what many consider the first expression of modern anarchist thought .\n" +
						"<START:PER> Godwin <END> was , according to <START:PER> Peter Kropotkin <END> , \" the first to formulate the political and economical conceptions of anarchism , even though he did not give that name to the ideas developed in his work \" , while <START:PER> Godwin <END> attached his anarchist ideas to an early <START:PER> Edmund Burke <END> .\n"
						;
		System.out.println(nerTrainingData);
	}
}