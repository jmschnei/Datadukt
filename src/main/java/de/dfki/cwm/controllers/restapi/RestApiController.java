package de.dfki.cwm.controllers.restapi;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.request.HttpRequest;

import de.dfki.cwm.communication.messages.ProcessingResultMessage;
import de.dfki.cwm.communication.rabbitmq.RabbitMQManager;
import de.dfki.cwm.controllers.Controller;
import de.dfki.cwm.conversion.ELGParser;
import de.qurator.commons.QuratorDocument;
import de.qurator.commons.conversion.Conversion;
import de.qurator.commons.conversion.Format;
import de.qurator.commons.conversion.QuratorDeserialization;
import de.qurator.commons.conversion.QuratorSerialization;

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
public class RestApiController extends Controller {

	static Logger logger = Logger.getLogger(RestApiController.class);

//	@OneToMany(cascade=CascadeType.PERSIST)
//	@ElementCollection
//	List<WorkflowComponent> components;
//	@ElementCollection
//	@Lob
//	private List<String> componentsDefinitions;

//	Date creationTime;

//	@JsonInclude(JsonInclude.Include.NON_NULL)
//	List<WorkflowComponent> components;
	
	@OneToOne(cascade=CascadeType.PERSIST)
	public RestApiConnection controllerConnection;
	
	public RestApiController() {
	}

	public RestApiController(String controllerId, String controllerName, String serviceId, String inputQueueNormal,
			String inputQueuePriority, String outputQueueNormal, String outputQueuePriority,
			RestApiConnection controllerConnection, 
			String inputFormat, String outputFormat,
			RabbitMQManager rabbitMQManager, String exchangeName,
			String routingKey) {
		super(controllerId,controllerName,serviceId,inputQueueNormal,inputQueuePriority,outputQueueNormal,outputQueuePriority,inputFormat,outputFormat,rabbitMQManager);
		this.controllerConnection = controllerConnection;
	}

	public RestApiController(JSONObject json, RabbitMQManager rabbitMQManager) throws Exception {
		super(json,rabbitMQManager);
		JSONObject connection = json.getJSONObject("connection");
		controllerConnection = new RestApiConnection(connection);
	}

	public RestApiController(String workflowString, RabbitMQManager rabbitMQManager) throws Exception {
		this(new JSONObject(workflowString),rabbitMQManager);
	}

	public String execute(String documentId, boolean priority, RabbitMQManager manager, String outputCallback, String statusCallback) throws Exception {
		return "DONE";
	}

/*    public void run() {
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
    }*/

