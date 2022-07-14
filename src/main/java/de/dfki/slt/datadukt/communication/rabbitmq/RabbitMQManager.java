package de.dfki.slt.datadukt.communication.rabbitmq;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerShutdownSignalCallback;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.MessageProperties;

import de.dfki.slt.datadukt.communication.CommunicationManager;
import de.dfki.slt.datadukt.communication.CommunicationMessage;
import de.dfki.slt.datadukt.controllers.Controller;
import de.dfki.slt.datadukt.controllers.ControllerRepository;
import de.dfki.slt.datadukt.exceptions.WorkflowException;
import de.dfki.slt.datadukt.persistence.Workflow;

@Component
public class RabbitMQManager implements CommunicationManager{

	@Autowired
	private ApplicationContext context;

	Logger logger = Logger.getLogger(RabbitMQManager.class);
	
	public ApplicationContext getContext() {
		return context;
	}
	
//	@Value( "${rabbitmq.username}" )
//	String userName;
//	@Value( "${rabbitmq.password}" )
//	String password;
//	@Value( "${rabbitmq.virtualhost}" )
//	String virtualHost;
//	@Value( "${rabbitmq.hostname}" )
//	String hostName;
//	@Value( "${rabbitmq.portnumber}" )
//	int portNumber;

	String userName;
	String password;
	String virtualHost;
	String hostName;
	int portNumber;

	@Value( "${rabbitmq.configpath}" )
	String configpath;

	Connection rabbitMQConnection;
	Channel rabbitMQChannel;

	Channel rabbitMQChannelPublishing;
	Channel rabbitMQChannelConsuming;

//	HashMap<String,ServiceController> serviceControllers;

	HashMap<String,Controller> controllers;

	@Autowired
	ControllerRepository controllersRepository;
	
	public RabbitMQManager() throws Exception {
	}

	@PostConstruct
	public void setup() throws Exception {
		logger.info("Starting RabbitMQManager setup...");
		File config = null;
		try {
			config = new ClassPathResource(configpath).getFile();			
		} catch (Exception e) {
			config = new File(configpath);
		}
		List<String> lines = FileUtils.readLines(config, "utf-8");
		for (String line : lines) {
			String parts[] = line.split(":");
			switch (parts[0]) {
			case "rabbitmq.username":
				userName = parts[1];
				break;
			case "rabbitmq.password":
				password = parts[1];
				break;
			case "rabbitmq.virtualhost":
				virtualHost = parts[1];
				break;
			case "rabbitmq.hostname":
				hostName = parts[1];
				break;
			case "rabbitmq.portnumber":
				portNumber = Integer.parseInt(parts[1]);
				break;
			default:
				logger.error("Error in RabbitMQ configuration file: "+parts[0]);
				System.out.println("Error in RabbitMQ configuration file.");
				break;
			}
		}
		//startRabbitMQManager();
		logger.info("... DONE RabbitMQManager setup");
	}

