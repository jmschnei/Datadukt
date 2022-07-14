package de.dfki.slt.datadukt.persistence.workflowexecutions;

import java.net.URL;
import java.util.Date;
import java.util.HashMap;
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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import de.dfki.slt.datadukt.communication.rabbitmq.RabbitMQManager;
import de.dfki.slt.datadukt.components.WorkflowComponent;
import de.dfki.slt.datadukt.components.input.InputComponent;
import de.dfki.slt.datadukt.components.output.OutputComponent;
import de.dfki.slt.datadukt.exceptions.WorkflowException;
import de.dfki.slt.datadukt.persistence.tasks.Task;
import de.dfki.slt.datadukt.persistence.workflowtemplates.WorkflowTemplate;
import de.dfki.slt.datadukt.persistence.workflowtemplates.WorkflowTemplateRepository;

/**
 * @author Julian Moreno Schneider jumo04@dfki.de
 *
 */
@Entity
public class WorkflowExecution_old {

//	public enum Status {
//		CREATED, RUNNING, FINISHED, PAUSED
//	}
//
	@Id
	public String workflowExecutionId;
//	String workflowExecutionName;
//
//	@Column(columnDefinition="LONGVARCHAR")
//	String workflowExecutionDescription;
//
//	String userId;
//
//	String projectId;
//
//	//	@OneToMany(cascade=CascadeType.PERSIST)
//	//	@ElementCollection
//	//	List<WorkflowComponent> components;
//	@ElementCollection
//	@Lob
//	private List<String> componentsDefinitions;
//
//	Date creationTime;
//
//	@JsonInclude(JsonInclude.Include.NON_NULL)
//	@Transient
//	List<WorkflowComponent> components;
//
//	@JsonInclude(JsonInclude.Include.NON_NULL)
//	//	@Transient
//	@ElementCollection
//	List<Task> tasks;
//
//	@Transient
//	Status status = Status.CREATED;
//
//	@Transient
//	HashMap<String, String> documents;
//
//	@Transient
//	List<JSONObject> documentsAll = new LinkedList<JSONObject>();
//	@Transient
//	List<JSONObject> documentsResults = new LinkedList<JSONObject>();
//	@Transient
//	List<JSONObject> documentsToExecute = new LinkedList<JSONObject>();
//	@Transient
//	List<JSONObject> documentsRunning = new LinkedList<JSONObject>();
//	@Transient
//	List<JSONObject> documentsDone = new LinkedList<JSONObject>();
//
//	@Transient
//	HashMap<String, HashMap<String,List<JSONObject>>> componentsToDocuments = new HashMap<String, HashMap<String,List<JSONObject>>>();
//
//	@Transient
//	CompletableFuture<String> future = new CompletableFuture<String>();
//
//	@Transient
//	String outputCallback = "";
//	@Transient
//	String statusCallback = "";
//
//	@Transient
//	String outputFormat = "";
//	@Transient
//	String inputFormat = "";
//	@Transient
//	String inputURL = "";
//	@Transient
//	boolean inputPersist = false;
//	@Transient
//	boolean inputContent = true;
//
//	@Transient
//	boolean priority;
//
//	public WorkflowExecution_old() {
//	}
//
//	public WorkflowExecution_old(JSONObject workflowExecutionDescription, RabbitMQManager rabbitMQManager, WorkflowTemplate wt) throws Exception {
//		super();
//
//		this.workflowExecutionDescription = workflowExecutionDescription.toString();
//		componentsDefinitions = new LinkedList<String>();
//		components = new LinkedList<WorkflowComponent>();
//		tasks = new LinkedList<Task>();
//		try{
//			this.workflowExecutionName = workflowExecutionDescription.getString("workflowExecutionName");
//			if(workflowExecutionDescription.has("workflowExecutionId")) {
//				this.workflowExecutionId = workflowExecutionDescription.getString("workflowExecutionId");
////				this.workflowExecutionId += "_"+(new Date()).getTime();
//			}
//			else {
//				this.workflowExecutionId = wt.getWorkflowTemplateId()+"_"+(new Date()).getTime();
//			}
//
//			this.statusCallback = workflowExecutionDescription.has("statusCallback") ? workflowExecutionDescription.getString("statusCallback") : null;
//			this.outputCallback = workflowExecutionDescription.has("outputCallback") ? workflowExecutionDescription.getString("outputCallback") : null;
//
//			try {
//				outputFormat = workflowExecutionDescription.getString("output");
//			}
//			catch(JSONException e) {
//				throw new Exception("Output value is not defined in Workflow Description.");
//			}
//			try {
//				String input = workflowExecutionDescription.getString("input");
//				JSONArray array = new JSONArray(input);
//				inputFormat = array.getJSONObject(0).getString("mimetype");
//				inputURL = array.getJSONObject(0).getString("url");
//				inputPersist = array.getJSONObject(0).getBoolean("persist");
//				inputContent = array.getJSONObject(0).getBoolean("content");
////				System.out.println(inputFormat + "  " + inputURL);
//			}
//			catch(JSONException e) {
//				e.printStackTrace();
//				throw new Exception("Input value is not (properly) defined in Workflow Description: "+e.getMessage());
//			}
//
//			//			System.out.println("WorkflowId (constructor with json): "+this.workflowId);
//
//			InputComponent inputComp = InputComponent.defineInput(inputFormat, inputPersist, inputContent);
//			components.add(inputComp);
//
//			List<Task> tasks = wt.getTasks();
//			for (Task task : tasks) {
//				//TODO If at any moment Tasks have to be implemented as WorkflowExecutionComponents, here is the moment.
////				Task t = new Task(json, rabbitMQManager);//, this.workflowExecutionId);
////				tasks.add(t);
//				System.out.println("Adding TASK: " +task.getTaskId());
//				WorkflowComponent wc = WorkflowComponent.defineComponent(new JSONObject(task.getTaskDescription()), rabbitMQManager, workflowExecutionId);
//				components.add(wc);
//				HashMap<String, List<JSONObject>> componentHash = new HashMap<String, List<JSONObject>>();
//				componentHash.put("waiting", new LinkedList<JSONObject>());
//				componentHash.put("running", new LinkedList<JSONObject>());
//				componentHash.put("done", new LinkedList<JSONObject>());
//				componentsToDocuments.put(wc.getWorkflowComponentId(), componentHash);
//			}
////			List<WorkflowComponent> components = wt.getComponents();
////			for (WorkflowComponent wc : components) {
////				//TODO If at any moment Tasks have to be implemented as WorkflowExecutionComponents, here is the moment.
//////				Task t = new Task(json, rabbitMQManager);//, this.workflowExecutionId);
//////				tasks.add(t);
////				
////				components.add(wc);
////				HashMap<String, List<JSONObject>> componentHash = new HashMap<String, List<JSONObject>>();
////				componentHash.put("waiting", new LinkedList<JSONObject>());
////				componentHash.put("running", new LinkedList<JSONObject>());
////				componentHash.put("done", new LinkedList<JSONObject>());
////				componentsToDocuments.put(wc.getWorkflowComponentId(), componentHash);
////			}
////			JSONArray arrayComponents = workflowExecutionDescription.getJSONArray("components");
////			for (int i = 0; i < arrayComponents.length(); i++) {
////				JSONObject json = arrayComponents.getJSONObject(i);
////				componentsDefinitions.add(json.toString());
////				WorkflowComponent wc = WorkflowComponent.defineComponent(json, rabbitMQManager, this.workflowExecutionId);
////				components.add(wc);
////				Task t = new Task(json, rabbitMQManager);//, this.workflowExecutionId);
////				tasks.add(t);
////				HashMap<String, List<JSONObject>> componentHash = new HashMap<String, List<JSONObject>>();
////				componentHash.put("waiting", new LinkedList<JSONObject>());
////				componentHash.put("running", new LinkedList<JSONObject>());
////				componentHash.put("done", new LinkedList<JSONObject>());
////				componentsToDocuments.put(wc.getWorkflowComponentId(), componentHash);
////			}
//			OutputComponent outputComp = OutputComponent.defineOutput(outputFormat);
//			components.add(outputComp);
//
//			System.out.println("NUMBER OF COMPONENTS in workflow: "+components.size());
//
//		}
//		catch(Exception e){
//			e.printStackTrace();
//			throw e;
//		}
//		//		System.out.println("COMPONENTS SIZE IN WORKFLOW CONSTRUCTOR: "+components.size());
//	}
//
//	public WorkflowExecution_old(String workflowString, RabbitMQManager rabbitMQManager, WorkflowTemplate wt) throws Exception {
//		this(new JSONObject(workflowString),rabbitMQManager, wt);
//	}
//
//	public boolean execute_vOLD(String documentId, boolean priority, RabbitMQManager manager, String outputCallback, String statusCallback) throws Exception {
//		if(components==null) {
//			String msg = "Components List in Workflow has to be established [not NULL] for execution.";
//			throw new WorkflowException(msg);
//		}
//		if(components.isEmpty()) {
//			String msg = "Components List in Workflow has to be established [not empty] for execution.";
//			throw new WorkflowException(msg);
//		}
//		Object monitoringObject = new Object();
//		for (WorkflowComponent wfc : components) {
//			//			System.out.println(wfc.getWorkflowComponentId());
//			//			System.out.println(wfc.getClass());
//			//			wfc.executeComponent(documentId, priority);
//			String partialResult = wfc.executeComponent(documentId, priority, manager,outputCallback,statusCallback,inputPersist,inputContent);
//			if(partialResult==null){
//				return false;
//			}
//		}
//		return true;
//	}
//
//
//	public void reestablishComponents(RabbitMQManager rabbitMQManager) {
//		try {
//			components = new LinkedList<WorkflowComponent>();
//			//			System.out.println("Workflow reestablishment: "+workflowId);
//			for (String wcd : componentsDefinitions) {
//				WorkflowComponent wc = WorkflowComponent.defineComponent(new JSONObject(wcd),rabbitMQManager, this.workflowExecutionId);
//				components.add(wc);
//				HashMap<String, List<JSONObject>> componentHash = new HashMap<String, List<JSONObject>>();
//				componentHash.put("waiting", new LinkedList<JSONObject>());
//				componentHash.put("running", new LinkedList<JSONObject>());
//				componentHash.put("done", new LinkedList<JSONObject>());
//				componentsToDocuments.put(wc.getWorkflowComponentId(), componentHash);
//			}
//		}catch(Exception e) {
//			e.printStackTrace();
//			System.out.println("----------------------------");
//			System.out.println("ERROR in reestablishing componentss");
//			System.out.println("----------------------------");
//		}
//	}
//
//	public String executeComponents(String input, RabbitMQManager manager) {
//		String serviceResult = input;		
//		try {
//			int counter = 0;
//			for (WorkflowComponent wfc : components) {
//				counter++;
//				String auxStatus = "RUNNING --> "+counter+"/"+components.size()+" component: "+wfc.getWorkflowComponentId();
//				System.out.println(auxStatus);
//				System.out.println(wfc.getClass());
//				System.out.println(wfc.getJSONRepresentation().toString(1));
//				status = Status.RUNNING;
//				//					System.out.println(wfc.getWorkflowComponentId());
//				//					System.out.println(wfc.getClass());
//				//					wfc.executeComponent(documentId, priority);
//				serviceResult = wfc.executeComponent(serviceResult, priority, manager, outputCallback, statusCallback, inputPersist, inputContent);
//				if(serviceResult==null){
//					return null;
//				}
//				//					if(partialResult==false){
//				//						return "ERROR";
//				//					}
//				//					System.out.println("Registering status change: ");
//				if(statusCallback!=null && !statusCallback.equalsIgnoreCase("")) {
//					System.out.println(statusCallback);
//					HttpResponse<String> statusCallbackResponse = Unirest.post(statusCallback).queryString("status", auxStatus).body(auxStatus).asString();
//					if(statusCallbackResponse.getStatus()!=200) {
//						throw new Exception("Error reporting statusCallback from workflow [" + workflowExecutionId + "] with ERROR: " + statusCallbackResponse.getStatus() + " ["+statusCallbackResponse.getBody()+"]");
//					}
//					//						System.out.println(statusCallbackResponse.getBody());
//				}
//			}
//		}
//		catch(Exception e) {
//			e.printStackTrace();
//		}
//		status = Status.FINISHED;
//		return serviceResult;
//	}
//
//	public void notifyOutput(String s) throws Exception {
//		future.complete(s);
//		status = Status.FINISHED;
//		if(outputCallback!=null) {
//			HttpResponse<String> callbackResponse = Unirest.post(outputCallback).body(s).asString();
//			if(callbackResponse.getStatus()!=200) {
//				throw new Exception("Error reporting outputCallback from workflow [" + workflowExecutionId + "] with ERROR: " + callbackResponse.getStatus() + " ["+callbackResponse.getBody()+"]");
//			}
//		}
//	}
//
//	public String execute(boolean priority, RabbitMQManager manager) throws Exception {
//		if(components==null) {
//			String msg = "Components List in Workflow Execution has to be established [not NULL] for execution.";
//			throw new WorkflowException(msg);
//		}
//		if(components.isEmpty()) {
//			String msg = "Components List in Workflow Execution has to be established [not empty] for execution.";
//			throw new WorkflowException(msg);
//		}
//		status = Status.RUNNING;
//		System.out.println("EXECUTING THE WORKFLOW.");
//
////		CompletableFuture<String> contentFuture = CompletableFuture.supplyAsync(() -> {
////			String fileContent = "";
////			try {
////				fileContent = IOUtils.toString(new URL(inputURL), "utf-8");
////				System.out.println("WE REACH AFTER READING THE FILE: "+fileContent);
////			} catch (Exception e) {
////				e.printStackTrace();
////				fileContent = null;
////			}
////			return fileContent;
////		});
////
////		String fileContent3 = ""; 
////		try {
////			fileContent3 = contentFuture.get(5, TimeUnit.SECONDS);
////		}
////		catch(TimeoutException e) {
////			throw new Exception("EXCEPTION: Reading file ["+inputURL+"] failed.");
////		}
////
////		String fileContent2 = fileContent3;
//		
//		String fileContent2 = inputURL;
//		CompletableFuture.supplyAsync(() -> {
//			String s = "";
//			try {
//				System.out.println("EXEUTING THE FIRST METHOD IN ASYNCHRONOUS MODE.");
//				s = executeComponents(fileContent2,manager);
//			} catch (Exception e) {
//				e.printStackTrace();
//				s = "EXCEPTION";
//			}
//			return s;
//		}).thenAccept(t -> {
//			try {
//				notifyOutput(t);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		});
//		return status.toString();
//	}
//
//	public JSONObject getJSONRepresentation() throws Exception{
//		JSONObject json = new JSONObject();
//		json.put("workflowExecutionId", workflowExecutionId);
//		json.put("workflowExecutionName", workflowExecutionName);
//		JSONArray array = new JSONArray();
//		for (String string: componentsDefinitions) {
//			array.put(new JSONObject(string));
//		}
//		json.put("components", array);
//		json.put("creationTime", creationTime);
//		json.put("status", status);
//		JSONObject inputJSON = new JSONObject();
//		inputJSON.put("format", inputFormat);
//		inputJSON.put("url", inputURL);
//		json.put("input", inputJSON);
//		json.put("output", outputFormat);
//		json.put("statusCallback", statusCallback);
//		json.put("outputCallback", outputCallback);
//		return json;
//	}
//
//	public String getStatus() throws Exception {
//		JSONObject status = new JSONObject();
//		JSONObject workflowstatus = new JSONObject();
//		workflowstatus.put("status", this.status.name());
//		status.put("workflowexecution", workflowstatus);
//		return status.toString();
//	}
//
//	public String getOutput() {
//		String outputResult = null;
//		try {
//			outputResult = future.get();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		} catch (ExecutionException e) {
//			e.printStackTrace();
//		}
//		return outputResult;
//	}
//
//	public static void main(String[] args) {
//		String nerTrainingData = 
//				"The Oxford Companion to Philosophy says , \" there is no single defining position that all anarchists hold , and those considered anarchists at best share a certain family resemblance . \"\n" +
//						"In the end , for anarchist historian <START:PER> Daniel Guerin <END> \" Some anarchists are more individualistic than social , some more social than individualistic .\n" +
//						"From this climate <START:PER> William Godwin <END> developed what many consider the first expression of modern anarchist thought .\n" +
//						"<START:PER> Godwin <END> was , according to <START:PER> Peter Kropotkin <END> , \" the first to formulate the political and economical conceptions of anarchism , even though he did not give that name to the ideas developed in his work \" , while <START:PER> Godwin <END> attached his anarchist ideas to an early <START:PER> Edmund Burke <END> .\n"
//						;
//		System.out.println(nerTrainingData);
//	}
}
