package de.dfki.cwm.engine;

import java.io.File;
import java.io.FileReader;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.cxf.helpers.IOUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import de.dfki.cwm.communication.rabbitmq.RabbitMQManager;
import de.dfki.cwm.controllers.Controller;
import de.dfki.cwm.controllers.ControllersManager;
import de.dfki.cwm.data.documents.WMDocument;
import de.dfki.cwm.exceptions.WorkflowException;
import de.dfki.cwm.persistence.DataManager;
import de.dfki.cwm.persistence.IdentifierGenerator;
import de.dfki.cwm.persistence.WorkflowRepository;
import de.dfki.cwm.persistence.tasks.Task;
import de.dfki.cwm.persistence.tasks.TaskManager;
import de.dfki.cwm.persistence.workflowexecutions.WorkflowExecution;
import de.dfki.cwm.persistence.workflowexecutions.WorkflowExecutionManager;
import de.dfki.cwm.persistence.workflowinstances.WorkflowInstance;
import de.dfki.cwm.persistence.workflowinstances.WorkflowInstanceManager;
import de.dfki.cwm.persistence.workflowtemplates.WorkflowTemplate;
import de.dfki.cwm.persistence.workflowtemplates.WorkflowTemplateManager;

/**
 * @author Julian Moreno Schneider julian.moreno_schneider@dfki.de
 * @project CurationWorkflowManager
 * @date 17.04.2020
 * @date_modified 
 * @company DFKI
 * @description Class defining the engine of the CWM managing everything. 
 *
 */
@Component
public class CWMEngine {

	Logger logger = Logger.getLogger(CWMEngine.class);

	String finishedStatus = "FINISHED";

	@Value( "${workflowmanager.tasks_path}" )
	String tasksPath = "tasks";
	@Value( "${workflowmanager.templates_path}" )
	String workflowTemplatesPath = "templates";
	
	@Autowired
	DataManager dataManager;

	@Autowired
	WorkflowTemplateManager workflowTemplateManager;

	@Autowired
	TaskManager taskManager;

	@Autowired
	WorkflowRepository workflowRepository;

	@Autowired
	RabbitMQManager rabbitMQManager;
	
	@Autowired
	ControllersManager controllersManager;
	
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    WorkflowExecutionManager workflowExecutionManager;
    
    @Autowired
    WorkflowInstanceManager workflowInstanceManager;
    
    boolean started = false;
    
	public CWMEngine() throws Exception {
	}

	/************************************************
	 * Methods for the initialization of the CWM
	 ************************************************/

	@PostConstruct
	public void initializeService() throws Exception{		
		initializeTasks();
		initializeWorkflowTemplates();
		startWorkflowManager();
	}
	
