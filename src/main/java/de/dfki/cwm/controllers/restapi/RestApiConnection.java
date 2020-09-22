package de.dfki.cwm.controllers.restapi;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.HttpRequestWithBody;

import de.dfki.cwm.exceptions.WorkflowException;
import de.qurator.commons.QuratorDocument;
import de.qurator.commons.conversion.QuratorDeserialization;
import de.qurator.commons.conversion.QuratorSerialization;

/**
 * @author julianmorenoschneider
 * @project CurationWorkflowManager
 * @date 17.04.2020
 * @date_modified 
 * @company DFKI
 * @description 
 *
 */
@Entity
public class RestApiConnection {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Integer id;

	String type;

	String method;

	String endpoint;
	//@Transient

	String bodyContent;

	HashMap<String, RestApiParameter> parameters;

	HashMap<String, RestApiHeader> headers;

	HashMap<String, RestApiAuthorization> authorization;

	public RestApiConnection() throws Exception {
		type = "dummy";
		endpoint = "";
		method = "";
		parameters = new HashMap<String, RestApiParameter>();
		headers = new HashMap<String, RestApiHeader>();
		authorization = new HashMap<String, RestApiAuthorization>();
	}

	public RestApiConnection(JSONObject connection) throws Exception {
		type = connection.getString("connection_type");
		if(type==null || type.isEmpty()) {
			String msg = "Error: Controller Type NULL or EMPTY.";
			throw new WorkflowException(msg);
		}
		else if(!type.equalsIgnoreCase("restapi")) {
			String msg = "Error: Controller Type ["+type+"] not supported. For the moment only 'restapi' is supported.";
			throw new WorkflowException(msg);
		}
		endpoint = connection.getString("endpoint_url");
		method = connection.getString("method");
		parameters = new HashMap<String, RestApiParameter>();
		headers = new HashMap<String, RestApiHeader>();
		authorization = new HashMap<String, RestApiAuthorization>();

		if(connection.has("parameters")) {
			JSONArray array = connection.getJSONArray("parameters");
			for (int i = 0; i < array.length(); i++) {
				JSONObject param = (JSONObject) array.get(i);
				String name = param.getString("name");
				String type = param.getString("type");
				String defaultValue = "";
				if(param.has("default_value")) {
					defaultValue = param.getString("default_value");
				}
				boolean required = param.getBoolean("required");
				RestApiParameter cp = new RestApiParameter(name, type, defaultValue, required);
				parameters.put(cp.name, cp);
			}
		}

		if(connection.has("body")) {
			JSONObject jsonBody = connection.getJSONObject("body");
			bodyContent = jsonBody.getString("content");
		}
		if(connection.has("authorization")) {
			//			System.out.println(connection.toString(1));
			JSONArray arrayAuthorization = connection.getJSONArray("authorization");

			for (int i = 0; i < arrayAuthorization.length(); i++) {
				JSONObject param = (JSONObject) arrayAuthorization.get(i);
				String name = param.getString("name");
				String type = param.getString("type");
				String defaultValue = "";
				if(param.has("default_value")) {
					defaultValue = param.getString("default_value");
				}
				boolean required = param.getBoolean("required");
				RestApiAuthorization cp = new RestApiAuthorization(name, type, defaultValue, required);
				authorization.put(cp.name, cp);
			}
		}

		if(connection.has("headers")) {
			JSONArray arrayHeaders = connection.getJSONArray("headers");
			for (int i = 0; i < arrayHeaders.length(); i++) {
				JSONObject param = (JSONObject) arrayHeaders.get(i);
				String name = param.getString("name");
				String type = param.getString("type");
				String defaultValue = "";
				if(param.has("default_value")) {
					defaultValue = param.getString("default_value");
				}
				boolean required = param.getBoolean("required");
				RestApiHeader cp = new RestApiHeader(name, type, defaultValue, required);
				headers.put(cp.name, cp);
			}
		}
	}