    protected void doWork(String message, boolean priority) throws Exception {
		String status = "ERROR";
		String result = "ERROR";
    	JSONObject jsonObject = new JSONObject(message);
    	System.out.println("MESSAGE RECEIVED IN CONTROLLER "+controllerId+": "+message);
    	String document = jsonObject.getString("document");
    	System.out.println("DOCUMENT:");
    	System.out.println(document);
    	String workflowExecutionId = jsonObject.getString("workflowId");
    	String callbackQueue = jsonObject.getString("callback");
    	boolean persist = jsonObject.getBoolean("persist");
    	boolean isContent = jsonObject.getBoolean("isContent");
    	JSONArray parameters = null;
    	if(jsonObject.has("parameters")) {
//	    		System.out.println(jsonObject.getString("parameters"));
    		parameters = jsonObject.getJSONArray("parameters");
    	}
		try{
//	    	System.out.println("------ Received message in Service Controller ["+serviceName+"]: "+message);
    	
	    	//TODO For the moment only the synchronous call has been implemented.
////    	HttpResponse<String> response371 = Unirest.post(controllerConnection.getEndpoint())
////				.queryString("documentId", documentId)
////				.queryString("language", "en")
////				.queryString("fields", "content;documentId")
////				.queryString("analyzers", "standard;whitespace")
////				.queryString("overwrite", true)
////				.asString();
			
			QuratorDocument qd = QuratorDeserialization.fromRDF(document, "TTL");
			Conversion c = new Conversion();
			Format inputF = Format.getFormat(inputFormat);
			String content = c.toFormat(qd, inputF);
	    	HttpRequest hrwb = controllerConnection.getRequest(content,isContent,parameters);
//    		hrwb = hrwb.queryString("documentURI", document);
	    	
	    	System.out.println("DEBUG: WE are requesting: "+hrwb.toString());
	    	
	    	HttpResponse<String> response371 = hrwb.asString();
	    	
	    	//TODO We still have to include Synchronous and Asynchronous execution.
	    	
			if(response371.getStatus()==200) {
				status = "CORRECT";
				
				System.out.println("RESPONSE BODY IN CONTROLLER "+controllerId+": "+response371.getBody());
				
				/**
				 * Here comes the conversion of the output.
				 */
				Format outputF = Format.getFormat(outputFormat);
//				System.out.println("-----------------------------------");
//				System.out.println("-----------------------------------");
//				System.out.println("-----------------------------------");
//				System.out.println("-----------------------------------");
//				System.out.println(qd.toRDF("TURTLE"));
//				System.out.println("-----------------------------------");
//				System.out.println("-----------------------------------");
//				System.out.println("-----------------------------------");
//				System.out.println("-----------------------------------");
				QuratorDocument qdout = c.fromString(qd,response371.getBody(), outputF);
				
//				System.out.println(qdout.toRDF("TURTLE"));
//				System.out.println("-----------------------------------");
//				System.out.println("-----------------------------------");
//				System.out.println("-----------------------------------");
//				System.out.println("-----------------------------------");
				if(persist) {
//					NIFAdquirer.saveNIFDocumentInLKGManager(response371.getBody(), "text/turtle");
				}
				if(isContent) {
					result = QuratorSerialization.toRDF(qdout,"TTL");//response371.getBody();
				}
				else {
					result = document;
				}
				System.out.println("[Controller ["+controllerName+"]] Executed correctly with output: "+result);
				System.out.println("[Controller ["+controllerName+"]] Executed correctly.");
			}
			else {
				logger.error(response371.getBody());
				result = response371.getBody();
				System.out.println("[Controller ["+controllerName+"]] Executed with error: "+result);
			}
		}
		catch(Exception e){
			e.printStackTrace();
			result = e.getMessage();
			System.out.println("[Controller ["+controllerName+"]] Executed with Exception: "+result);
		}
		ProcessingResultMessage prs = new ProcessingResultMessage(result, status, controllerId, workflowExecutionId);
//		System.out.println("????????--"+prs.toString());
		rabbitMQManager.sendMessageToQueue(prs, callbackQueue, priority, false);
	}

