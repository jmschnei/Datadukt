package de.dfki.slt.datadukt.persistence;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.dfki.slt.datadukt.communication.rabbitmq.RabbitMQManager;
import de.dfki.slt.datadukt.controllers.ControllerRepository;
import de.dfki.slt.datadukt.controllers.ControllersManager;
import de.dfki.slt.datadukt.persistence.tasks.TaskManager;
import de.dfki.slt.datadukt.persistence.workflowexecutions.WorkflowExecutionManager;
import de.dfki.slt.datadukt.persistence.workflowtemplates.WorkflowTemplateManager;
import de.dfki.slt.datadukt.storage.FileStorage;

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
