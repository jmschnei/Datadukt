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
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import de.dfki.cwm.communication.rabbitmq.RabbitMQManager;
import de.dfki.cwm.controllers.Controller;
import de.dfki.cwm.controllers.ControllerRepository;
import de.dfki.cwm.controllers.ControllersManager;
import de.dfki.cwm.exceptions.WorkflowException;
import de.dfki.cwm.persistence.DataManager;
import de.dfki.cwm.persistence.IdentifierGenerator;
import de.dfki.cwm.persistence.WorkflowRepository;
import de.dfki.cwm.persistence.tasks.Task;
import de.dfki.cwm.persistence.tasks.TaskManager;
import de.dfki.cwm.persistence.workflowexecutions.WorkflowExecution;
import de.dfki.cwm.persistence.workflowexecutions.WorkflowExecutionManager;
import de.dfki.cwm.persistence.workflowtemplates.WorkflowTemplate;
import de.dfki.cwm.persistence.workflowtemplates.WorkflowTemplateManager;

/**
 * @author Julian Moreno Schneider julian.moreno_schneider@dfki.de
 *
 */

@Component
public class CWMEngine {

	Logger logger = Logger.getLogger(CWMEngine.class);

//	String finishedStatus = "{\"workflowexecution\":{\"status\":\"FINISHED\"}}";
	String finishedStatus = "FINISHED";
	
	@Autowired
	DataManager dataManager;

	@Autowired
	WorkflowTemplateManager workflowTemplateManager;

//	@Autowired
//	WorkflowExecutionRepository workflowExecutionRepository;

//	@Autowired
//	TaskRepository taskRepository;

	@Autowired
	TaskManager taskManager;

	@Autowired
	ControllerRepository controllerRepository;

	@Autowired
	WorkflowRepository workflowRepository;
	
//	HashMap<String, WorkflowContainer> workflows = new HashMap<String, WorkflowContainer>();
		
	@Autowired
	RabbitMQManager rabbitMQManager;
	
	@Autowired
	ControllersManager controllersManager;
	
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    WorkflowExecutionManager workflowExecutionManager;
    
    boolean started = false;
    
	public CWMEngine() throws Exception {
	}

	@PostConstruct
	public void initializeService() throws Exception{		
		initializeTasks();
		initializeWorkflowTemplates();
		startWorkflowManager();
	}
	
	public void startWorkflowManager() throws Exception {
		logger.info("Starting DEFAULT RabbitMQManager ...");
		controllersManager.initializeServices();
		rabbitMQManager.startRabbitMQManager();
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
		rabbitMQManager.startRabbitMQManager(properties);
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
		rabbitMQManager.stopRabbitMQManager();
		controllersManager.stopServices();
		started=false;
	}

