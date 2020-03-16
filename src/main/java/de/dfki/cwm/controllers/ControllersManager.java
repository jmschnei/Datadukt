package de.dfki.cwm.controllers;

import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import de.dfki.cwm.communication.rabbitmq.RabbitMQManager;
import de.dfki.cwm.exceptions.WorkflowException;

@Component
public class ControllersManager {

	Logger logger = Logger.getLogger(ControllersManager.class);

	@Autowired
	RabbitMQManager rabbitMQManager;
	
	List<Controller> conts;
	
	@Value( "${workflowmanager.controllers_path}" )
	String controllersPath;
	
	@Autowired
	ControllerRepository controllerRepository;

	public ControllersManager() throws Exception {
		conts = new LinkedList<Controller>();
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
					Controller c = new Controller(json, rabbitMQManager);
					controllerRepository.save(c);
					conts.add(c);
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

	@SuppressWarnings("deprecation")
	public void stopRunningServices() throws Exception{
		logger.info("Stopping execution Controllers of ControllersManager ... ");
		if(conts==null) {
			throw new WorkflowException("Error: controllers list is NULL in ControllersManager");
		}		
		for (Controller controller : conts) {
			controller.stop();
			logger.info("Controller ["+controller.getControllerName()+" ("+controller.getControllerId()+")] STOPPED");
		}
		logger.info(" ... stopping executions Controllers in ControllersManager DONE.");
	}

}