    public void testFunctionality(String message, boolean priority) throws Exception {
		String status = "ERROR";
		String result = "ERROR";
    	JSONObject jsonObject = new JSONObject(message);
    	System.out.println("MESSAGE RECEIVED IN CONTROLLER "+controllerId+": "+message);
    	String document = jsonObject.getString("document");
    	System.out.println("DOCUMENT:");
    	System.out.println(document);
    	String workflowExecutionId = jsonObject.getString("workflowId");
    	String callbackQueue = jsonObject.getString("callback");
    	boolean persist = jsonObject.getBoolean("persist");
    	boolean isContent = jsonObject.getBoolean("isContent");
    	JSONArray parameters = null;
    	if(jsonObject.has("parameters")) {
//	    		System.out.println(jsonObject.getString("parameters"));
    		parameters = jsonObject.getJSONArray("parameters");
    	}
		try{
//	    	System.out.println("------ Received message in Service Controller ["+serviceName+"]: "+message);
    	
	    	//TODO For the moment only the synchronous call has been implemented.
////    	HttpResponse<String> response371 = Unirest.post(controllerConnection.getEndpoint())
////				.queryString("documentId", documentId)
////				.queryString("language", "en")
////				.queryString("fields", "content;documentId")
////				.queryString("analyzers", "standard;whitespace")
////				.queryString("overwrite", true)
////				.asString();
			
			QuratorDocument qd = QuratorDeserialization.fromRDF(document, "TTL");
			Conversion c = new Conversion();
			Format inputF = Format.getFormat(inputFormat);
			String content = c.toFormat(qd, inputF);
	    	HttpRequest hrwb = controllerConnection.getRequest(content,isContent,parameters);
//    		hrwb = hrwb.queryString("documentURI", document);
	    	
	    	System.out.println("DEBUG: WE are requesting: "+hrwb.toString());
	    	
	    	HttpResponse<String> response371 = hrwb.asString();
	    	
	    	//TODO We still have to include Synchronous and Asynchronous execution.
	    	
			if(response371.getStatus()==200) {
				status = "CORRECT";
				
				System.out.println("RESPONSE BODY IN CONTROLLER "+controllerId+": "+response371.getBody());
				
				/**
				 * Here comes the conversion of the output.
				 */
				Format outputF = Format.getFormat(outputFormat);
				QuratorDocument qdout = c.fromString(qd,response371.getBody(), outputF);
				
				if(persist) {
//					NIFAdquirer.saveNIFDocumentInLKGManager(response371.getBody(), "text/turtle");
				}
				if(isContent) {
					result = QuratorSerialization.toRDF(qdout,"TTL");//response371.getBody();
				}
				else {
					result = document;
				}
				System.out.println("[Controller ["+controllerName+"]] Executed correctly with output: "+result);
				System.out.println("[Controller ["+controllerName+"]] Executed correctly.");
			}
			else {
				logger.error(response371.getBody());
				result = response371.getBody();
				System.out.println("[Controller ["+controllerName+"]] Executed with error: "+result);
			}
		}
		catch(Exception e){
			e.printStackTrace();
			result = e.getMessage();
			System.out.println("[Controller ["+controllerName+"]] Executed with Exception: "+result);
		}
		ProcessingResultMessage prs = new ProcessingResultMessage(result, status, controllerId, workflowExecutionId);
//		System.out.println("????????--"+prs.toString());
		rabbitMQManager.sendMessageToQueue(prs, callbackQueue, priority, false);

//		try{
//	    	JSONObject jsonObject = new JSONObject(message);
//	    	System.out.println("MESSAGE RECEIVED IN CONTROLLER "+controllerId+": "+message);
//	    	String document = jsonObject.getString("document");
//	    	String workflowExecutionId = jsonObject.getString("workflowId");
////	    	String callbackQueue = jsonObject.getString("callback");
//	    	boolean persist = jsonObject.getBoolean("persist");
//	    	boolean isContent = jsonObject.getBoolean("isContent");
//	    	JSONArray parameters = null;
//	    	if(jsonObject.has("parameters")) {
//	    		parameters = jsonObject.getJSONArray("parameters");
//	    	}
//
////	    	System.out.println("------ Received message in Service Controller ["+serviceName+"]: "+message);
//    	
//	    	//TODO For the moment only the synchronous call has been implemented.
//////    	HttpResponse<String> response371 = Unirest.post(controllerConnection.getEndpoint())
//////				.queryString("documentId", documentId)
//////				.queryString("language", "en")
//////				.queryString("fields", "content;documentId")
//////				.queryString("analyzers", "standard;whitespace")
//////				.queryString("overwrite", true)
//////				.asString();
//	    	HttpRequest hrwb = controllerConnection.getRequest(document,isContent,parameters);
////    		hrwb = hrwb.queryString("documentURI", document);
//    	
//	    	System.out.println("URL: " + hrwb.getUrl());
//	    	System.out.println("BODY: " + new String(hrwb.getBody().getEntity().getContent().readAllBytes()));
//	    	System.out.println("HEADERS: " + hrwb.getHeaders());
//	    	
//	    	HttpResponse<String> response371 = hrwb.asString();
//
//	    	String status = "ERROR";
//			String result = "ERROR";
//			
//			
//			//TODO Include the possibility of handling Asynchronous calls to services, because if not, TIMEOUT can happen.
//			
//			if(response371.getStatus()==200) {
//				status = "CORRECT";
//				
//				System.out.println("RESPONSE BODY IN CONTROLLER "+controllerId+": "+response371.getBody());
//				if(persist) {
////					NIFAdquirer.saveNIFDocumentInLKGManager(response371.getBody(), "text/turtle");
//				}
//				if(isContent) {
//					result = response371.getBody();
//				}
//				else {
//					result = document;
//				}
//			}
//			else {
//				logger.error(response371.getBody());
//			}
//			System.out.println(result);
//			System.out.println(status);
//			ProcessingResultMessage prs = new ProcessingResultMessage(result, status, controllerId, workflowExecutionId);
//			System.out.println(prs.getByteArray());
//			System.out.println("[Controller ["+controllerName+"]] Executed correctly.");
//		}
//		catch(Exception e){
//			e.printStackTrace();
//		}
	}

