package de.dfki.cwm.controllers;

import java.io.File;
import java.io.FileReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import de.dfki.cwm.communication.rabbitmq.RabbitMQManager;
import de.dfki.cwm.exceptions.WorkflowException;
import de.dfki.cwm.persistence.DataManager;
import de.dfki.cwm.persistence.tasks.Task;

@Component
public class ControllersManager {

	Logger logger = Logger.getLogger(ControllersManager.class);

	@Autowired
	DataManager dataManager;
	
	@Autowired
	RabbitMQManager rabbitMQManager;
	
	List<Controller> conts;
	
	@Value( "${workflowmanager.controllers_path}" )
	String controllersPath;
	
	@Autowired
	ControllerRepository controllerRepository;

	HashMap<String, Controller> controllers = null;
	
	public ControllersManager() throws Exception {
		conts = new LinkedList<Controller>();
		controllers = new HashMap<String, Controller>();
	}

    public Controller save(Controller c) {
    	Controller c_tosave = c;
    	controllerRepository.save(c_tosave);
    	conts.add(c_tosave);
		controllers.put(c.controllerId, c);
		return c;
    }
    
	public List<Controller> findAll(){
//		Collection<Controller> collection = controllers.values();
//		List<Controller> list = new LinkedList<Controller>();
//		for (Controller c : collection) {
//			list.add(c);
//		}
//		return list;
		return conts;
	}

	@Transactional
	public Controller findOneByControllerId(String controllerId) throws Exception {
		Controller c = null;
		if(controllers.containsKey(controllerId)) {
//			System.out.println("RETURNING TASK FROM HASHMAP");
			return controllers.get(controllerId);
		}
		else if ((c=controllerRepository.findOneByControllerId(controllerId))!=null) {
//			System.out.println("RETURNING TASK FROM REPOSITORY");
			return c;
		}
		else {
			logger.warn("The Task ["+controllerId+"] does not exist.");
			return null;
		}
	}

	@Transactional
	public void deleteByControllerId(String controllerId) {
		if ((controllerRepository.findOneByControllerId(controllerId))!=null) {
			controllerRepository.deleteByControllerId(controllerId);;
		}
		if(controllers.containsKey(controllerId)) {
			controllers.remove(controllerId);
		}
	}

	public void initializeServices() throws Exception{
		logger.info("Initializing Services of ControllersManager ... ");
		if(conts==null) {
			conts = new LinkedList<Controller>();
		}		
		File f = new File(ControllersManager.class.getClassLoader().getResource(controllersPath).getFile());
		if(f.isDirectory()) {
			File files[] = f.listFiles();
			for (File file : files) {
				if(!file.getName().startsWith(".")) {
					logger.info("Initializing "+file.getName());
					String fileContent = IOUtils.toString(new FileReader(file));
					JSONObject json = new JSONObject(fileContent);
					
					try {
//						Controller c = Controller.constructController(json, rabbitMQManager);
						Controller c = Controller.constructController(json, dataManager);
						if(c!=null) {
							controllerRepository.save(c);
							conts.add(c);
						}
						else {
							logger.error("Controller not created.");
						}
					}
					catch(Exception e) {
						logger.error(e.getMessage());
						throw e;
					}					
				}
			}
		}
		else {
			String msg = "Error at initializing the Controllers: Controllers folder can not be found because it is not a folder.";
			throw new WorkflowException(msg);
		}
		logger.info(" ... initializing services in ControllersManager DONE.");
	}

	public void startRunningServices() throws Exception{
		logger.info("Starting Controllers of ControllersManager ... ");
		if(conts==null) {
			logger.error("Error: controllers list is NULL in ControllersManager");
			throw new WorkflowException("Error: controllers list is NULL in ControllersManager");
		}
		for (Controller controller : conts) {
			controller.start();
			logger.info("Controller ["+controller.getControllerName()+" ("+controller.getControllerId()+")] STARTED");
		}
		logger.info(" ... starting Controllers in ControllersManager DONE.");
	}

	public List<Controller> getControllers() {
		return conts;
	}

	public void setControllers(List<Controller> controllers) {
		this.conts = controllers;
	}
	
	public void stopServices() throws Exception{
		logger.info("Stopping Services of ControllersManager ... ");
		conts = new LinkedList<Controller>();
		logger.info(" ... stopping services in ControllersManager DONE.");
	}

	public void stopRunningServices() throws Exception{
		logger.info("Stopping execution Controllers of ControllersManager ... ");
		if(conts==null) {
			throw new WorkflowException("Error: controllers list is NULL in ControllersManager");
		}		
		for (Controller controller : conts) {
//			controller.stop();
			controller.stopController();
			logger.info("Controller ["+controller.getControllerName()+" ("+controller.getControllerId()+")] STOPPED");
		}
		logger.info(" ... stopping executions Controllers in ControllersManager DONE.");
	}

}