	public void startManager() throws Exception {
		try {
			logger.info("Starting setup of RabbitMQManager ... ");
			ConnectionFactory factory = new ConnectionFactory();
			factory.setUsername(userName);
			factory.setPassword(password);
			factory.setVirtualHost(virtualHost);
			factory.setHost(hostName);
			factory.setPort(portNumber);
			System.out.println("RABBITMQ features: " + hostName+" "+portNumber+" "+virtualHost+ " " + userName + " " + password);
			rabbitMQConnection = factory.newConnection();
			rabbitMQChannel = rabbitMQConnection.createChannel();
			rabbitMQChannel.basicQos(1);
			rabbitMQChannelPublishing = rabbitMQConnection.createChannel();
			rabbitMQChannelPublishing.basicQos(1);
			rabbitMQChannelConsuming = rabbitMQConnection.createChannel();
			rabbitMQChannelConsuming.basicQos(1);
			logger.info(" ... RabbitMQManager setup DONE.");
		}
		catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public void startManager(Properties properties) throws Exception {
		logger.info("Starting setup of RabbitMQManager ... ");
		ConnectionFactory factory = new ConnectionFactory();
		
		
		factory.setUsername(properties.getProperty("rabbitmq.username"));
		System.out.println(properties.getProperty("rabbitmq.username"));

		factory.setPassword(properties.getProperty("rabbitmq.password"));
		System.out.println(properties.getProperty("rabbitmq.password"));
		factory.setVirtualHost(properties.getProperty("rabbitmq.virtualhost"));
		System.out.println(properties.getProperty("rabbitmq.virtualhost"));
		factory.setHost(properties.getProperty("rabbitmq.hostname"));
		System.out.println(properties.getProperty("rabbitmq.hostname"));
		factory.setPort(Integer.parseInt(properties.getProperty("rabbitmq.portnumber")));
		System.out.println(Integer.parseInt(properties.getProperty("rabbitmq.portnumber")));
		//		System.out.println(hostName+" "+portNumber+" "+virtualHost);
		rabbitMQConnection = factory.newConnection();
		rabbitMQChannel = rabbitMQConnection.createChannel();
		rabbitMQChannel.basicQos(1);
		rabbitMQChannelPublishing = rabbitMQConnection.createChannel();
		rabbitMQChannelPublishing.basicQos(1);
		rabbitMQChannelConsuming = rabbitMQConnection.createChannel();
		rabbitMQChannelConsuming.basicQos(1);
		logger.info(" ... RabbitMQManager setup DONE.");
	}
	
	@Override
	public void stopManager() throws Exception {
		logger.info("Stopping setup of RabbitMQManager ... ");
		
		// TODO Delete all the controlers.
		
		rabbitMQChannelPublishing.close();
		rabbitMQChannelConsuming.close();
		rabbitMQChannel.close();
		rabbitMQConnection.close();
		logger.info(" ... RabbitMQManager stop DONE.");
	}	
	
	public void initializeWorkflows(List<Workflow> listWorkflows) throws Exception{
		logger.info("Initializing Workflows in RabbitMQManager ... ");
		for (Workflow workflow : listWorkflows) {			
			defineWorkflow(workflow);
		}
		logger.info("... Initializing Workflows in RabbitMQManager DONE");
	}

	public void defineWorkflow(Workflow wf) {
		//		List<WorkflowComponent> components = wf.getComponents();		
		//		for (WorkflowComponent wc : components) {
		//			if(wc instanceof RabbitMQRestApiComponent) {
		//				for (ServiceController sc : serviceControllers.values()) {
		//
		//					// TODO Auto-generated method stub
		//					
		//				}
		//			}
		//		}
		//		logger.info("Workflow ["+wf.getName()+"] initialized.");
	}

	public void initializeControllers(List<Controller> listControllers) throws Exception{
		logger.info("Initializing Controllers in RabbitMQManager ... ");
//		serviceControllers = new HashMap<String,ServiceController>();
//		for (ServiceController controller: listControllers) {
//			serviceControllers.put(controller.getServiceId(), controller);
//
//			//			System.out.println("\t"+controller.getServiceName()+" "+controller.getServiceId());
//			//			System.out.println("\t\t"+controller.getInputQueueNormal());
//			//			System.out.println("\t\t"+controller.getInputQueuePriority());
//			//			System.out.println("\t\t"+controller.getOutputQueueNormal());
//			//			System.out.println("\t\t"+controller.getOutputQueuePriority());
//
//			try{
//				//				rabbitMQChannel.exchangeDeclare(exchangeName, "direct", true);
//				//				rabbitMQChannel.queueDeclare(queueName, true, false, false, null);
//				//				rabbitMQChannel.queueBind(queueName, exchangeName, routingKey);
//				//				rabbitMQChannel.queueBind(controller.getInputQueueNormal(), exchangeName, routingKey);
////				rabbitMQChannel.queueDeclare(controller.getOutputQueueNormal(), true, false, true, null);
////				rabbitMQChannel.queueDeclare(controller.getOutputQueuePriority(), true, false, true, null);
//				rabbitMQChannel.queueDeclare(controller.getInputQueueNormal(), true, false, true, null);
//				rabbitMQChannel.queueDeclare(controller.getInputQueuePriority(), true, false, true, null);
//				rabbitMQChannel.basicQos(1);
//			}
//			catch(Exception e){
//				e.printStackTrace();
//				throw e;
//			}
//			logger.info("ServiceController ["+controller.getServiceId()+"] initialized in RabbitMQManager.");
//		}		
		controllers = new HashMap<String,Controller>();
		List<Controller> conts = controllersRepository.findAll();
		for (Controller controller: conts) {
			controllers.put(controller.getControllerId(), controller);

			//			System.out.println("\t"+controller.getServiceName()+" "+controller.getServiceId());
			//			System.out.println("\t\t"+controller.getInputQueueNormal());
			//			System.out.println("\t\t"+controller.getInputQueuePriority());
			//			System.out.println("\t\t"+controller.getOutputQueueNormal());
			//			System.out.println("\t\t"+controller.getOutputQueuePriority());
			try{
				//				rabbitMQChannel.exchangeDeclare(exchangeName, "direct", true);
				//				rabbitMQChannel.queueDeclare(queueName, true, false, false, null);
				//				rabbitMQChannel.queueBind(queueName, exchangeName, routingKey);
				//				rabbitMQChannel.queueBind(controller.getInputQueueNormal(), exchangeName, routingKey);
//				rabbitMQChannel.queueDeclare(controller.getOutputQueueNormal(), true, false, true, null);
//				rabbitMQChannel.queueDeclare(controller.getOutputQueuePriority(), true, false, true, null);
				System.out.println("DECLARING INPUT NORMAL QUEUE: "+controller.getInputQueueNormal()+" ["+controller.getControllerId()+"]");
				rabbitMQChannel.queueDeclare(controller.getInputQueueNormal(), true, false, true, null);
				System.out.println("DECLARING INPUT PRIORITY QUEUE: "+controller.getInputQueuePriority()+" ["+controller.getControllerId()+"]");
				rabbitMQChannel.queueDeclare(controller.getInputQueuePriority(), true, false, true, null);
				rabbitMQChannel.basicQos(1);
			}
			catch(Exception e){
				e.printStackTrace();
				throw e;
			}
			logger.info("Controller ["+controller.getControllerId()+"] initialized in RabbitMQManager.");
		}		

		logger.info("... Initializing Controllers in RabbitMQManager DONE");
	}

	public void stopWorkflows(List<Workflow> listWorkflows) throws Exception{
		logger.info("Stopping Workflows in RabbitMQManager ... ");
		for (Workflow workflow : listWorkflows) {			
			//undefineWorkflow(workflow);
		}
		logger.info("... Stopping Workflows in RabbitMQManager DONE");
	}

	public void stopControllers(List<Controller> listControllers) throws Exception{
		logger.info("Stopping Controllers in RabbitMQManager ... ");
		controllers = new HashMap<String,Controller>();
		List<Controller> conts = controllersRepository.findAll();
		for (Controller controller: conts) {
			controllers.put(controller.getControllerId(), controller);
			try{
				System.out.println("DELETING INPUT NORMAL AND PRIORITY QUEUE: "+controller.getControllerId());
				rabbitMQChannel.queueDelete(controller.getInputQueueNormal());
				rabbitMQChannel.queueDelete(controller.getInputQueuePriority());
			}
			catch(Exception e){
				e.printStackTrace();
				throw e;
			}
			logger.info("Controller ["+controller.getControllerId()+"] stopped in RabbitMQManager.");
		}		

		logger.info("... Stopping Controllers in RabbitMQManager DONE");
	}

	public void declareQueue(String queueName) throws Exception{
		logger.info("Declaring queue ["+queueName+"] in RabbitMQManager ... ");
		try{
			rabbitMQChannel.queueDeclare(queueName, true, false, true, null);
			rabbitMQChannel.basicQos(1);
		}
		catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		logger.info("... declaring queue in RabbitMQManager DONE");
	}

	public void declareQueue(String queueName, boolean publish) throws Exception{
		logger.info("Declaring queue ["+queueName+"] in RabbitMQManager ... ");
		try{
			if(publish) {
				rabbitMQChannelPublishing.queueDeclare(queueName, true, false, true, null);
				rabbitMQChannelPublishing.basicQos(1);
			}
			else {
				rabbitMQChannelConsuming.queueDeclare(queueName, true, false, true, null);
				rabbitMQChannelConsuming.basicQos(1);
			}
		}
		catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		logger.info("... declaring queue in RabbitMQManager DONE");
	}

	public boolean queueExists(String queueName) {
		//TODO
		return true;
	}

	public boolean sendMessage(RabbitMQMessage message, String serviceControllerId, boolean priority, boolean input) throws Exception {
		try {
//			if(!serviceControllers.containsKey(serviceControllerId)) {
//				String msg = "Error: the controller ["+serviceControllerId+"] specified in sendMessage in RabbitMQManager does not exist.";
//				throw new WorkflowException(msg);
//			}
//			String TASK_QUEUE_NAME = "";
//			if(input) {
//				if(priority) {
//					TASK_QUEUE_NAME = serviceControllers.get(serviceControllerId).getInputQueuePriority();
//				}
//				else {
//					TASK_QUEUE_NAME = serviceControllers.get(serviceControllerId).getInputQueueNormal();
//				}
//			}
//			else {
//				if(priority) {
//					TASK_QUEUE_NAME = serviceControllers.get(serviceControllerId).getOutputQueuePriority();
//				}
//				else {
//					TASK_QUEUE_NAME = serviceControllers.get(serviceControllerId).getOutputQueueNormal();
//				}
//			}
			if(!controllers.containsKey(serviceControllerId)) {
				String msg = "Error: the controller ["+serviceControllerId+"] specified in sendMessage in RabbitMQManager does not exist.";
				throw new WorkflowException(msg);
			}
			String TASK_QUEUE_NAME = "";
			if(input) {
				if(priority) {
					TASK_QUEUE_NAME = controllers.get(serviceControllerId).getInputQueuePriority();
				}
				else {
					TASK_QUEUE_NAME = controllers.get(serviceControllerId).getInputQueueNormal();
				}
			}
			else {
				if(priority) {
					TASK_QUEUE_NAME = controllers.get(serviceControllerId).getOutputQueuePriority();
				}
				else {
					TASK_QUEUE_NAME = controllers.get(serviceControllerId).getOutputQueueNormal();
				}
			}

			System.out.println("------------------------------------------------------------");
			System.out.println("[RabbitMQManager] Sending message to: "+TASK_QUEUE_NAME);
			System.out.println("[RabbitMQManager] Message bytes: "+message.getByteArray());
			System.out.println("[RabbitMQManager] Message string: "+message.toString());
			System.out.println("------------------------------------------------------------");

			rabbitMQChannel.basicPublish("", TASK_QUEUE_NAME,
					MessageProperties.PERSISTENT_TEXT_PLAIN,
					message.getByteArray());
			return true;
		}
		catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public boolean sendMessage(CommunicationMessage message, String serviceControllerId, boolean priority, boolean input) throws Exception {
		if(message instanceof RabbitMQMessage) {
			return sendMessage((RabbitMQMessage)message, serviceControllerId, priority, input);
		}
		else {
			logger.error("Only RabbitMQMessage objects are allowed in RabbitMQManager.");
			throw new Exception("Unsupported Message format/type.");
		}
	}
	
	//	public boolean sendMessage(RabbitMQMessage message, String queueName, String exchangeName, String routingKey) throws Exception {
	//		try {
	////			if(queues.containsKey(queueName)) {
	////
	////				//TODO include the queuename.
	////				
	////				rabbitMQChannel.basicPublish(exchangeName, routingKey,
	////						new AMQP.BasicProperties.Builder()
	////						.contentType("text/plain")
	////						.deliveryMode(2)
	////						.priority(1)
	////						.userId("bob")
	////						.build(),
	////						message.getByteArray());
	////				return true;
	////			}
	////			else {
	////				String msg = "";
	////				throw new WorkflowException(msg);
	////			}
	//			return true;
	//		}
	//		catch(Exception e) {
	//			e.printStackTrace();
	//			throw e;
	//		}
	//	}

	public Channel getChannel() {
		return rabbitMQChannel;
	}

	public void setChannel(Channel rabbitMQChannel) {
		this.rabbitMQChannel = rabbitMQChannel;
	}

	public void basicConsume(String serviceControllerId, boolean priority, boolean input, 
			DeliverCallback deliverCallback, ConsumerShutdownSignalCallback cssc) throws Exception {
		String TASK_QUEUE_NAME = "";
		if(input) {
//			if(priority) {
//				TASK_QUEUE_NAME = serviceControllers.get(serviceControllerId).getInputQueuePriority();
//			}
//			else {
//				TASK_QUEUE_NAME = serviceControllers.get(serviceControllerId).getInputQueueNormal();
//			}
			if(priority) {
				TASK_QUEUE_NAME = controllers.get(serviceControllerId).getInputQueuePriority();
			}
			else {
				TASK_QUEUE_NAME = controllers.get(serviceControllerId).getInputQueueNormal();
			}
		}
		else {
//			if(priority) {
//				TASK_QUEUE_NAME = serviceControllers.get(serviceControllerId).getOutputQueuePriority();
//			}
//			else {
//				TASK_QUEUE_NAME = serviceControllers.get(serviceControllerId).getOutputQueueNormal();
//			}
			if(priority) {
				TASK_QUEUE_NAME = controllers.get(serviceControllerId).getOutputQueuePriority();
			}
			else {
				TASK_QUEUE_NAME = controllers.get(serviceControllerId).getOutputQueueNormal();
			}
		}
		//		System.out.println("Consuming "+TASK_QUEUE_NAME+" in ["+serviceControllerId+"].");
		rabbitMQChannel.basicConsume(TASK_QUEUE_NAME, false, deliverCallback, consumerTag -> { });
	}

//	public void basicConsumeQueueSynchronous(String queueName, boolean priority, boolean input, 
//			DeliverCallback deliverCallback, ConsumerShutdownSignalCallback cssc) throws Exception {
//		//		System.out.println("Consuming "+TASK_QUEUE_NAME+" in ["+serviceControllerId+"].");
//		rabbitMQChannel.basicConsume(queueName, false, callback)(queueName, false, deliverCallback, consumerTag -> { });
//	}

	public void basicConsumeQueue(String queueName, boolean priority, boolean input, 
			DeliverCallback deliverCallback, ConsumerShutdownSignalCallback cssc) throws Exception {
		//		System.out.println("Consuming "+TASK_QUEUE_NAME+" in ["+serviceControllerId+"].");
		rabbitMQChannel.basicConsume(queueName, false, deliverCallback, consumerTag -> { });
	}

	public boolean sendMessageToQueue(CommunicationMessage message, String callback, boolean priority, boolean b) throws Exception {
		try {
			System.out.println("------------------------------------------------------------");
			System.out.println("[RabbitMQManager] Sending message to: "+callback);
			System.out.println("[RabbitMQManager] Message bytes: "+message.getByteArray());
			System.out.println("[RabbitMQManager] Message string: "+message.toString());
			System.out.println("------------------------------------------------------------");

			rabbitMQChannel.basicPublish("", callback,
					MessageProperties.PERSISTENT_TEXT_PLAIN,
					message.getByteArray());
			return true;
		}
		catch(Exception e) {
			e.printStackTrace();
			throw e;
		}		
	}

	
	public static void main(String[] args) throws Exception {
		
//		String vhost = "/";
//		String host = "rabbitmqapi-88-dev-int.cloud.itandtel.at";
//		int port = 80;
//		ConnectionFactory factory = new ConnectionFactory();
//		factory.setUsername("lynxWM");
//		factory.setPassword("lynxWMPass");
//		factory.setVirtualHost(vhost);
//		factory.setHost(host);
//		factory.setPort(port);
//		Connection rabbitMQConnection;
//		Channel rabbitMQChannel;
//
//		Channel rabbitMQChannelPublishing;
//		Channel rabbitMQChannelConsuming;
//
//		//		System.out.println(hostName+" "+portNumber+" "+virtualHost);
//		System.out.println("Starting new connection...");
//		rabbitMQConnection = factory.newConnection();
//		rabbitMQChannel = rabbitMQConnection.createChannel();
//		rabbitMQChannel.basicQos(1);
//
//		rabbitMQChannelPublishing = rabbitMQConnection.createChannel();
//		rabbitMQChannelPublishing.basicQos(1);
//
//		System.out.println("...DONE");
//		rabbitMQChannelConsuming = rabbitMQConnection.createChannel();
//		rabbitMQChannelConsuming.basicQos(1);
//
//		rabbitMQChannelConsuming.close();
//		rabbitMQChannelPublishing.close();
//		rabbitMQChannel.close();
//		
//		rabbitMQConnection.close();

		String vhost = "/";
		String host = "localhost";
		int port = 5672;
		ConnectionFactory factory = new ConnectionFactory();
		factory.setUsername("guest");
		factory.setPassword("guest");
		factory.setVirtualHost(vhost);
		factory.setHost(host);
		factory.setPort(port);
		Connection rabbitMQConnection;
		Channel rabbitMQChannel;

		Channel rabbitMQChannelPublishing;
		Channel rabbitMQChannelConsuming;

		//		System.out.println(hostName+" "+portNumber+" "+virtualHost);
		System.out.println("Starting new connection...");
		rabbitMQConnection = factory.newConnection();
		
		
		

	}

}