	public HttpRequest getRequest(String content, boolean isContent, JSONArray inputParameters) throws Exception {
		//    	String content = null;
		HashMap<String, String> hmParameters = new HashMap<String, String>();
		if(inputParameters!=null) {
			System.out.println("DEBUG: input parameters in getRequest: "+inputParameters.toString(1));
			for (int i = 0; i < inputParameters.length(); i++) {
				JSONObject jsonObj = inputParameters.getJSONObject(i);
				Iterator<String> it = jsonObj.keys();
				while(it.hasNext()) {
					String s = it.next();
					hmParameters.put(s, jsonObj.getString(s));
				}
			}
		}
		else {
			System.out.println("DEBUG: input parameters in getRequest: NULL");
		}
		hmParameters.put("content", content);
		hmParameters.put("text", content);
//		QuratorDocument qd = QuratorDeserialization.fromRDF(content, "TURTLE");
//		hmParameters.put("text", qd.getText());
		
		//		System.out.println("GENERATING CONNECTION FOR CONTROLLER: "+endpoint);
		//		System.out.println("DEBUG: SERIALIZE QDOCUMENT");
		//		QuratorDocument qDocument = QuratorDeserialization.fromRDF(document, "TURTLE");

		String body = "";
		if(bodyContent!=null) {
			//    	if(parameters.containsKey("body")) {
			//			String defaultValue = parameters.get("body").defaultValue;
			String defaultValue = bodyContent;
			//			if(defaultValue.startsWith("{")) {
			if(defaultValue.contains("documentContentText")) {
				//					if(isContent) {
				//						body = content;
				//					}
				//					else {
				//						body = NIFReader.extractIsString(modelContent);
				//					}
				body = content;//qDocument.getText();
				System.out.println("DEBUG: INCLUDING TEXT TO BODY REQUEST: "+body);
			}
			else if(defaultValue.contains("documentContentNIF")) {
				//					if(isContent) {
				//						body = content;
				//					}
				//					else {
				//						body = NIFReader.model2String(modelContent, RDFSerialization.TURTLE);
				//					}
				body = content;//(String) QuratorSerialization.toRDF(qDocument, "TURTLE");
			}
			else if(defaultValue.contains("jsonTemplate")) {
				String templateName = defaultValue.substring(defaultValue.indexOf("_")+1);
				body = defineAndFillTemplate(templateName,hmParameters);
			}
			else if(defaultValue.contains("documentURI")) {
				if(isContent) {
					throw new Exception("documentURI body can not be defined with variable isContent=true.");
				}
				else {
					body = content;
				}
			}
			else if(defaultValue.contains("inputParameter")) {
				body = "inputParameter";
			}
			else {
				throw new Exception("body defaultValue not supported \""+defaultValue+"\".");
			}
			//			}
			//			else {
			//		    	body = defaultValue;
			//			}
			//	    	System.out.println("CONTENT FOR CONTROLLER CONNECTION: "+body);
		}

		System.out.println("DEBUG: DEFINING REQUEST: ");

		HttpRequest request;
		if(method.equalsIgnoreCase("get")) {
			request = Unirest.get(endpoint);
			//	    	System.out.println("GET CONNECTION");
		}
		else if(method.equalsIgnoreCase("put")) {
			HttpRequestWithBody request2 = Unirest.put(endpoint);
			if(bodyContent!=null) {
				request2.body(body);
			}
			request = request2;
			//	    	System.out.println("PUT CONNECTION");
		}
		else if(method.equalsIgnoreCase("post")) {
			HttpRequestWithBody request2 = Unirest.post(endpoint);
			if(bodyContent!=null) {
				//	    		System.out.println("ADDING BODY: "+body);
				request2.body(body);
			}
			request = request2;
			//	    	System.out.println("POST CONNECTION");
		}
		else if(method.equalsIgnoreCase("delete")) {
			HttpRequestWithBody request2 = Unirest.delete(endpoint);
			if(bodyContent!=null) {
				request2.body(body);
			}
			request = request2;
			//	    	System.out.println("DELETE CONNECTION");
		}
		else {
			String msg = "Error: method ["+method+"] not supported.";
			throw new WorkflowException(msg);
		}
		for (String key : parameters.keySet()) {
			RestApiParameter param = parameters.get(key);

			if(hmParameters!=null && hmParameters.containsKey(param.name)) {
				request = request.queryString(param.name, hmParameters.get(param.name));
				//        		System.out.println("SET PARAMETER: "+param.name+ " --> "+inputParameters.get(param.name));
			}
			else {
				if(param.name.equalsIgnoreCase("input")) {
					request = request.queryString(param.name, content);
				}
				else {
					request = request.queryString(param.name, param.getDefaultValue());
				}
				//        		System.out.println("SET PARAMETER: "+param.name+ " --> "+param.getDefaultValue());
			}
		}
		for (String key : headers.keySet()) {
			RestApiHeader header = headers.get(key);
			if(hmParameters!=null && hmParameters.containsKey(header.name)) {
				request = request.header(header.name, hmParameters.get(header.name));
				//        		System.out.println("SET HEADER: "+header.name+ " --> "+inputParameters.getString(header.name));
			}
			else {
				request = request.header(header.name, header.getDefaultValue());
				//        		System.out.println("SET HEADER: "+header.name+ " --> "+header.getDefaultValue());
			}
		}
		for (String key : authorization.keySet()) {
			RestApiAuthorization auth = authorization.get(key);
			if(auth.name.equalsIgnoreCase("basicauth")) {
				String user = null;
				String password = null;
				if(hmParameters!=null && hmParameters.containsKey(auth.name)) {
					String value = hmParameters.get(auth.name);
					user = value.substring(0, value.indexOf(':'));
					password = value.substring(value.indexOf(':')+1);
				}
				else {
					user = auth.user;
					password = auth.password;
				}
				request = request.basicAuth(user, password);
				//        		System.out.println("SET AUTHORIOZATION: "+user + " --> " + password);
			}
			else if(auth.name.equalsIgnoreCase("tokenauth")) {
				String token = null;
				if(hmParameters!=null && hmParameters.containsKey(auth.name)) {
					String value = hmParameters.get(auth.name);
					token = value;
				}
				else {
					token = auth.user;
				}
			    request = request.header("Authorization", "Bearer "+token);
				System.out.println("SET AUTHORIOZATION: Bearer "+token);
			}
			else {
				throw new Exception ("Unsupported Authorization method in Controller Connection");
			}
		}
		return request;
	}