	public JSONObject getJSONRepresentation() throws Exception{
		JSONObject json = super.getJSONRepresentation();
		json.put("controllerConnection", controllerConnection.getJSONRepresentation());
		return json;
	}
	
//	public void reestablishComponents(RabbitMQManager rabbitMQManager) {
//		try {
//			this.rabbitMQManager = rabbitMQManager;
//		}catch(Exception e) {
//			e.printStackTrace();
//			System.out.println("----------------------------");
//			System.out.println("ERROR in reestablishing controller ["+controllerId+"--"+controllerName+"]");
//			System.out.println("----------------------------");
//		}
//	}
	public static void main(String[] args) throws Exception {
		String document = "@prefix lkg-res: <http://lkg.lynx-project.eu/res/> .\r\n" + 
				"@prefix eli:   <http://data.europa.eu/eli/ontology#> .\r\n" + 
				"@prefix owl:   <http://www.w3.org/2002/07/owl#> .\r\n" + 
				"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\r\n" + 
				"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\r\n" + 
				"@prefix lkg:   <http://lkg.lynx-project.eu/def/> .\r\n" + 
				"@prefix skos:  <http://www.w3.org/2004/02/skos/core#> .\r\n" + 
				"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\r\n" + 
				"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\r\n" + 
				"@prefix dbo:   <http://dbpedia.org/ontology/> .\r\n" + 
				"@prefix qont:  <http://qurator-projekt.de/ontology/> .\r\n" + 
				"@prefix nif-ann: <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-annotation#> .\r\n" + 
				"@prefix dct:   <http://purl.org/dc/terms/> .\r\n" + 
				"@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\r\n" + 
				"@prefix dbr:   <http://dbpedia.org/resource/> .\r\n" + 
				"@prefix foaf:  <http://xmlns.com/foaf/0.1/> .\r\n" + 
				"\r\n" + 
				"<http://qurator-project.de/res/bad99fbe>\r\n" + 
				"        a               qont:QuratorDocument , nif:OffsetBasedString , nif:Context ;\r\n" + 
				"        lkg:metadata    [ eli:id_local  \"bad99fbe\" ;\r\n" + 
				"                          dct:language  \"en\"\r\n" + 
				"                        ] ;\r\n" + 
				"        nif:beginIndex  \"0\"^^xsd:nonNegativeInteger ;\r\n" + 
				"        nif:endIndex    \"175\"^^xsd:nonNegativeInteger ;\r\n" + 
				"        nif:isString    \"In Sachsen und Brandenburg hat die CDU bei den Wahlen stark verloren, vor allem an die AfD. Schleswig-Holsteins Ministerpräsident Daniel Günther spricht von einem Alarmsignal.\" .\r\n" + 
				"";
		QuratorDocument qd = QuratorDeserialization.fromRDF(document, "TTL");
//		Conversion c = new Conversion();
//		Format inputF = Format.getFormat(inputFormat);
//		String content = c.toFormat(qd, inputF);
		
//		System.out.println(qd.toJSON());

		String srvId = "487";
		ELGParser ep = new ELGParser();
		Controller rac = ep.parseControllerFromELGId(srvId, null);

		JSONObject obj = new JSONObject();
		obj.put("document", qd.toRDF("TURTLE"));
		obj.put("workflowId", "example101");
		obj.put("callback", "null");
		obj.put("persist", false);
		obj.put("isContent", true);		
		rac.testFunctionality(obj.toString(), false);
		
	}
}
