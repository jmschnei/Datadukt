package de.dfki.cwm.persistence.workflowinstances;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Transient;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import de.dfki.cwm.components.SequentialComponent;
import de.dfki.cwm.components.WorkflowComponent;
import de.dfki.cwm.components.input.InputComponent;
import de.dfki.cwm.components.output.OutputComponent;
import de.dfki.cwm.data.Format;
import de.dfki.cwm.data.documents.WMDocument;
import de.dfki.cwm.exceptions.WorkflowException;
import de.dfki.cwm.persistence.DataManager;
import de.dfki.cwm.persistence.tasks.Task;
import de.dfki.cwm.persistence.workflowtemplates.WorkflowTemplate;

/**
 * @author Julian Moreno Schneider julian.moreno_schneider@dfki.de
 * @modified_by 
 * @project CurationWorkflowManager
 * @date 12.07.2021
 * @date_modified 
 * @company DFKI
 * @description Class representing the
 *
 */
@Entity
public class WorkflowInstance implements Serializable {

	// Logger object
	@Transient
	Logger logger = Logger.getLogger(SequentialComponent.class);

	public enum Status {
		CREATED, INSTANCIATED
	}

	@Id
	public String workflowInstanceId;
	String workflowInstanceName;

	@Column(columnDefinition="LONGVARCHAR")
	String workflowInstanceDescription;

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
	
	public WorkflowInstance() {
	}

	public WorkflowInstance(JSONObject workflowExecutionDescription, DataManager dataManager, WorkflowTemplate wt) throws Exception {
		super();
		this.workflowInstanceDescription = workflowExecutionDescription.toString();
		componentsDefinitions = new LinkedList<String>();
		components = new LinkedList<WorkflowComponent>();
		tasks = new LinkedList<Task>();
		tasksIds = new LinkedList<String>();
		try{
			/**
			 * TODO include user and project from the JSON if comming.
			 */
			this.workflowInstanceName = workflowExecutionDescription.getString("workflowExecutionName");			
			this.workflowInstanceId  = (workflowExecutionDescription.has("workflowExecutionId")) 
					? workflowExecutionDescription.getString("workflowExecutionId") 
					: wt.getWorkflowTemplateId()+"_"+(new Date()).getTime();

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
			inputLanguage = (workflowExecutionDescription.has("language")) ? workflowExecutionDescription.getString("language") : null;

			InputComponent inputComp = InputComponent.defineInput(sInputFormat, inputLanguage, inputPersist, inputContent);
			components.add(inputComp);

			JSONObject workflowDescription = new JSONObject(wt.getWorkflowTemplateDescription());
			JSONArray arrayTasks = workflowDescription.getJSONArray("tasks");
			for (int i = 0; i < arrayTasks.length(); i++) {
				JSONObject json = arrayTasks.getJSONObject(i);
//				Task t = new Task(json, rabbitMQManager);
//				System.out.println(json.toString(1));
				Task t = dataManager.taskManager.findOneByTaskId(json.getString("taskId"));
				if(t==null) {
					throw new Exception("The Task ["+json.getString("taskId")+"] used in template ["+workflowInstanceId+"] is NOT available.");
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
				WorkflowComponent wc = WorkflowComponent.defineComponent(object, dataManager, workflowInstanceId);
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
//			System.out.println("NUMBER OF COMPONENTS in workflow: "+components.size());
			logger.info("NUMBER OF COMPONENTS in workflow: "+components.size());

		}
		catch(Exception e){
			e.printStackTrace();
			throw e;
		}
	}

	public WorkflowInstance(String workflowString, DataManager dataManager, WorkflowTemplate wt) throws Exception {
		this(new JSONObject(workflowString),dataManager, wt);
	}

	public JSONObject getJSONRepresentation() throws Exception{
		JSONObject json = new JSONObject();
		json.put("workflowExecutionId", workflowInstanceId);
		json.put("workflowExecutionName", workflowInstanceName);
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
		return this.status.name();
	}

}