	private String defineAndFillTemplate(String templateName, HashMap<String, String> hmParameters) throws Exception {
		String template = "";
		if(templateName.equalsIgnoreCase("questionTemplate")) {
			if(hmParameters!=null && hmParameters.containsKey("question")) {
				String q = hmParameters.get("question");
				template = "{\"question\":\""+q+"\"}";
			}
		}
		else if(templateName.equalsIgnoreCase("textTemplate")) {
			if(hmParameters!=null && hmParameters.containsKey("text")) {
				String t = hmParameters.get("text");
				template = "{\"text\":\""+t+"\"}";
			}
		}
		return template;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public HashMap<String, RestApiParameter> getParameters() {
		return parameters;
	}

	public void setParameters(HashMap<String, RestApiParameter> parameters) {
		this.parameters = parameters;
	}

	public JSONObject getJSONRepresentation() throws Exception{
		JSONObject json = new JSONObject();
		json.put("id", id);
		json.put("type", type);
		json.put("method", method);
		json.put("endpoint", endpoint);		
		JSONArray parametersArray = new JSONArray();
		Set<String> keys = parameters.keySet();
		for (String s : keys) {
			RestApiParameter cp = parameters.get(s);
			JSONObject jsonParameter = new JSONObject();
			jsonParameter.put("name", cp.getName());
			jsonParameter.put("type", cp.getType());
			jsonParameter.put("defaultValue", cp.getDefaultValue());
			jsonParameter.put("required", cp.isRequired());
			parametersArray.put(jsonParameter);
		}
		json.put("parameters", parametersArray);
		return json;
	}


}
