package de.dfki.cwm.controllers;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.request.HttpRequest;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import de.dfki.cwm.communication.messages.ProcessingResultMessage;
import de.dfki.cwm.communication.rabbitmq.RabbitMQManager;

/**
 * @author Julian Moreno Schneider jumo04@dfki.de
 *
 */
@Entity
public class Controller extends Thread {

	static Logger logger = Logger.getLogger(Controller.class);

	public enum Status {
		CREATED
	}

	//	@GeneratedValue(strategy = GenerationType.AUTO)
	//	Integer id;

	//	@JoinColumn(name = "indexId")
	@Id
	String controllerId;
	String controllerName;
//	String serviceId;

	@Column(columnDefinition="LONGVARCHAR")
	String controllerDescription;

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

	@OneToOne(cascade=CascadeType.PERSIST)
	ControllerConnection controllerConnection;
	
	@Transient
	RabbitMQManager rabbitMQManager;
	@Transient
	String exchangeName;
	@Transient
	String routingKey;

	public Controller() {
	}

	public Controller(String controllerId, String controllerName, String serviceId, String inputQueueNormal,
			String inputQueuePriority, String outputQueueNormal, String outputQueuePriority,
			ControllerConnection controllerConnection, RabbitMQManager rabbitMQManager, String exchangeName,
			String routingKey) {
		super();
		this.controllerId = controllerId;
		this.controllerName = controllerName;
//		this.serviceId = serviceId;
		this.inputQueueNormal = inputQueueNormal;
		this.inputQueuePriority = inputQueuePriority;
		this.outputQueueNormal = outputQueueNormal;
		this.outputQueuePriority = outputQueuePriority;
		this.controllerConnection = controllerConnection;
		this.rabbitMQManager = rabbitMQManager;
		this.exchangeName = exchangeName;
		this.routingKey = routingKey;
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

		JSONObject connection = json.getJSONObject("connection");
		controllerConnection = new ControllerConnection(connection);
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

    private void doWork(String message, boolean priority) throws Exception {
		try{
	    	JSONObject jsonObject = new JSONObject(message);
	    	System.out.println("MESSAGE RECEIVED IN CONTROLLER "+controllerId+": "+message);
	    	String document = jsonObject.getString("document");
	    	String workflowExecutionId = jsonObject.getString("workflowId");
	    	String callbackQueue = jsonObject.getString("callback");
	    	boolean persist = jsonObject.getBoolean("persist");
	    	boolean isContent = jsonObject.getBoolean("isContent");
	    	JSONArray parameters = null;
	    	if(jsonObject.has("parameters")) {
//	    		System.out.println(jsonObject.getString("parameters"));
	    		parameters = jsonObject.getJSONArray("parameters");
	    	}

//	    	System.out.println("------ Received message in Service Controller ["+serviceName+"]: "+message);
    	
	    	//TODO For the moment only the synchronous call has been implemented.
////    	HttpResponse<String> response371 = Unirest.post(controllerConnection.getEndpoint())
////				.queryString("documentId", documentId)
////				.queryString("language", "en")
////				.queryString("fields", "content;documentId")
////				.queryString("analyzers", "standard;whitespace")
////				.queryString("overwrite", true)
////				.asString();
	    	HttpRequest hrwb = controllerConnection.getRequest(document,isContent,parameters);
//    		hrwb = hrwb.queryString("documentURI", document);
    	
	    	HttpResponse<String> response371 = hrwb.asString();
	    		    	
	    	//TODO We still have to include Synchronous and Asynchronous execution.
	    	
			String status = "ERROR";
			String result = "ERROR";
			if(response371.getStatus()==200) {
				status = "CORRECT";
				
				System.out.println("RESPONSE BODY IN CONTROLLER "+controllerId+": "+response371.getBody());
				if(persist) {
//					NIFAdquirer.saveNIFDocumentInLKGManager(response371.getBody(), "text/turtle");
				}
				if(isContent) {
					result = response371.getBody();
				}
				else {
					result = document;
				}
			}
			else {
				logger.error(response371.getBody());
			}
			ProcessingResultMessage prs = new ProcessingResultMessage(result, status, controllerId, workflowExecutionId);
//			System.out.println("????????--"+prs.toString());
			rabbitMQManager.sendMessageToQueue(prs, callbackQueue, priority, false);
			
			System.out.println("[Controller ["+controllerName+"]] Executed correctly.");
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

    public void testFunctionality(String message, boolean priority) throws Exception {
		try{
	    	JSONObject jsonObject = new JSONObject(message);
	    	System.out.println("MESSAGE RECEIVED IN CONTROLLER "+controllerId+": "+message);
	    	String document = jsonObject.getString("document");
	    	String workflowExecutionId = jsonObject.getString("workflowId");
	    	String callbackQueue = jsonObject.getString("callback");
	    	boolean persist = jsonObject.getBoolean("persist");
	    	boolean isContent = jsonObject.getBoolean("isContent");
	    	JSONArray parameters = null;
	    	if(jsonObject.has("parameters")) {
	    		parameters = jsonObject.getJSONArray("parameters");
	    	}

//	    	System.out.println("------ Received message in Service Controller ["+serviceName+"]: "+message);
    	
	    	//TODO For the moment only the synchronous call has been implemented.
////    	HttpResponse<String> response371 = Unirest.post(controllerConnection.getEndpoint())
////				.queryString("documentId", documentId)
////				.queryString("language", "en")
////				.queryString("fields", "content;documentId")
////				.queryString("analyzers", "standard;whitespace")
////				.queryString("overwrite", true)
////				.asString();
	    	HttpRequest hrwb = controllerConnection.getRequest(document,isContent,parameters);
//    		hrwb = hrwb.queryString("documentURI", document);
    	
	    	System.out.println("URL: " + hrwb.getUrl());
	    	System.out.println("BODY: " + hrwb.getBody());
	    	System.out.println("HEADERS: " + hrwb.getHeaders());
	    	
	    	HttpResponse<String> response371 = hrwb.asString();

	    	String status = "ERROR";
			String result = "ERROR";
			
			
			//TODO Include the possibility of handling Asynchronous calls to services, because if not, TIMEOUT can happen.
			
			if(response371.getStatus()==200) {
				status = "CORRECT";
				
				System.out.println("RESPONSE BODY IN CONTROLLER "+controllerId+": "+response371.getBody());
				if(persist) {
//					NIFAdquirer.saveNIFDocumentInLKGManager(response371.getBody(), "text/turtle");
				}
				if(isContent) {
					result = response371.getBody();
				}
				else {
					result = document;
				}
			}
			else {
				logger.error(response371.getBody());
			}
			System.out.println(result);
			System.out.println(status);
			ProcessingResultMessage prs = new ProcessingResultMessage(result, status, controllerId, workflowExecutionId);
			System.out.println("[Controller ["+controllerName+"]] Executed correctly.");
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public JSONObject getJSONRepresentation() throws Exception{
		JSONObject json = new JSONObject();
		json.put("controllerId", controllerId);
		json.put("controllerName", controllerName);
//		json.put("serviceId", serviceId);
		json.put("inputQueueNormal", inputQueueNormal);
		json.put("inputQueuePriority", inputQueuePriority);
		json.put("outputQueueNormal", outputQueueNormal);
		json.put("outputQueuePriority", outputQueuePriority);
		json.put("controllerConnection", controllerConnection.getJSONRepresentation());
		json.put("exchangeName", exchangeName);
		json.put("routingKey", routingKey);
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

	
}