	public void initializeWorkflowTemplates() {
		try {
			logger.info("Initializing workflowTemplates...");
			String workflowTemplatesPath = "templates";
			ClassPathResource folder = new ClassPathResource(workflowTemplatesPath);
//			System.out.println("PATH: "+folder.getFile().getAbsolutePath());
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
	
	public void initializeTasks() {
		try {
			logger.info("Initializing tasks...");
			String workflowTemplatesPath = "tasks";
			ClassPathResource folder = new ClassPathResource(workflowTemplatesPath);
			File [] files = folder.getFile().listFiles();
			for (File file : files) {
				if(!file.getName().startsWith(".")) {
					logger.info("Initializing "+file.getName());
					
					String content = IOUtils.toString(new FileReader(file));
					Task t = new Task(content, rabbitMQManager);
					
					Task t2 = taskManager.findOneByTaskId(t.getTaskId());
					if(t2!=null) {
						logger.info("Task ["+t.getTaskId()+"] already included in TaskRepository.");
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
	
	/**
	 * 
	 * Methods related to the Execution of Workflows.
	 * 
	 */
	
	public Object executeWorkflow(String workflowExecutionId, boolean priority) throws Exception {
//		Workflow wf = workflowRepository.findOneByWorkflowId(workflowId);
		//WorkflowExecution we = workflowExecutionRepository.findOneByWorkflowExecutionId(workflowExecutionId);
		WorkflowExecution we = workflowExecutionManager.findOneByWorkflowExecutionId(workflowExecutionId);
		
		if(we==null){
			String msg = String.format("The workflow \"%s\" does not exist.",workflowExecutionId);
        	throw new Exception(msg);
		}
		Object obj = we.execute(priority, dataManager);
		
		return obj;
	}

	public Object executeWorkflow(String content, String workflowExecutionId, boolean priority) throws Exception {
//		Workflow wf = workflowRepository.findOneByWorkflowId(workflowId);
		//WorkflowExecution we = workflowExecutionRepository.findOneByWorkflowExecutionId(workflowExecutionId);
		WorkflowExecution we = workflowExecutionManager.findOneByWorkflowExecutionId(workflowExecutionId);
		
		if(we==null){
			String msg = String.format("The workflow \"%s\" does not exist.",workflowExecutionId);
        	throw new Exception(msg);
		}
		Object obj = we.execute(content, priority, dataManager);
		
		return obj;
	}

	public String getWorkflowExecutionOutput(String workflowExecutionId, String contentTypeHeader, boolean keepWaiting) throws Exception{
		
		WorkflowExecution wfe = workflowExecutionManager.findOneByWorkflowExecutionId(workflowExecutionId);
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

	/**
	 * 
	 * Methods related to the management of Templates.
	 * 
	 */
	
	public JSONArray listWorkflowTemplates(String workflowTemplateId) throws Exception {
		try {
			JSONArray workflowTemplates = new JSONArray();
			List<WorkflowTemplate> workflowsList = new LinkedList<WorkflowTemplate>();
			if(workflowTemplateId==null || workflowTemplateId.equalsIgnoreCase("")) {
				workflowsList = workflowTemplateManager.findAll();
			}
			else {
				WorkflowTemplate w = workflowTemplateManager.findOneByWorkflowTemplateId(workflowTemplateId);
				workflowsList.add(w);
			}
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

	/**
	 * 
	 * Methods related to the management of Workflow Executions.
	 * 
	 */
	
	public JSONArray listWorkflowExecutions(String workflowExecutionId) throws Exception {
		try {
			JSONArray workflowExecutions = new JSONArray();
			List<WorkflowExecution> workflowsList = new LinkedList<WorkflowExecution>();
			if(workflowExecutionId==null || workflowExecutionId.equalsIgnoreCase("")) {
				workflowsList = workflowExecutionManager.findAll();
			}
			else {
				WorkflowExecution w = workflowExecutionManager.findOneByWorkflowExecutionId(workflowExecutionId);
				if(w!=null) {
					workflowsList.add(w);
				}
				else {
					System.out.println("Adding a null");
				}
			}
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
	
	public String createWorkflowExecution(String postBody, String workflowExecutionId) throws JSONException,WorkflowException,Exception {
		JSONObject json = new JSONObject(postBody);
//		System.out.println(json.toString(1));
//		System.out.println("WorkflowExecutionId_1: "+workflowExecutionId);
		if(workflowExecutionId==null || workflowExecutionId.equalsIgnoreCase("")) {
			workflowExecutionId = IdentifierGenerator.createWorkflowExecutionId();
		}
		else {
			WorkflowExecution wfe = workflowExecutionManager.findOneByWorkflowExecutionId(workflowExecutionId);
			if(wfe!=null) {
				workflowExecutionId += "_"+(new Date()).getTime();
			}
		}
//		System.out.println("WorkflowExecutionId_2: "+workflowExecutionId);
		json.put("workflowExecutionId", workflowExecutionId);
		
		String workflowTemplateId = json.getString("workflowTemplateId");
		WorkflowTemplate wt = workflowTemplateManager.findOneByWorkflowTemplateId(workflowTemplateId);
		if(wt==null) {
			throw new Exception("The specified workflow Template does not exist. Request [GET] /templates to get a complete list of available workflow templates.");
		}
		
		WorkflowExecution new_wf = new WorkflowExecution(json, dataManager, wt);
//		System.out.println("WorkflowExecutionId_3: "+new_wf.workflowExecutionId);
//		System.out.println(new_wf.getJSONRepresentation().toString(1));
		new_wf = workflowExecutionManager.save(new_wf);
//		System.out.println("WorkflowExecutionId_4: "+new_wf.workflowExecutionId);
//		System.out.println(new_wf.getJSONRepresentation().toString(1));
//		rabbitMQManager.defineWorkflow(new_wf);
		return workflowExecutionId;
	}

	public String createWorkflowExecution(JSONObject json, String workflowExecutionId) throws JSONException,WorkflowException,Exception {
//		System.out.println(json.toString(1));
//		System.out.println("WorkflowExecutionId_1: "+workflowExecutionId);
		if(workflowExecutionId==null || workflowExecutionId.equalsIgnoreCase("")) {
			workflowExecutionId = IdentifierGenerator.createWorkflowExecutionId();
		}
		else {
			WorkflowExecution wfe = workflowExecutionManager.findOneByWorkflowExecutionId(workflowExecutionId);
			if(wfe!=null) {
				workflowExecutionId += "_"+(new Date()).getTime();
			}
		}
//		System.out.println("WorkflowExecutionId_2: "+workflowExecutionId);
		json.put("workflowExecutionId", workflowExecutionId);
		
		String workflowTemplateId = json.getString("workflowTemplateId");
		WorkflowTemplate wt = workflowTemplateManager.findOneByWorkflowTemplateId(workflowTemplateId);
		if(wt==null) {
			throw new Exception("The specified workflow Template does not exist. Request [GET] /templates to get a complete list of available workflow templates.");
		}
		
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

	/**
	 * 
	 * Methods related to the management of Tasks.
	 * 
	 */
	
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

	/**
	 * 
	 * Methods related to the management of Controllers.
	 * 
	 */
	
	public JSONArray listControllers(String controllerId) throws Exception {
		try {
			JSONArray controllers = new JSONArray();
			List<Controller> controllersList = new LinkedList<Controller>();
			if(controllerId==null || controllerId.equalsIgnoreCase("")) {
				controllersList = controllerRepository.findAll();
			}
			else {
				Controller c = controllerRepository.findOneByControllerId(controllerId);
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
		Controller new_wf = new Controller(json, rabbitMQManager);
		new_wf = controllerRepository.save(new_wf);
//		TODO
//		rabbitMQManager.defineWorkflow(new_wf);
		return controllerId;
	}

	public boolean deleteController(String controllerId) {
		try{
			Controller c = controllerRepository.findOneByControllerId(controllerId);
			if(c==null) {
				return false;
			}
			else {
				controllerRepository.deleteByControllerId(controllerId);
				//workflowDAO.deleteByWorkflowId(workflowTemplateId);
				return true;
			}
		}
		catch(Exception e){
//			e.printStackTrace();
			throw e;
		}
	}

	/**
	 *
	public JSONObject listWorkflows() throws Exception {
		try {
			List<Workflow> workflowsList = workflowRepository.findAll();
			JSONObject workflows = new JSONObject();
			int counter = 0;
			for (Workflow wf : workflowsList) {
				workflows.put("workflow_"+counter, wf.getJSONRepresentation());
				counter++;
			}
			JSONObject obj = new JSONObject();
			obj.put("workflows", workflows);
			return obj;
		}
		catch(Exception e){
			logger.error(e.getMessage());
			throw e;
		}
	}
	
	public JSONObject listWorkflow(String workflowId) throws Exception {
		Workflow wf = workflowRepository.findOneByWorkflowId(workflowId);
		if(wf==null){
			String msg = String.format("The workflow \"%s\" does not exist.",workflowId);
        	throw new Exception(msg);
		}
		JSONObject json = wf.getJSONRepresentation();
		return json;
	}
	
	public JSONObject defineWorkflow(String workflowId, String postBody) throws JSONException,WorkflowException,Exception {
		JSONObject json = new JSONObject(postBody);
		json.put("workflowId", workflowId);
		Workflow new_wf = new Workflow(json, rabbitMQManager);
		if(workflowId==null) {
			workflowId = new_wf.getWorkflowId();
		}
		Workflow wf = workflowRepository.findOneByWorkflowId(workflowId);
		if(wf!=null){
			String msg = String.format("The workflow \"%s\" ALREADY exists.",workflowId);
        	throw new WorkflowException(msg);
		}
		new_wf = workflowRepository.save(new_wf);
		rabbitMQManager.defineWorkflow(new_wf);
		return new_wf.getJSONRepresentation();
	}

	public Object executeWorkflow(String workflowId, String documentId, boolean priority) throws Exception {
//		Workflow wf = workflowRepository.findOneByWorkflowId(workflowId);
		Workflow wf = workflowDAO.findOneByWorkflowId(workflowId, rabbitMQManager);
		if(wf==null){
			String msg = String.format("The workflow \"%s\" does not exist.",workflowId);
        	throw new Exception(msg);
		}
		Object obj = wf.execute(documentId, priority, rabbitMQManager);
		return obj;
	}

	public boolean deleteWorkflow(String workflowId) {
		try{
			workflowDAO.deleteByWorkflowId(workflowId);
			return true;
		}
		catch(Exception e){
//			e.printStackTrace();
			throw e;
		}
	}

	public JSONObject listServices() throws Exception {
		try {
			List<Service> servicesList = serviceRepository.findAll();
			JSONObject services = new JSONObject();
			int counter = 0;
			for (Service sv : servicesList) {
				services.put("service_"+counter, sv.getJSONRepresentation());
				counter++;
			}
			JSONObject obj = new JSONObject();
			obj.put("services", services);
			return obj;
		}
		catch(Exception e){
			logger.error(e.getMessage());
			throw e;
		}
	}
	
	public JSONObject listService(String serviceId) throws Exception {
		Service sv = serviceRepository.findOneByServiceId(serviceId);
		if(sv==null){
			String msg = String.format("The service \"%s\" does not exist.",serviceId);
        	throw new Exception(msg);
		}
		JSONObject json = sv.getJSONRepresentation();
		return json;
	}
	
	public JSONObject defineService(String serviceId, String postBody) throws JSONException,WorkflowException,Exception {
		JSONObject json = new JSONObject(postBody);
		json.put("serviceId", serviceId);
		Service new_sv = new Service(json, rabbitMQManager);
		if(serviceId==null) {
			serviceId = new_sv.getServiceId();
		}
		Service sv = serviceRepository.findOneByServiceId(serviceId);
		if(sv!=null){
			String msg = String.format("The service \"%s\" ALREADY exists.", serviceId);
        	throw new WorkflowException(msg);
		}
		new_sv = serviceRepository.save(new_sv);
//		rabbitMQManager.defineWorkflow(new_sv);
		return new_sv.getJSONRepresentation();
	}

	public boolean deleteService(String serviceId) {
		try{
			serviceDAO.deleteByServiceId(serviceId);
			return true;
		}
		catch(Exception e){
//			e.printStackTrace();
			throw e;
		}
	}
	 * 
	 */


}
