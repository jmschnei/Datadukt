package de.dfki.cwm.controllers;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import de.dfki.cwm.communication.rabbitmq.RabbitMQManager;
import de.dfki.cwm.controllers.restapi.RestApiController;

/**
 * @author Julian Moreno Schneider jumo04@dfki.de
 * @project CurationWorkflowManager
 * @date 17.04.2020
 * @date_modified 
 * @company DFKI
 * @description 
 *
 */
@Entity
public abstract class Controller extends Thread {

	static Logger logger = Logger.getLogger(Controller.class);

	public enum Status {
		CREATED
	}

	@Id
	protected String controllerId;
	protected String controllerName;
//	String serviceId;

	@Column(columnDefinition="LONGVARCHAR")
	protected String controllerDescription;

//	@OneToMany(cascade=CascadeType.PERSIST)
//	@ElementCollection
//	List<WorkflowComponent> components;
//	@ElementCollection
//	@Lob
//	private List<String> componentsDefinitions;

//	Date creationTime;

//	@JsonInclude(JsonInclude.Include.NON_NULL)
//	List<WorkflowComponent> components;
	
	public String inputQueueNormal;
	public String inputQueuePriority;
	public String outputQueueNormal;
	public String outputQueuePriority;
	
	@Transient
	protected RabbitMQManager rabbitMQManager;

	public Controller() {
	}

	public Controller(String controllerId, String controllerName, String serviceId, String inputQueueNormal,
			String inputQueuePriority, String outputQueueNormal, String outputQueuePriority,
			RabbitMQManager rabbitMQManager) {
		super();
		this.controllerId = controllerId;
		this.controllerName = controllerName;
		this.inputQueueNormal = inputQueueNormal;
		this.inputQueuePriority = inputQueuePriority;
		this.outputQueueNormal = outputQueueNormal;
		this.outputQueuePriority = outputQueuePriority;
		this.rabbitMQManager = rabbitMQManager;
	}

	public Controller(JSONObject json, RabbitMQManager rabbitMQManager) throws Exception {
		super();
		controllerName = json.getString("controllerName");
//		serviceId = json.getString("serviceId");
		controllerId = json.getString("controllerId");
		JSONObject queues = json.getJSONObject("queues");
		inputQueueNormal = queues.getString("nameInputNormal");
		inputQueuePriority = queues.getString("nameInputPriority");
		outputQueueNormal = queues.getString("nameOutputNormal");
		outputQueuePriority = queues.getString("nameOutputPriority");
		this.rabbitMQManager = rabbitMQManager;
	}

	public Controller(String workflowString, RabbitMQManager rabbitMQManager) throws Exception {
		this(new JSONObject(workflowString),rabbitMQManager);
	}

	public String execute(String documentId, boolean priority, RabbitMQManager manager, String outputCallback, String statusCallback) throws Exception {
		return "DONE";
	}

    public void run() {
//    	logger.info("ServiceController ["+serviceName+"("+serviceId+")] starting...");
//        boolean test = true;
//        if(test) {
//        	return;
//        }
        Channel channel = rabbitMQManager.getChannel();
        
        //TODO Hay que controlar que la cola de prioridad no se coma todos los recursos y la otra se quede cortada sin nada que hacer.
        
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        	String message = new String(delivery.getBody(), "UTF-8");
//            System.out.println(" [x] Received in Service Controller ["+serviceName+"] '" + message + "'");
            try {
                doWork(message,false);
            }catch(Exception e) {
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            } finally {
//                System.out.println(" [x] Done");
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }
        };
        DeliverCallback deliverCallback2 = (consumerTag, delivery) -> {
        	String message = new String(delivery.getBody(), "UTF-8");
//            System.out.println(" [x] Received in Service Controller ["+serviceName+"] '" + message + "'");
            try {
                doWork(message,true);
            }catch(Exception e) {
//                System.out.println(" [x] ERROR");
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            } finally {
//                System.out.println(" [x] Done");
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }
        };
        try {
        	logger.info("Consuming NORMAL QUEUE ["+inputQueueNormal+"] in Controller ["+controllerId+"]...");
	        channel.basicConsume(inputQueueNormal, false, deliverCallback, consumerTag -> { });
        	logger.info("Consuming PRIORITY QUEUE ["+inputQueuePriority+"] in Controller ["+controllerId+"]...");
	        channel.basicConsume(inputQueuePriority, false, deliverCallback2, consumerTag -> { });
        }catch(Exception e) {
        	e.printStackTrace();
        	return;
        }
//    	logger.info("Finished the execution of ServiceController ["+serviceName+"].");
    }

    @SuppressWarnings("deprecation")
	public void stopController() {
    	
		// TODO Auto-generated method stub

    	this.stop();
	}
    
    protected abstract void doWork(String message, boolean priority) throws Exception; 

    public abstract void testFunctionality(String message, boolean priority) throws Exception;

	public JSONObject getJSONRepresentation() throws Exception{
		JSONObject json = new JSONObject();
		json.put("controllerId", controllerId);
		json.put("controllerName", controllerName);
//		json.put("serviceId", serviceId);
		json.put("inputQueueNormal", inputQueueNormal);
		json.put("inputQueuePriority", inputQueuePriority);
		json.put("outputQueueNormal", outputQueueNormal);
		json.put("outputQueuePriority", outputQueuePriority);
//		json.put("exchangeName", exchangeName);
//		json.put("routingKey", routingKey);
		return json;
	}
		
	public void reestablishComponents(RabbitMQManager rabbitMQManager) {
		try {
			this.rabbitMQManager = rabbitMQManager;
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println("----------------------------");
			System.out.println("ERROR in reestablishing controller ["+controllerId+"--"+controllerName+"]");
			System.out.println("----------------------------");
		}
	}

	public String getControllerId() {
		return controllerId;
	}

	public void setControllerId(String controllerId) {
		this.controllerId = controllerId;
	}

	public String getInputQueueNormal() {
		return inputQueueNormal;
	}

	public void setInputQueueNormal(String inputQueueNormal) {
		this.inputQueueNormal = inputQueueNormal;
	}

	public String getInputQueuePriority() {
		return inputQueuePriority;
	}

	public void setInputQueuePriority(String inputQueuePriority) {
		this.inputQueuePriority = inputQueuePriority;
	}

	public String getOutputQueueNormal() {
		return outputQueueNormal;
	}

	public void setOutputQueueNormal(String outputQueueNormal) {
		this.outputQueueNormal = outputQueueNormal;
	}

	public String getOutputQueuePriority() {
		return outputQueuePriority;
	}

	public void setOutputQueuePriority(String outputQueuePriority) {
		this.outputQueuePriority = outputQueuePriority;
	}

	public String getControllerName() {
		return controllerName;
	}

	public void setControllerName(String controllerName) {
		this.controllerName = controllerName;
	}

	public static Controller constructController(JSONObject json, RabbitMQManager rabbitMQManager2) throws Exception {
		String type = (json.has("connectionType")) ? json.getString("connectionType"): "null";
		switch (type) {
		case "":
		case "null":
			logger.error("Controller type is not defined in controller definition json object.");
			break;
		case "restapi":
			return new RestApiController(json, rabbitMQManager2);
			/**
			 * TODO include here more construction types.
			 */
		default:
			logger.error("Controller type: "+type+" not supported.");
			break;
		}
		return null;
	}

	
}