	public void initializeTasks() {
		try {
			logger.info("Initializing tasks...");
			ClassPathResource folder = new ClassPathResource(tasksPath);
			File [] files = folder.getFile().listFiles();
			for (File file : files) {
				if(!file.getName().startsWith(".")) {
					logger.info("Initializing Task "+file.getName());
					String content = IOUtils.toString(new FileReader(file));
					Task t = new Task(content, rabbitMQManager);
					Task t2 = taskManager.findOneByTaskId(t.getTaskId());
					if(t2!=null) {
						logger.info("Task ["+t.getTaskId()+"] is already included in TaskRepository.");
					}
					else {
						logger.info("Task ["+t.getTaskId()+"] included.");
						taskManager.save(t);
					}
				}
			}
			logger.info("... initializing tasks DONE");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void initializeWorkflowTemplates() {
		try {
			logger.info("Initializing workflowTemplates...");
			ClassPathResource folder = new ClassPathResource(workflowTemplatesPath);
			File [] files = folder.getFile().listFiles();
			for (File file : files) {
				if(!file.getName().startsWith(".")) {
					logger.info("Initializing "+file.getName());
					String content = IOUtils.toString(new FileReader(file));
					WorkflowTemplate wt = new WorkflowTemplate(content, rabbitMQManager, taskManager);
					WorkflowTemplate wt2 = workflowTemplateManager.findOneByWorkflowTemplateId(wt.getWorkflowId());
					if(wt2!=null) {
						logger.info("WorkflowTemplate ["+wt.getWorkflowId()+"] already included in WorkflowTemplateRepository.");
					}
					else {
						logger.info("WorkflowTemplate ["+wt.getWorkflowId()+"] included.");
						workflowTemplateManager.save(wt);
					}
				}
			}
			logger.info("... initializing workflowTemplates DONE");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void startWorkflowManager() throws Exception {
		logger.info("Starting DEFAULT RabbitMQManager ...");
		controllersManager.initializeServices();
		rabbitMQManager.startManager();
		rabbitMQManager.initializeControllers(controllersManager.getControllers());
//		rabbitMQManager.initializeWorkflows(workflowRepository.findAll());
		controllersManager.startRunningServices();
		started=true;
		logger.info(" ... Starting DEFAULT RabbitMQManager DONE");
	}

	public void startWorkflowManager(Properties properties, boolean restart) throws Exception {
		logger.info("Starting PROPERTIES RabbitMQManager ...");
		if(started) {
			if(restart) {
				logger.info("Restarting ...");
				stopWorkflowManager(properties);
			}
			else{
				throw new Exception ("The service is already started and RESTART is stablished to 'false'.");
			}
		}
		controllersManager.initializeServices();
		rabbitMQManager.startManager(properties);
		rabbitMQManager.initializeControllers(controllersManager.getControllers());
//		rabbitMQManager.initializeWorkflows(workflowRepository.findAll());
		controllersManager.startRunningServices();
		started=true;
		logger.info(" ... Starting PROPERTIES RabbitMQManager DONE");
	}

	public void stopWorkflowManager(Properties properties) throws Exception {
		if(!started) {
			throw new Exception ("The service is not STARTED. It can not be stopped.");
		}
		controllersManager.stopRunningServices();
		rabbitMQManager.stopWorkflows(workflowRepository.findAll());
		rabbitMQManager.stopControllers(controllersManager.getControllers());
		rabbitMQManager.stopManager();
		controllersManager.stopServices();
		started=false;
	}

	/************************************************
	 * Methods related to the Execution of Workflows.
	 ************************************************/
	
	public Object executeWorkflow(String workflowExecutionId, boolean priority) throws Exception {
		WorkflowExecution we = workflowExecutionManager.findOneByWorkflowExecutionId(workflowExecutionId);
		if(we==null){
			String msg = String.format("The workflow \"%s\" does not exist.",workflowExecutionId);
        	throw new Exception(msg);
		}
		Object obj = we.execute(priority, dataManager);
		return obj;
	}

	public Object executeWorkflow(String content, String workflowExecutionId, boolean priority) throws Exception {
		WorkflowExecution we = workflowExecutionManager.findOneByWorkflowExecutionId(workflowExecutionId);
		if(we==null){
			String msg = String.format("The workflow \"%s\" does not exist.",workflowExecutionId);
        	throw new Exception(msg);
		}
		Object obj = we.execute(content, priority, dataManager);
		return obj;
	}

	public Object executeWorkflow(WMDocument qd, String workflowExecutionId, boolean priority) throws Exception {
		WorkflowExecution we = workflowExecutionManager.findOneByWorkflowExecutionId(workflowExecutionId);
		if(we==null){
			String msg = String.format("The workflow \"%s\" does not exist.",workflowExecutionId);
        	throw new Exception(msg);
		}
		/**
		 * TODO Generate a new WorkflowExecution, so that queues does not fail.
		 */
		System.out.println(we.getJSONRepresentation());
		String weId_new = we.getWorkflowExecutionId();
		weId_new = weId_new + "_" + (new Date()).getTime();
		createWorkflowExecution(we.getWorkflowExecutionDescription(), weId_new);
		WorkflowExecution we_new = workflowExecutionManager.findOneByWorkflowExecutionId(weId_new);
		if(we_new==null){
			String msg = String.format("The workflow \"%s\" does not exist.",weId_new);
        	throw new Exception(msg);
		}
		Object obj = we_new.execute(qd, priority, dataManager);
		return obj;
	}

	public String getWorkflowExecutionOutput(String workflowExecutionId, String contentTypeHeader, boolean keepWaiting) throws Exception{
		WorkflowExecution wfe = workflowExecutionManager.findOneByWorkflowExecutionId(workflowExecutionId);
		if(wfe==null) {
			throw new Exception("WorkflowExecution ID does not exist.");
		}
		if(keepWaiting) {
			boolean done=false;
			while(!done) {
//				System.out.println(wfe.getStatus());
				if(wfe.getStatus().equalsIgnoreCase(finishedStatus)) {
					return wfe.getOutput();
				}
				else {
					System.out.println("Workflowexecution ["+workflowExecutionId+"] is still not finished. Status is: "+wfe.getStatus());
					Thread.sleep(5000);

					//TODO It could be interesting to include a Future where the result is read.
					
				}
			}
		}
		else {
			if(wfe.getStatus().equalsIgnoreCase("FINISHED")) {
				return wfe.getOutput();
			}
			else {
				return "The workflow execution ["+workflowExecutionId+"] is still RUNNING: "+wfe.getStatus();
			}
		}
		System.out.println("ERROR IN CODE: THIS METHOD SHOULD NEVER COME UNTIL HERE.");
		return null;
	}

	/************************************************
	 * Methods related to the management of Workflow Instances.
	 ************************************************/

	public JSONArray listWorkflowInstances(String workflowInstanceId) throws Exception {
		try {
			JSONArray workflowInstances = new JSONArray();
			List<WorkflowInstance> workflowsList = listWorkflowInstancesObject(workflowInstanceId);
			for (WorkflowInstance wf : workflowsList) {
				workflowInstances.put(wf.getJSONRepresentation());
			}
			return workflowInstances;
		}
		catch(Exception e){
			logger.error(e.getMessage());
			throw e;
		}
	}
	
	public List<WorkflowInstance> listWorkflowInstancesObject(String WorkflowInstanceId) throws Exception {
		try {
			List<WorkflowInstance> workflows = new LinkedList<WorkflowInstance>();
			if(WorkflowInstanceId==null || WorkflowInstanceId.equalsIgnoreCase("")) {
				workflows = workflowInstanceManager.findAll();
			}
			else {
				WorkflowInstance w = workflowInstanceManager.findOneByWorkflowInstanceId(WorkflowInstanceId);
				if(w!=null) {
					workflows.add(w);
				}
				else {
					System.out.println("Adding a null");
				}
			}
			return workflows;
		}
		catch(Exception e){
			logger.error(e.getMessage());
			throw e;
		}
	}
	
	public String createWorkflowInstance(String postBody, String workflowInstanceId) throws JSONException,WorkflowException,Exception {
		JSONObject json = new JSONObject(postBody);
		return createWorkflowInstance(json, workflowInstanceId);
	}

	public String createWorkflowInstance(JSONObject json, String workflowInstanceId) throws JSONException,WorkflowException,Exception {
		if(workflowInstanceId==null || workflowInstanceId.equalsIgnoreCase("")) {
			workflowInstanceId = (json.has("workflowInstanceId"))?json.getString("workflowInstanceId"):IdentifierGenerator.createWorkflowInstanceId();
		}
		else {
			WorkflowInstance wfe = workflowInstanceManager.findOneByWorkflowInstanceId(workflowInstanceId);
			if(wfe!=null) {
				workflowInstanceId += "_"+(new Date()).getTime();
			}
		}
		json.put("workflowInstanceId", workflowInstanceId);
		
		String workflowTemplateId = json.getString("workflowTemplateId");
		WorkflowTemplate wt = workflowTemplateManager.findOneByWorkflowTemplateId(workflowTemplateId);
		if(wt==null) {
			throw new Exception("The specified workflow Template does not exist. Request [GET] /templates to get a complete list of available workflow templates.");
		}
//		System.out.println(wt.getWorkflowDescription());
		WorkflowInstance new_wf = new WorkflowInstance(json, dataManager, wt);
//		System.out.println("WorkflowInstanceId_3: "+new_wf.WorkflowInstanceId);
//		System.out.println(new_wf.getJSONRepresentation().toString(1));
		new_wf = workflowInstanceManager.save(new_wf);
//		System.out.println("WorkflowInstanceId_4: "+new_wf.WorkflowInstanceId);
//		System.out.println(new_wf.getJSONRepresentation().toString(1));
//		rabbitMQManager.defineWorkflow(new_wf);
		return workflowInstanceId;
	}

	public boolean deleteWorkflowInstance(String workflowInstanceId) throws Exception{
		try{
			WorkflowInstance wt = workflowInstanceManager.findOneByWorkflowInstanceId(workflowInstanceId);
			if(wt==null) {
				return false;
			}
			else {
				workflowInstanceManager.deleteByWorkflowInstanceId(workflowInstanceId);
				//workflowDAO.deleteByWorkflowId(workflowTemplateId);
				return true;
			}
		}
		catch(Exception e){
//			e.printStackTrace();
			throw e;
		}
	}


	/************************************************
	 * Methods related to the management of Workflow Executions.
	 ************************************************/

	public JSONArray listWorkflowExecutions(String workflowExecutionId) throws Exception {
		try {
			JSONArray workflowExecutions = new JSONArray();
			List<WorkflowExecution> workflowsList = listWorkflowExecutionsObject(workflowExecutionId);
			for (WorkflowExecution wf : workflowsList) {
				workflowExecutions.put(wf.getJSONRepresentation());
			}
			return workflowExecutions;
		}
		catch(Exception e){
			logger.error(e.getMessage());
			throw e;
		}
	}
	
	public List<WorkflowExecution> listWorkflowExecutionsObject(String workflowExecutionId) throws Exception {
		try {
			List<WorkflowExecution> workflows = new LinkedList<WorkflowExecution>();
			if(workflowExecutionId==null || workflowExecutionId.equalsIgnoreCase("")) {
				workflows = workflowExecutionManager.findAll();
			}
			else {
				WorkflowExecution w = workflowExecutionManager.findOneByWorkflowExecutionId(workflowExecutionId);
				if(w!=null) {
					workflows.add(w);
				}
				else {
					System.out.println("Adding a null");
				}
			}
			return workflows;
		}
		catch(Exception e){
			logger.error(e.getMessage());
			throw e;
		}
	}
	
	public String createWorkflowExecution(String postBody, String workflowExecutionId) throws JSONException,WorkflowException,Exception {
		JSONObject json = new JSONObject(postBody);
		return createWorkflowExecution(json, workflowExecutionId);
	}

	public String createWorkflowExecution(JSONObject json, String workflowExecutionId) throws JSONException,WorkflowException,Exception {
		if(workflowExecutionId==null || workflowExecutionId.equalsIgnoreCase("")) {
			workflowExecutionId = (json.has("workflowExecutionId"))?json.getString("workflowExecutionId"):IdentifierGenerator.createWorkflowExecutionId();
		}
		else {
			WorkflowExecution wfe = workflowExecutionManager.findOneByWorkflowExecutionId(workflowExecutionId);
			if(wfe!=null) {
				workflowExecutionId += "_"+(new Date()).getTime();
			}
		}
		json.put("workflowExecutionId", workflowExecutionId);
		
		String workflowTemplateId = json.getString("workflowTemplateId");
		WorkflowTemplate wt = workflowTemplateManager.findOneByWorkflowTemplateId(workflowTemplateId);
		if(wt==null) {
			throw new Exception("The specified workflow Template does not exist. Request [GET] /templates to get a complete list of available workflow templates.");
		}
//		System.out.println(wt.getWorkflowDescription());
		WorkflowExecution new_wf = new WorkflowExecution(json, dataManager, wt);
//		System.out.println("WorkflowExecutionId_3: "+new_wf.workflowExecutionId);
//		System.out.println(new_wf.getJSONRepresentation().toString(1));
		new_wf = workflowExecutionManager.save(new_wf);
//		System.out.println("WorkflowExecutionId_4: "+new_wf.workflowExecutionId);
//		System.out.println(new_wf.getJSONRepresentation().toString(1));
//		rabbitMQManager.defineWorkflow(new_wf);
		return workflowExecutionId;
	}

	public boolean deleteWorkflowExecution(String workflowExecutionId) throws Exception{
		try{
			WorkflowExecution wt = workflowExecutionManager.findOneByWorkflowExecutionId(workflowExecutionId);
			if(wt==null) {
				return false;
			}
			else {
				workflowExecutionManager.deleteByWorkflowExecutionId(workflowExecutionId);
				//workflowDAO.deleteByWorkflowId(workflowTemplateId);
				return true;
			}
		}
		catch(Exception e){
//			e.printStackTrace();
			throw e;
		}
	}

	/************************************************
	 * Methods related to the management of Templates.
	 ************************************************/
	
	public JSONArray listWorkflowTemplates(String workflowTemplateId) throws Exception {
		try {
			JSONArray workflowTemplates = new JSONArray();
			List<WorkflowTemplate> workflowsList = listWorkflowTemplatesObject(workflowTemplateId);
			for (WorkflowTemplate wf : workflowsList) {
				workflowTemplates.put(wf.getJSONRepresentation());
			}
			return workflowTemplates;
		}
		catch(Exception e){
			logger.error(e.getMessage());
			throw e;
		}
	}
	
	public List<WorkflowTemplate> listWorkflowTemplatesObject(String workflowTemplateId) throws Exception {
		try {
			List<WorkflowTemplate> workflows = new LinkedList<WorkflowTemplate>();
			if(workflowTemplateId==null || workflowTemplateId.equalsIgnoreCase("")) {
				workflows = workflowTemplateManager.findAll();
			}
			else {
				WorkflowTemplate w = workflowTemplateManager.findOneByWorkflowTemplateId(workflowTemplateId);
				workflows.add(w);
			}
			return workflows;
		}
		catch(Exception e){
			logger.error(e.getMessage());
			throw e;
		}
	}
	
	public String createWorkflowTemplate(String postBody, String workflowTemplateId) throws JSONException,WorkflowException,Exception {
		JSONObject json = new JSONObject(postBody);
		if(workflowTemplateId==null || workflowTemplateId.equalsIgnoreCase("")) {
			workflowTemplateId = IdentifierGenerator.createWorkflowTemplateId();
		}
		else {
		}
		json.put("workflowTemplateId", workflowTemplateId);
//		WorkflowTemplate new_wf = new WorkflowTemplate(json, rabbitMQManager);
		WorkflowTemplate new_wf = new WorkflowTemplate(json, rabbitMQManager, taskManager);
		System.out.println(new_wf.getTasksIds().size());
		new_wf = workflowTemplateManager.save(new_wf);
//		rabbitMQManager.defineWorkflow(new_wf);
		return workflowTemplateId;
	}

	public boolean deleteWorkflowTemplate(String workflowTemplateId) throws Exception {
		try{
			WorkflowTemplate wt = workflowTemplateManager.findOneByWorkflowTemplateId(workflowTemplateId);
			if(wt==null) {
				return false;
			}
			else {
				workflowTemplateManager.deleteByWorkflowTemplateId(workflowTemplateId);
				//workflowDAO.deleteByWorkflowId(workflowTemplateId);
				return true;
			}
		}
		catch(Exception e){
//			e.printStackTrace();
			throw e;
		}
	}

	/************************************************
	 * Methods related to the management of Tasks.
	 ************************************************/

	public JSONArray listTasks(String taskId) throws Exception {
		try {
			JSONArray tasks = new JSONArray();
			List<Task> tasksList = new LinkedList<Task>();
			if(taskId==null || taskId.equalsIgnoreCase("")) {
				tasksList = taskManager.findAll();
			}
			else {
				Task t= taskManager.findOneByTaskId(taskId);
				if(t!=null) {
					tasksList.add(t);
				}
				else {
					System.out.println("Adding a null in tasks list.");
				}
			}
			for (Task t : tasksList) {
				tasks.put(t.getJSONRepresentation());
			}
			return tasks;
		}
		catch(Exception e){
			logger.error(e.getMessage());
			throw e;
		}
	}
	
	public String createTask(String postBody, String taskId) throws JSONException,WorkflowException,Exception {
		JSONObject json = new JSONObject(postBody);
		if(taskId ==null || taskId.equalsIgnoreCase("")) {
			if(json.has("taskId")) {
				taskId = json.getString("taskId");
			}
			else {
				taskId = IdentifierGenerator.createTaskId();
			}
		}
		else {
		}
		json.put("taskId", taskId);
		Task new_wf = new Task(json, rabbitMQManager);
		new_wf = taskManager.save(new_wf);
//		rabbitMQManager.defineWorkflow(new_wf);
		return taskId;
	}

	public boolean deleteTask(String taskId) throws Exception {
		try{
			System.out.println(taskId);
			Task t = taskManager.findOneByTaskId(taskId);
			if(t==null) {
				throw new Exception("The task with taskId="+taskId+" cannot be found. It has not been deleted.");
			}
			else {
				taskManager.deleteByTaskId(taskId);
				//workflowDAO.deleteByWorkflowId(workflowTemplateId);
				return true;
			}
		}
		catch(Exception e){
//			e.printStackTrace();
			throw e;
		}
	}

	/************************************************
	 * Methods related to the management of Controllers.
	 ************************************************/

	public JSONArray listControllers(String controllerId) throws Exception {
		try {
			JSONArray controllers = new JSONArray();
			List<Controller> controllersList = new LinkedList<Controller>();
			if(controllerId==null || controllerId.equalsIgnoreCase("")) {
				controllersList = controllersManager.findAll();
			}
			else {
				Controller c = controllersManager.findOneByControllerId(controllerId);
				if(c!=null) {
					controllersList.add(c);
				}
				else {
					System.out.println("Adding a null in controllers list.");
				}
			}
			for (Controller t : controllersList) {
//				System.out.println("Adding controller to list.");
				controllers.put(t.getJSONRepresentation());
			}
			return controllers;
		}
		catch(Exception e){
			logger.error(e.getMessage());
			throw e;
		}
	}
	
	public List<Controller> listControllersObject(String controllerId) throws Exception {
		try {
			List<Controller> controllers = new LinkedList<Controller>();
			if(controllerId==null || controllerId.equalsIgnoreCase("")) {
				controllers = controllersManager.findAll();
			}
			else {
				Controller c = controllersManager.findOneByControllerId(controllerId);
				if(c!=null) {
					controllers.add(c);
				}
				else {
					System.out.println("Adding a null in controllers list.");
				}
			}
			return controllers;
		}
		catch(Exception e){
			logger.error(e.getMessage());
			throw e;
		}
	}
	
	public String createController(String postBody, String controllerId) throws JSONException,WorkflowException,Exception {
		JSONObject json = new JSONObject(postBody);
		if(controllerId==null || controllerId.equalsIgnoreCase("")) {
			if(json.has("taskId")) {
				controllerId = json.getString("controllerId");
			}
			else {
				controllerId = IdentifierGenerator.createControllerId();
			}
		}
		else {
		}
		json.put("controllerId", controllerId);
		Controller new_wf = Controller.constructController(json, dataManager);
		new_wf = controllersManager.save(new_wf);
//		TODO
//		rabbitMQManager.defineWorkflow(new_wf);
		return controllerId;
	}

	public boolean deleteController(String controllerId) throws Exception{
		try{
			Controller c = controllersManager.findOneByControllerId(controllerId);
			if(c==null) {
				return false;
			}
			else {
				controllersManager.deleteByControllerId(controllerId);
				//workflowDAO.deleteByWorkflowId(workflowTemplateId);
				return true;
			}
		}
		catch(Exception e){
//			e.printStackTrace();
			throw e;
		}
	}

}
