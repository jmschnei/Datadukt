package de.dfki.cwm.persistence.tasks;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.json.JSONObject;

import de.dfki.cwm.communication.rabbitmq.RabbitMQManager;

/**
 * @author Julian Moreno Schneider jumo04@dfki.de
 *
 */
@Entity
public class Task {

//	String id;
	
	@Id
   	String taskId;
	
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "workflow_template_id", nullable = false)
//	WorkflowTemplate workflowTemplate; 
//	
	String taskName;

	@Column(columnDefinition="LONGVARCHAR")
	String taskDescription;

	Date creationTime;

	// TODO Include the Controller Description??
	
	public Task() {
	}

	public Task(String taskId, String taskName, String taskDescription, Date creationTime) {
		super();
		this.taskId = taskId;
		this.taskName = taskName;
		this.taskDescription = taskDescription;
		this.creationTime = creationTime;
	}

	public Task(JSONObject taskDescription, RabbitMQManager rabbitMQManager) throws Exception {
		super();
		this.taskDescription = taskDescription.toString();
		try{
			this.taskName = taskDescription.getString("taskName");
			this.taskId = taskDescription.getString("taskId");
		}
		catch(Exception e){
			e.printStackTrace();
			throw e;
		}
	}

	public Task(String workflowString, RabbitMQManager rabbitMQManager) throws Exception {
		this(new JSONObject(workflowString),rabbitMQManager);
	}

	public boolean execute(String documentId, boolean priority, RabbitMQManager manager) throws Exception {
		
		// TODO
		
		return true;
	}

	public JSONObject getJSONRepresentation() throws Exception{
		JSONObject json = new JSONObject();
		json.put("taskId", taskId);
		json.put("taskName", taskName);
		json.put("taskDescription", taskDescription);
		json.put("creationTime", creationTime);
		return json;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getTaskDescription() {
		return taskDescription;
	}

	public void setTaskDescription(String taskDescription) {
		this.taskDescription = taskDescription;
	}

	public Date getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}
	
}
