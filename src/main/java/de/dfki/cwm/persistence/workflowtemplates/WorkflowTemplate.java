package de.dfki.cwm.persistence.workflowtemplates;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonInclude;

import de.dfki.cwm.communication.rabbitmq.RabbitMQManager;
import de.dfki.cwm.components.WorkflowComponent;
import de.dfki.cwm.persistence.tasks.Task;
import de.dfki.cwm.persistence.tasks.TaskManager;
import de.dfki.cwm.persistence.tasks.TaskRepository;
import de.dfki.eservices.exceptions.WorkflowException;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Julian Moreno Schneider jumo04@dfki.de
 *
 */
@Entity
public class WorkflowTemplate {

	public enum Status {
		CREATED
	}

	//	@JoinColumn(name = "indexId")
	@Id
	String workflowTemplateId;
	String name;

	@Column(columnDefinition="LONGVARCHAR")
	String workflowTemplateDescription;

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

	//@ElementCollection
//	@OneToMany(cascade = CascadeType.ALL,
//			fetch = FetchType.LAZY,
//			mappedBy = "workflowTemplate")
	@Transient
	List<Task> tasks;

	@ElementCollection
	List<String> tasksIds;

	public WorkflowTemplate() {
	}

	public WorkflowTemplate(String workflowTemplateId, String templateName, String workflowTemplateDescription,
			List<String> componentsDefinitions, Date creationTime) {
		super();
		this.workflowTemplateId = workflowTemplateId;
		this.name = templateName;
		this.workflowTemplateDescription = workflowTemplateDescription;
		this.componentsDefinitions = componentsDefinitions;
		this.creationTime = creationTime;
	}

	public WorkflowTemplate(JSONObject workflowDescription, RabbitMQManager rabbitMQManager, TaskManager taskManager) throws Exception {
		super();
		this.workflowTemplateDescription = workflowDescription.toString();
		componentsDefinitions = new LinkedList<String>();
		components = new LinkedList<WorkflowComponent>();
		tasks = new LinkedList<Task>();
		tasksIds = new LinkedList<String>();
		try{
			this.name = workflowDescription.getString("workflowTemplateName");
			this.workflowTemplateId = workflowDescription.getString("workflowTemplateId");

//			System.out.println("WorkflowId (constructor with json): "+this.workflowId);
			
//			JSONArray arrayComponents = workflowDescription.getJSONArray("components");
//			for (int i = 0; i < arrayComponents.length(); i++) {
//				JSONObject json = arrayComponents.getJSONObject(i);
//				componentsDefinitions.add(json.toString());
//				WorkflowComponent wc = WorkflowComponent.defineComponent(json, rabbitMQManager, this.workflowTemplateId);
//				components.add(wc);
//			}
			
//			List<Task> tasks2 = taskRepository.findAll();
//			System.out.println("TASK SIZE:"+tasks.size());
			JSONArray arrayTasks = workflowDescription.getJSONArray("tasks");
			for (int i = 0; i < arrayTasks.length(); i++) {
				JSONObject json = arrayTasks.getJSONObject(i);
//				Task t = new Task(json, rabbitMQManager);
				Task t = taskManager.findOneByTaskId(json.getString("taskId"));
				if(t==null) {
					throw new Exception("The Task ["+json.getString("taskId")+"] used in template ["+workflowTemplateId+"] is NOT available.");
				}
//				System.out.println("TASK ID generating workflow template: " + json.getString("taskId"));
				tasks.add(t);
				tasksIds.add(t.getTaskId());
			}
		}
		catch(Exception e){
			e.printStackTrace();
			throw e;
		}
//		System.out.println("COMPONENTS SIZE IN WORKFLOW CONSTRUCTOR: "+components.size());
	}

	public WorkflowTemplate(String workflowString, RabbitMQManager rabbitMQManager, TaskManager taskManager) throws Exception {
		this(new JSONObject(workflowString), rabbitMQManager, taskManager);
	}

//	public boolean execute(String documentId, boolean priority, RabbitMQManager manager, String outputCallback, String statusCallback) throws Exception {
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
////			System.out.println(wfc.getWorkflowComponentId());
////			System.out.println(wfc.getClass());
////			wfc.executeComponent(documentId, priority);
//			String partialResult = wfc.executeComponent(documentId, priority, manager,outputCallback,statusCallback);
//			if(partialResult==null){
//				return false;
//			}
//		}
//		return true;
//	}

	public JSONObject getJSONRepresentation() throws Exception{
		JSONObject json = new JSONObject();
		json.put("workflowDefinitionId", workflowTemplateId);
		json.put("name", name);
		json.put("description", workflowTemplateDescription);
		JSONArray array = new JSONArray();
		for (String string: componentsDefinitions) {
			array.put(new JSONObject(string));
		}
		json.put("components", array);
		json.put("creationTime", creationTime);
		return json;
	}

	public String getWorkflowId() {
		return workflowTemplateId;
	}

	public void setWorkflowId(String workflowId) {
		this.workflowTemplateId = workflowId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getWorkflowDescription() {
		return workflowTemplateDescription;
	}

	public void setWorkflowDescription(String workflowDescription) {
		this.workflowTemplateDescription = workflowDescription;
	}

	public Date getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

	public List<String> getComponentsDefinitions() {
		return componentsDefinitions;
	}

	public void setComponentsDefinitions(List<String> componentsDefinitions) {
		this.componentsDefinitions = componentsDefinitions;
	}

	public void setComponents(List<WorkflowComponent> components) {
		this.components = components;
	}

	public List<WorkflowComponent> getComponents() {
		return components;
	}

	
	public String getWorkflowTemplateId() {
		return workflowTemplateId;
	}

	public void setWorkflowTemplateId(String workflowTemplateId) {
		this.workflowTemplateId = workflowTemplateId;
	}

	public String getWorkflowTemplateDescription() {
		return workflowTemplateDescription;
	}

	public void setWorkflowTemplateDescription(String workflowTemplateDescription) {
		this.workflowTemplateDescription = workflowTemplateDescription;
	}

	public List<Task> getTasks() {
		if(tasks==null ||tasks.isEmpty()) {
			System.out.println("TASK ARE NULL WHEN CALLING GETTASKS()");
		}
		return tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}

	public void reestablishComponents(RabbitMQManager rabbitMQManager, TaskManager taskManager) {
		try {
//			components = new LinkedList<WorkflowComponent>();
////			System.out.println("Workflow reestablishment: "+workflowId);
//			for (String wcd : componentsDefinitions) {
//				
//				WorkflowComponent wc = WorkflowComponent.defineComponent(new JSONObject(wcd),rabbitMQManager, this.workflowTemplateId);
//				components.add(wc);
//			}
			for (String s : tasksIds) {
				Task t = taskManager.findOneByTaskId(s);
				if(t==null) {
					throw new Exception("The Task ["+s+"] used in template ["+workflowTemplateId+"] is NOT available.");
				}
//				System.out.println("TASK ID generating workflow template: " + json.getString("taskId"));
				tasks.add(t);
			}
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println("----------------------------");
			System.out.println("ERROR in reestablishing componentss");
			System.out.println("----------------------------");
		}
	}

	public List<String> getTasksIds() {
		return tasksIds;
	}

	public void setTasksIds(List<String> tasksIds) {
		this.tasksIds = tasksIds;
	}

}
