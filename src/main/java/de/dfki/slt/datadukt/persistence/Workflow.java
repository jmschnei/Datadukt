package de.dfki.slt.datadukt.persistence;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonInclude;

import de.dfki.slt.datadukt.communication.rabbitmq.RabbitMQManager;
import de.dfki.slt.datadukt.components.WorkflowComponent;
import de.dfki.slt.datadukt.exceptions.WorkflowException;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Julian Moreno Schneider jumo04@dfki.de
 *
 */
@Entity
public class Workflow {

//	public enum Status {
//		CREATED
//	}
//
//	//	@GeneratedValue(strategy = GenerationType.AUTO)
//	//	Integer id;
//
//	//	@JoinColumn(name = "indexId")
	@Id
	String workflowId;
	String name;
//
//	@Column(columnDefinition="LONGVARCHAR")
//	String workflowDescription;
//
////	@OneToMany(cascade=CascadeType.PERSIST)
////	@ElementCollection
////	List<WorkflowComponent> components;
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
//	public Workflow() {
//	}
//
//	public Workflow(String workflowId, String name, String workflowDescription,
//			List<String> componentsDefinitions, Date creationTime) {
//		super();
//		this.workflowId = workflowId;
//		this.name = name;
//		this.workflowDescription = workflowDescription;
//		this.componentsDefinitions = componentsDefinitions;
//		this.creationTime = creationTime;
//	}
//
//	public Workflow(JSONObject workflowDescription, RabbitMQManager rabbitMQManager) throws Exception {
//		super();
//		this.workflowDescription = workflowDescription.toString();
//		componentsDefinitions = new LinkedList<String>();
//		components = new LinkedList<WorkflowComponent>();
//		try{
//			this.name = workflowDescription.getString("name");
//			this.workflowId = workflowDescription.getString("workflowId");
//
////			System.out.println("WorkflowId (constructor with json): "+this.workflowId);
//			
//			JSONArray arrayComponents = workflowDescription.getJSONArray("components");
//			for (int i = 0; i < arrayComponents.length(); i++) {
//				JSONObject json = arrayComponents.getJSONObject(i);
//				componentsDefinitions.add(json.toString());
//				WorkflowComponent wc = WorkflowComponent.defineComponent(json, rabbitMQManager, this.workflowId);
//				components.add(wc);
//			}
//		}
//		catch(Exception e){
//			e.printStackTrace();
//			throw e;
//		}
////		System.out.println("COMPONENTS SIZE IN WORKFLOW CONSTRUCTOR: "+components.size());
//	}
//
//	public Workflow(String workflowString, RabbitMQManager rabbitMQManager) throws Exception {
//		super();
//		JSONObject workflowDescription = new JSONObject(workflowString);
//		this.workflowDescription = workflowString;
//		componentsDefinitions = new LinkedList<String>();
//		components = new LinkedList<WorkflowComponent>();
//		try{
//			this.name = workflowDescription.getString("name");
//			this.workflowId = workflowDescription.getString("workflowId");
//
////			System.out.println("WorkflowId (constructor with string): "+this.workflowId);
//			
//			JSONArray arrayComponents = workflowDescription.getJSONArray("components");
//			for (int i = 0; i < arrayComponents.length(); i++) {
//				JSONObject json = arrayComponents.getJSONObject(i);
//				componentsDefinitions.add(json.toString());
//				WorkflowComponent wc = WorkflowComponent.defineComponent(json, rabbitMQManager, this.workflowId);
//				components.add(wc);
//			}
//		}
//		catch(Exception e){
//			e.printStackTrace();
//			throw e;
//		}
//	}
//
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
//			String partialResult = wfc.executeComponent(documentId, priority, manager,outputCallback,statusCallback, true, true);
//			if(partialResult==null){
//				return false;
//			}
//		}
//		return true;
//	}
//
//	public JSONObject getJSONRepresentation() throws Exception{
//		JSONObject json = new JSONObject();
//		json.put("workflowDefinitionId", workflowId);
//		json.put("name", name);
//		json.put("description", workflowDescription);
//		JSONArray array = new JSONArray();
//		for (String string: componentsDefinitions) {
//			array.put(new JSONObject(string));
//		}
//		json.put("components", array);
//		json.put("creationTime", creationTime);
//		return json;
//	}
//
//	public String getWorkflowId() {
//		return workflowId;
//	}
//
//	public void setWorkflowId(String workflowId) {
//		this.workflowId = workflowId;
//	}
//
//	public String getName() {
//		return name;
//	}
//
//	public void setName(String name) {
//		this.name = name;
//	}
//
//	public String getWorkflowDescription() {
//		return workflowDescription;
//	}
//
//	public void setWorkflowDescription(String workflowDescription) {
//		this.workflowDescription = workflowDescription;
//	}
//
//	public Date getCreationTime() {
//		return creationTime;
//	}
//
//	public void setCreationTime(Date creationTime) {
//		this.creationTime = creationTime;
//	}
//
//	public List<String> getComponentsDefinitions() {
//		return componentsDefinitions;
//	}
//
//	public void setComponentsDefinitions(List<String> componentsDefinitions) {
//		this.componentsDefinitions = componentsDefinitions;
//	}
//
//	public void setComponents(List<WorkflowComponent> components) {
//		this.components = components;
//	}
//
//	public List<WorkflowComponent> getComponents() {
//		return components;
//	}
//
//	public void reestablishComponents(RabbitMQManager rabbitMQManager) {
//		try {
//			components = new LinkedList<WorkflowComponent>();
////			System.out.println("Workflow reestablishment: "+workflowId);
//			for (String wcd : componentsDefinitions) {
//				
//				WorkflowComponent wc = WorkflowComponent.defineComponent(new JSONObject(wcd),rabbitMQManager, this.workflowId);
//				components.add(wc);
//			}
//		}catch(Exception e) {
//			e.printStackTrace();
//			System.out.println("----------------------------");
//			System.out.println("ERROR in reestablishing componentss");
//			System.out.println("----------------------------");
//		}
//	}

}
