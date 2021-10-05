package de.dfki.cwm.persistence;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.dfki.cwm.communication.rabbitmq.RabbitMQManager;
import de.dfki.cwm.controllers.ControllerRepository;
import de.dfki.cwm.controllers.ControllersManager;
import de.dfki.cwm.persistence.tasks.TaskManager;
import de.dfki.cwm.persistence.workflowexecutions.WorkflowExecutionManager;
import de.dfki.cwm.persistence.workflowtemplates.WorkflowTemplateManager;
import de.dfki.cwm.storage.FileStorage;

@Component
public class DataManager {

	@Autowired
	public WorkflowTemplateManager workflowTemplateManager;

	@Autowired
	public TaskManager taskManager;

	@Autowired
	public ControllerRepository controllerRepository;
		
	@Autowired
	public RabbitMQManager rabbitMQManager;
	
	@Autowired
	public ControllersManager controllersManager;
	
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public WorkflowExecutionManager workflowExecutionManager;
	
    @Autowired
    public FileStorage fileStorage;
    
}
