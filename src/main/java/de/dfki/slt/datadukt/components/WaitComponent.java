package de.dfki.slt.datadukt.components;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;

import com.rabbitmq.client.DeliverCallback;

import de.dfki.slt.datadukt.communication.rabbitmq.RabbitMQManager;
import de.dfki.slt.datadukt.exceptions.WorkflowException;
import de.dfki.slt.datadukt.persistence.DataManager;

/**
 * @author julianmorenoschneider
 * @project CurationWorkflowManager
 * @date 07.02.2020
 * @company DFKI
 * @description Class that represents a WaitComponent, that does not continue until all the connected services answer
 * 
 */
public class WaitComponent extends WorkflowComponent{
	
	List<WorkflowComponent> componentsList;

	List<String> restingComponents;

	private Note msg;

	public WaitComponent(JSONObject jsonDefinition, DataManager dataManager, String workflowExecutionId) throws Exception{
		super(jsonDefinition.getString("componentName"), jsonDefinition.getString("componentId"),jsonDefinition.getString("component_type"),workflowExecutionId);
		try{
//			System.out.println("WAIT COMPONENT CONSTRUCTOR");
			msg = new Note();
		}
		catch(Exception e){
			e.printStackTrace();
			throw e;
		}
	}


	@Override
	public String executeComponentSynchronous(String document, HashMap<String, String> parameters, boolean priority, DataManager manager, String outputCallback, String statusCallback, boolean persist, boolean isContent) throws WorkflowException{
		String msg = "Method not supported in WaitComponent.";
		System.out.println(msg);
		throw new WorkflowException(msg);
	}

	@Override
	public String executeComponent(String document, HashMap<String, String> parameters, boolean priority, DataManager manager, String outputCallback, String statusCallback, boolean persist, boolean isContent) throws WorkflowException{
		return executeComponent(document, priority, manager, outputCallback, statusCallback, persist, isContent);
	}

