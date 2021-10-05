package de.dfki.cwm.components;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.rabbitmq.client.DeliverCallback;

import de.dfki.cwm.communication.messages.NIFMessageWithParameters;
import de.dfki.cwm.communication.messages.NIFURLMessage;
import de.dfki.cwm.communication.rabbitmq.RabbitMQManager;
import de.dfki.cwm.data.documents.WMDocument;
import de.dfki.cwm.data.documents.conversion.WMDeserialization;
import de.dfki.cwm.data.documents.conversion.WMSerialization;
import de.dfki.cwm.exceptions.WorkflowException;
import de.dfki.cwm.persistence.DataManager;

public class RabbitMQRestApiComponent extends WorkflowComponent{

	// Logger object
	Logger logger = Logger.getLogger(RabbitMQRestApiComponent.class);

	RabbitMQManager rabbitMQManager;

	String controllerId;
	
	String callbackQueueName;

	public RabbitMQRestApiComponent(JSONObject jsonDefinition, DataManager dataManager, String workflowExecutionId) throws Exception{
//		super(jsonDefinition.getString("componentName"), jsonDefinition.getString("componentId"),jsonDefinition.getString("component_type"),workflowId);
//		workflowComponentName = jsonDefinition.getString("componentName");
		this.workflowExecutionId = workflowExecutionId;
		if(jsonDefinition.has("taskId")) {
			workflowComponentId = jsonDefinition.getString("taskId")+"_"+(new Date()).getTime();
			workflowComponentName = jsonDefinition.getString("taskId")+"_"+workflowExecutionId;
		}
		else {
			workflowComponentId = jsonDefinition.getString("controllerId")+"_"+(new Date()).getTime();
			workflowComponentName = jsonDefinition.getString("controllerId")+"_"+workflowExecutionId;
		}
		workflowComponentType = jsonDefinition.getString("component_type");
		id = workflowExecutionId;
		try{
			this.controllerId = jsonDefinition.getString("controllerId");
			this.rabbitMQManager = dataManager.rabbitMQManager;
			this.callbackQueueName = workflowExecutionId + "_" + this.workflowComponentId;			
			rabbitMQManager.declareQueue(callbackQueueName);
//			System.out.println("Constructor RABIITMQREST: "+workflowId);
//			DeliverCallback deliverCallback = (consumerTag, delivery) -> {
//				String message = new String(delivery.getBody(), "UTF-8");
//				System.out.println(" [x] Received '" + message + "'");
//				try {
//					doWork(message,false);
//				}catch(Exception e) {
//					rabbitMQManager.getChannel().basicAck(delivery.getEnvelope().getDeliveryTag(), false);
//				} finally {
//					System.out.println(" [x] Done");
//					rabbitMQManager.getChannel().basicAck(delivery.getEnvelope().getDeliveryTag(), false);
//				}
//			};
//			DeliverCallback deliverCallback2 = (consumerTag, delivery) -> {
//				String message = new String(delivery.getBody(), "UTF-8");
//				System.out.println(" [x] Received '" + message + "'");
//				try {
//					doWork(message,true);
//				}catch(Exception e) {
//					System.out.println(" [x] ERROR");
//					rabbitMQManager.getChannel().basicAck(delivery.getEnvelope().getDeliveryTag(), false);
//				} finally {
//					System.out.println(" [x] Done");
//					rabbitMQManager.getChannel().basicAck(delivery.getEnvelope().getDeliveryTag(), false);					
//				}
//			};
//			System.out.println("Consuming NORMAL QUEUE in RabbitMQRest ["+serviceControllerId+"]...");
//			rabbitMQManager.basicConsume(serviceControllerId, false, false, deliverCallback, null);
//			System.out.println("Consuming PRIORITY QUEUE in RabbitMQRest ["+serviceControllerId+"]...");
//			rabbitMQManager.basicConsume(serviceControllerId, true, false, deliverCallback2, null);
		}
		catch(Exception e){
			e.printStackTrace();
			throw e;
		}
	}

	public void doWork(String message, boolean priority) {
//		System.out.println("Received Message in RabbitMQRest ["+controllerId+"]: "+message);
		logger.info("Received Message in RabbitMQRest ["+controllerId+"]: "+message);
	}

