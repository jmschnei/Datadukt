package de.dfki.cwm.components;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.json.JSONObject;

import com.rabbitmq.client.DeliverCallback;

import de.dfki.cwm.exceptions.WorkflowException;
import de.dfki.cwm.persistence.DataManager;
import de.dfki.nif.processing.NIFCombination;
import de.dfki.nif.processing.NIFConverter;

public class WaitCombinerComponent extends WorkflowComponent{
	
	List<WorkflowComponent> componentsList;

	List<String> restingComponents;

	List<String> results = new LinkedList<String>();
	private Note msg;

	public WaitCombinerComponent(JSONObject jsonDefinition, DataManager dataManager, String workflowExecutionId) throws Exception{
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
	public String executeComponent(String document, HashMap<String, String> parameters, boolean priority, DataManager manager, String outputCallback, String statusCallback, boolean persist, boolean isContent) throws WorkflowException{
		return executeComponent(document, priority, manager, outputCallback, statusCallback, persist, isContent);
	}

	@Override
	public String executeComponent(String document, boolean priority, DataManager manager, String outputCallback, String statusCallback, boolean persist, boolean isContent) throws WorkflowException {
		try{
			results = new LinkedList<String>();
	        synchronized (msg) {
	            try{
	                System.out.println("[Wait Combiner Execution] Waiting to get notified at time:"+System.currentTimeMillis());
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
	            
//	            System.out.println("§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§");
//	            System.out.println("§§§§§§§§§        COMBINING       §§§§§§§§§§§§§§");
//	            System.out.println("§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§");
//	            //TODO Combine all the results and return them, in case it is not persist and content.
//	            System.out.println("There are "+results.size()+" results to be combined.");
//	            
	            Model resultModel = null;
	            String finalResult = null;
	            if(results.size()==0) {
	            	throw new Exception("There are no results to combine in WaitAndCombineComponent.");
	            }
	            else if(results.size()>1) {
//		            System.out.println("============================================");
		            resultModel = NIFConverter.unserializeRDF(results.get(0), "text/turtle");
	            	for (int i = 1; i < results.size(); i++) {
//	            		System.out.println("First Model: "+NIFConverter.serializeRDF(resultModel, "text/turtle"));
//	            		System.out.println("Second Model: "+results.get(i));
	            		Model intermediateModel = NIFConverter.unserializeRDF(results.get(i), "text/turtle");
		            	resultModel = NIFCombination.combineNIFModels(resultModel, intermediateModel);
					}
	            	finalResult = NIFConverter.serializeRDF(resultModel, "text/turtle");
	            }
	            else {
	            	finalResult = results.get(0);
	            }
//	            System.out.println("§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§");
//	            System.out.println("§§§§§§§§§        COMBINED        §§§§§§§§§§§§§§");
//	            System.out.println("§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§");
//	            System.out.println("FINALRESULT: "+finalResult);
//	            System.out.println("§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§");
				System.out.println("[Wait Combiner Controller ["+workflowComponentName+"]] Executed correctly.");
				return finalResult;
	        }
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
				System.out.println("[WaitCombinerComponent] Received message: '" + message + "'");
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
									results.add(json.getString("document"));
									System.out.println("Adding to the WaitCombiner results: "+json.getString("document"));
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
		
	public JSONObject getJSONRepresentation() throws Exception {
		JSONObject json = new JSONObject();
		json.put("name", workflowComponentName);
		json.put("id", workflowComponentId);
		return json;
	}

}