	@Override
	public String executeComponent(String document, boolean priority, DataManager manager, String outputCallback, String statusCallback, boolean persist, boolean isContent) throws WorkflowException {
		try{
	        synchronized (msg) {
	            try{
	                System.out.println("[Wait Execution] Waiting to get notified at time:"+System.currentTimeMillis());
	                msg.wait();
//	                if(restingComponents.isEmpty()) {
//	                	return true;
//	                }
//	                else {
//	                	msg.wait();
//	                }
	            }catch(InterruptedException e){
	                e.printStackTrace();
	            }
//	            System.out.println(" wait thread got notified at time:"+System.currentTimeMillis());
	            //process the message now
//	            System.out.println(" processed: "+msg.msg);
	        }
			System.out.println("[Wait Controller ["+workflowComponentName+"]] Executed correctly.");
			return "DONE";
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("unused")
	public String startExecuteComponent(String documentId, boolean priority, DataManager dataManager, String outputCallback, String statusCallback, boolean persist, boolean isContent) throws WorkflowException {
		try{
			List<WorkflowComponent> components = new LinkedList<WorkflowComponent>();
			restingComponents = new LinkedList<String>();
			for (WorkflowComponent wc : componentsList) {
				components.add(wc);
				if(wc instanceof RabbitMQRestApiComponent) {
					RabbitMQRestApiComponent rmrac = (RabbitMQRestApiComponent) wc;
					String controllerId = rmrac.controllerId;
					restingComponents.add(controllerId);
//					System.out.println("ADDING RESTING ELEMENT: ... "+serviceControllerId);
				}
			}
//			System.out.println("CREATION OF NOTIFIER IN START EXECUTION");
//			Notifier notifier = new Notifier(documentId, components, manager, msg);
//			notifier.run();
			DeliverCallback deliverCallback = (consumerTag, delivery) -> {
				String message = new String(delivery.getBody(), "UTF-8");
				System.out.println("[WaitComponent] Received message: '" + message + "'");
//				System.out.println("Before left components: "+restingComponents.toArray().toString());
//				for (String s : restingComponents) {
//					System.out.println("\t--"+s+"--");
//				}

				try {
//					doWork2(message,false,documentId);
					JSONObject json = new JSONObject(message);
//					System.out.println(this.workflowId);
//					System.out.println(documentId);
					if(json.getString("workflowId").equalsIgnoreCase(this.workflowExecutionId)) {
//						if(json.getString("documentId").equalsIgnoreCase(documentId)) {
							String serviceId = json.getString("controllerId");
//							System.out.println("ServiceId: "+serviceId);					

							dataManager.rabbitMQManager.getChannel().basicAck(delivery.getEnvelope().getDeliveryTag(), false);
							
							synchronized (restingComponents) {
//								System.out.println("Synchronized in Thread: "+Thread.currentThread().getName()+" for service: "+serviceId);
//								System.out.println(restingComponents);
								if(restingComponents.contains(serviceId)) {
//									System.out.println("Contains.");
									restingComponents.remove(serviceId);
//									System.out.println("After left components: "+restingComponents.toArray().toString());
//									for (String s : restingComponents) {
//										System.out.println("\t--"+s+"--");
//									}								
									if(restingComponents.isEmpty()) {
//										System.out.println("\t\t" + "NOTIFIER FINISHED");
										synchronized (msg) {
//											System.out.println(" Notifying at time:"+System.currentTimeMillis());
											msg.msg = " Notifier work done";
											msg.notify();
											// msg.notifyAll();
										}
									}
								}
							}

//						}
//						else {
//							System.out.println("[Wait Component] FALSE documentID");
//						}
					}
					else {
						System.out.println("[Wait Component] FALSE workflowID");
					}
				}catch(Exception e) {
					e.printStackTrace();
				} finally {
//					System.out.println(" [x] Done");
//					rabbitMQManager.getChannel().basicAck(delivery.getEnvelope().getDeliveryTag(), false);
				}
			};
			DeliverCallback deliverCallback2 = (consumerTag, delivery) -> {
				String message = new String(delivery.getBody(), "UTF-8");
//				System.out.println(" [Notifier Priority] Received '" + message + "'");
				try {
//					String serviceId = doWork2(message,true,documentId);
//					if(serviceId!=null) {
//						rabbitMQManager.getChannel().basicAck(delivery.getEnvelope().getDeliveryTag(), false);
//						
//						if(restingComponents.contains(serviceId)) {
//							restingComponents.remove(serviceId);
//							
//							if(restingComponents.isEmpty()) {
//					            synchronized (msg) {
//					                msg.msg = " Notifier work done";
//					                msg.notify();
//					                // msg.notifyAll();
//					            }
//							}
//						}
//					}
				}catch(Exception e) {
					e.printStackTrace();
				} finally {
//					System.out.println(" [x] Done");
//					rabbitMQManager.getChannel().basicAck(delivery.getEnvelope().getDeliveryTag(), false);	
				}
			};
			try {
				for (WorkflowComponent wc : components) {
					if(wc instanceof RabbitMQRestApiComponent) {
						RabbitMQRestApiComponent rmrac = (RabbitMQRestApiComponent) wc;
//						String serviceControllerId = rmrac.serviceControllerId;
						String callbackQueueName = rmrac.callbackQueueName;
//						System.out.println("Consuming NORMAL QUEUE in WaitComponent ["+callbackQueueName+"]...");
						dataManager.rabbitMQManager.basicConsumeQueue(callbackQueueName, false, false, deliverCallback, null);
//						System.out.println("Consuming PRIORITY QUEUE in WaitComponent ["+serviceControllerId+"]...");
						//rabbitMQManager.basicConsume(serviceControllerId, true, false, deliverCallback2, null);
					}
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
//	        synchronized (msg) {
//	            try{
//	                System.out.println(" waiting to get notified at time:"+System.currentTimeMillis());
//	                msg.wait();
//	            }catch(InterruptedException e){
//	                e.printStackTrace();
//	            }
//	            System.out.println(" wait thread got notified at time:"+System.currentTimeMillis());
//	            //process the message now
//	            System.out.println(" processed: "+msg.msg);
//	        }
////			
////			while(!components.isEmpty()){
////				Thread.sleep(millis);
////			}
			return "DONE";
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public synchronized String doWork2(String message, boolean priority, String documentId) throws Exception {
//		System.out.println("Received Message in Wait Notifer ["+workflowComponentId+"]: "+message);
		JSONObject json = new JSONObject(message);
		if(json.getString("documentId").equalsIgnoreCase(documentId)) {
			String controllerId = json.getString("controllerId");
			
			System.out.println("ControllerId: "+controllerId);					
			if(controllerId!=null) {
//		        synchronized (restingComponents) {
		        	System.out.println("Synchronized in Thread: "+Thread.currentThread().getName()+" for service: "+controllerId);
		        	System.out.println(restingComponents);
					System.out.println("Before left components:");
					for (String s : restingComponents) {
						System.out.println("\t--"+s+"--");
					}
					if(restingComponents.contains(controllerId)) {
						restingComponents.remove(controllerId);
						System.out.println("After left components:");
						for (String s : restingComponents) {
							System.out.println("\t--"+s+"--");
						}								
						if(restingComponents.isEmpty()) {
							System.out.println("\t\t" + "NOTIFIER FINISHED");
				            synchronized (msg) {
				                System.out.println(" Notifying at time:"+System.currentTimeMillis());
				                msg.msg = " Notifier work done";
				                msg.notify();
				                // msg.notifyAll();
				            }
						}
					}
//		        }
			}

			
			return controllerId;
		}
		else {
			return null;
		}
	}

	public List<WorkflowComponent> getComponentsList() {
		return componentsList;
	}

	public void setComponentsList(List<WorkflowComponent> componentsList) {
		this.componentsList = componentsList;
	}

	public class Note{
		public String msg;
	}
	
	public class Notifier implements Runnable {

		private String documentId;
	    private Note msg;
	    private List<WorkflowComponent> components;
	    private RabbitMQManager rabbitMQManager;
	    private List<String> restingComponents;

	    public Notifier(String documentId, List<WorkflowComponent> components, RabbitMQManager manager, Note msg) {
	    	System.out.println("NOTIFIER CONSTRUCTOR");
	        this.documentId = documentId;
	        this.msg = msg;
	        this.components = components;
	        rabbitMQManager = manager;
	        restingComponents = new LinkedList<String>();
	        for (WorkflowComponent wc: components) {
				if(wc instanceof RabbitMQRestApiComponent) {
					RabbitMQRestApiComponent rmrac = (RabbitMQRestApiComponent) wc;
					String serviceControllerId = rmrac.controllerId;
					restingComponents.add(serviceControllerId);
				}
			}
	        System.out.println("Generated elements in resting component.");
	        for (String s : restingComponents) {
				System.out.println("\t"+s);
			}
	    }

	    @Override
	    public void run() {
//	        String name = Thread.currentThread().getName();
//	        System.out.println("Notifier (" + name+") started");
	        
			DeliverCallback deliverCallback = (consumerTag, delivery) -> {
				String message = new String(delivery.getBody(), "UTF-8");
				System.out.println("[Notifier Normal] Received '" + message + "'");
				try {
					doWork2(message,false);
					rabbitMQManager.getChannel().basicAck(delivery.getEnvelope().getDeliveryTag(), false);

//					String serviceId = doWork(message,false);
//					System.out.println("ServiceId: "+serviceId);					
//					if(serviceId!=null) {
//						rabbitMQManager.getChannel().basicAck(delivery.getEnvelope().getDeliveryTag(), false);
//				        synchronized (restingComponents) {
//				        	System.out.println("Synchronized in Thread: "+Thread.currentThread().getName()+" for service: "+serviceId);
//				        	System.out.println(restingComponents);
//							System.out.println("Before left components:");
//							for (String s : restingComponents) {
//								System.out.println("\t--"+s+"--");
//							}
//							if(restingComponents.contains(serviceId)) {
//								restingComponents.remove(serviceId);
//								System.out.println("After left components:");
//								for (String s : restingComponents) {
//									System.out.println("\t--"+s+"--");
//								}								
//								if(restingComponents.isEmpty()) {
//									System.out.println("\t\t" + "NOTIFIER FINISHED");
//						            synchronized (msg) {
//						                System.out.println(" Notifying at time:"+System.currentTimeMillis());
//						                msg.msg = " Notifier work done";
//						                msg.notify();
//						                // msg.notifyAll();
//						            }
//								}
//							}
//				        }
//					}
				}catch(Exception e) {
					e.printStackTrace();
				} finally {
//					System.out.println(" [x] Done");
//					rabbitMQManager.getChannel().basicAck(delivery.getEnvelope().getDeliveryTag(), false);
				}
			};
			DeliverCallback deliverCallback2 = (consumerTag, delivery) -> {
				String message = new String(delivery.getBody(), "UTF-8");
//				System.out.println(" [Notifier Priority] Received '" + message + "'");
				try {
					String serviceId = doWork(message,true);
					if(serviceId!=null) {
						rabbitMQManager.getChannel().basicAck(delivery.getEnvelope().getDeliveryTag(), false);
						
						if(restingComponents.contains(serviceId)) {
							restingComponents.remove(serviceId);
							
							if(restingComponents.isEmpty()) {
					            synchronized (msg) {
					                msg.msg = " Notifier work done";
					                msg.notify();
					                // msg.notifyAll();
					            }
							}
						}
					}
				}catch(Exception e) {
					e.printStackTrace();
				} finally {
//					System.out.println(" [x] Done");
//					rabbitMQManager.getChannel().basicAck(delivery.getEnvelope().getDeliveryTag(), false);	
				}
			};
			try {
				for (WorkflowComponent wc : components) {
					if(wc instanceof RabbitMQRestApiComponent) {
						RabbitMQRestApiComponent rmrac = (RabbitMQRestApiComponent) wc;
						String serviceControllerId = rmrac.controllerId;
//						System.out.println("Consuming NORMAL QUEUE in WaitComponent ["+serviceControllerId+"]...");
						rabbitMQManager.basicConsume(serviceControllerId, false, false, deliverCallback, null);
//						System.out.println("Consuming PRIORITY QUEUE in WaitComponent ["+serviceControllerId+"]...");
						rabbitMQManager.basicConsume(serviceControllerId, true, false, deliverCallback2, null);
					}
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
	    }
	    
		public String doWork(String message, boolean priority) throws Exception {
//			System.out.println("Received Message in Wait Notifer ["+workflowComponentId+"]: "+message);
			JSONObject json = new JSONObject(message);
			if(json.getString("documentId").equalsIgnoreCase(documentId)) {
				return json.getString("serviceControllerId");
			}
			else {
				return null;
			}
		}
		
		public synchronized String doWork2(String message, boolean priority) throws Exception {
//			System.out.println("Received Message in Wait Notifer ["+workflowComponentId+"]: "+message);
			JSONObject json = new JSONObject(message);
			if(json.getString("documentId").equalsIgnoreCase(documentId)) {
				String serviceId = json.getString("serviceControllerId");
				
				System.out.println("ServiceId: "+serviceId);					
				if(serviceId!=null) {
//			        synchronized (restingComponents) {
			        	System.out.println("Synchronized in Thread: "+Thread.currentThread().getName()+" for service: "+serviceId);
			        	System.out.println(restingComponents);
						System.out.println("Before left components:");
						for (String s : restingComponents) {
							System.out.println("\t--"+s+"--");
						}
						if(restingComponents.contains(serviceId)) {
							restingComponents.remove(serviceId);
							System.out.println("After left components:");
							for (String s : restingComponents) {
								System.out.println("\t--"+s+"--");
							}								
							if(restingComponents.isEmpty()) {
								System.out.println("\t\t" + "NOTIFIER FINISHED");
					            synchronized (msg) {
					                System.out.println(" Notifying at time:"+System.currentTimeMillis());
					                msg.msg = " Notifier work done";
					                msg.notify();
					                // msg.notifyAll();
					            }
							}
						}
//			        }
				}
	
				
				return serviceId;
			}
			else {
				return null;
			}
		}
	}
	
	public JSONObject getJSONRepresentation() throws Exception {
		JSONObject json = new JSONObject();
		json.put("name", workflowComponentName);
		json.put("id", workflowComponentId);
		return json;
	}

}