	public String executeComponentSynchronous(String document, HashMap<String, String> parameters, boolean priority, DataManager manager, String outputCallback, String statusCallback, boolean persist, boolean isContent) throws WorkflowException{
		NIFMessageWithParameters message = new NIFMessageWithParameters(document, workflowExecutionId, callbackQueueName, persist, isContent, parameters);
		try{
			rabbitMQManager.sendMessage(message, controllerId, priority, true);
//			System.out.println("[RabbitMQRestAPI ["+workflowComponentName+"]] Executed correctly.");
			logger.info("[Synchronous@RabbitMQRestAPI ["+workflowComponentName+"]] Executed correctly.");
			
	        final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);

			DeliverCallback deliverCallback = (consumerTag, delivery) -> {
				String receivedMessage = new String(delivery.getBody(), "UTF-8");
//				System.out.println("[Synchronous RabbitMQRestAPI [\"+workflowComponentName+\"]] Received message: '" + receivedMessage + "'");
				logger.info("[Synchronous@RabbitMQRestAPI ["+workflowComponentName+"]] Received message: '" + receivedMessage + "'");
				try {
					JSONObject json = new JSONObject(receivedMessage);
					if(json.getString("workflowId").equalsIgnoreCase(this.workflowExecutionId)) {
							String serviceId = json.getString("controllerId");
							rabbitMQManager.getChannel().basicAck(delivery.getEnvelope().getDeliveryTag(), false);
							String status = json.getString("status");
							if(status.equalsIgnoreCase("ERROR") || status.equalsIgnoreCase("EXCEPTION")) {
				                response.offer("ERROR");
								logger.error("[Synchronous@RabbitMQRestApiComponent] Result is ERROR or EXCEPTION");
							}
							else {
								response.offer(json.getString("document"));
							}
					}
					else {
//						System.out.println("[Synchronous RabbitMQRestApiComponent] FALSE workflowID");
						logger.warn("[Synchronous@RabbitMQRestApiComponent] FALSE workflowID");
					}
				}catch(Exception e) {
					e.printStackTrace();
				} finally {
//					System.out.println(" [x] Done");
//					rabbitMQManager.getChannel().basicAck(delivery.getEnvelope().getDeliveryTag(), false);
				}
			};

			String callbackQueueName = this.callbackQueueName;
			rabbitMQManager.basicConsumeQueue(callbackQueueName, false, false, deliverCallback, null);
	        String result = response.take();

            WMDocument qd = null;
            if(result.equalsIgnoreCase("ERROR")) {
            	logger.error("There are no results from controller "+workflowComponentId+".");
            	throw new Exception("There are no results from controller "+workflowComponentId+".");
            }
            else {
            	qd = WMDeserialization.fromRDF(result, "TURTLE");
            	String finalResult = WMSerialization.toRDF(qd, "TURTLE");
    			logger.info("[RabbitMQRestAPI ["+workflowComponentName+"]] Executed correctly.");
            	return finalResult;
            }
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String executeComponent(String document, HashMap<String, String> parameters, boolean priority, DataManager manager, String outputCallback, String statusCallback, boolean persist, boolean isContent) throws WorkflowException {
		NIFMessageWithParameters message = new NIFMessageWithParameters(document, workflowExecutionId, callbackQueueName, persist, isContent, parameters);
		try{
			rabbitMQManager.sendMessage(message, controllerId, priority, true);
//			System.out.println("[RabbitMQRestAPI ["+workflowComponentName+"]] Executed correctly.");
			logger.info("[RabbitMQRestAPI ["+workflowComponentName+"]] Executed correctly.");
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
		return document;
	}
		
	@Override
	public String executeComponent(String document, boolean priority, DataManager manager, String outputCallback, String statusCallback, boolean persist, boolean isContent) throws WorkflowException {
		NIFURLMessage message = new NIFURLMessage(document, workflowExecutionId, callbackQueueName, persist, isContent);
		try{
//			System.out.println(message);
//			System.out.println(workflowId);
//			System.out.println("[RabbitMQRest...] Executing the component: "+workflowComponentId+"("+workflowComponentName+")");
			rabbitMQManager.sendMessage(message, controllerId, priority, true);
			
			//			byte[] messageBodyBytes = "Hello, world!".getBytes();
			//			rabbitMQChannel.basicPublish(exchangeName, routingKey, null, messageBodyBytes);
			//
			//			boolean mandatory = true;
			//			rabbitMQChannel.basicPublish(exchangeName, routingKey, mandatory,
			//					MessageProperties.PERSISTENT_TEXT_PLAIN,
			//					messageBodyBytes);
			//

			//			rabbitMQChannel.basicPublish(exchangeName, routingKey,
			//					new AMQP.BasicProperties.Builder()
			//					.contentType("text/plain")
			//					.deliveryMode(2)
			//					.priority(1)
			//					.userId("bob")
			//					.build(),
			//					message.getByteArray());

			//			Map<String, Object> headers = new HashMap<String, Object>();
			//			headers.put("latitude",  51.5252949);
			//			headers.put("longitude", -0.0905493);
			//

//			System.out.println("[RabbitMQRestAPI ["+workflowComponentName+"]] Executed correctly.");
			logger.info("[RabbitMQRestAPI ["+workflowComponentName+"]] Executed correctly.");
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
		return document;
	}

	public JSONObject getJSONRepresentation() throws Exception {
		JSONObject json = new JSONObject();
		json.put("name", workflowComponentName);
		json.put("id", workflowComponentId);
		json.put("controllerId", controllerId);
		return json;
	}

	public String startExecuteComponent(String documentId, boolean priority, DataManager dataManager, String outputCallback, String statusCallback, boolean persist, boolean isContent) throws WorkflowException {
		return "DONE";
	}

	public void setComponentsList(List<WorkflowComponent> componentsList) {
	}


}
