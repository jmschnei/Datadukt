package de.dfki.cwm.communication;

import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.ConsumerShutdownSignalCallback;
import com.rabbitmq.client.DeliverCallback;

import de.dfki.cwm.communication.rabbitmq.RabbitMQMessage;
import de.dfki.cwm.controllers.Controller;
import de.dfki.cwm.persistence.Workflow;

@Component
public interface CommunicationManager {

	Logger logger = Logger.getLogger(CommunicationManager.class);

	@PostConstruct
	public void setup() throws Exception ;

	public void startManager() throws Exception ;
	
	public void startManager(Properties properties) throws Exception ;	

	public void stopManager() throws Exception ;
	
	public void initializeWorkflows(List<Workflow> listWorkflows) throws Exception ;

	public void defineWorkflow(Workflow wf) ;

	public void initializeControllers(List<Controller> listControllers) throws Exception;

	public void stopWorkflows(List<Workflow> listWorkflows) throws Exception;

	public void stopControllers(List<Controller> listControllers) throws Exception;

	public boolean sendMessage(CommunicationMessage message, String serviceControllerId, boolean priority, boolean input) throws Exception ;

	public void basicConsume(String serviceControllerId, boolean priority, boolean input, 
			DeliverCallback deliverCallback, ConsumerShutdownSignalCallback cssc) throws Exception ;

	public void basicConsumeQueue(String queueName, boolean priority, boolean input, 
			DeliverCallback deliverCallback, ConsumerShutdownSignalCallback cssc) throws Exception ;

	public boolean sendMessageToQueue(RabbitMQMessage message, String callback, boolean priority, boolean b) throws Exception ;

}
