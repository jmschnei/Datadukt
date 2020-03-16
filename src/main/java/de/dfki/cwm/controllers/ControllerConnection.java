package de.dfki.cwm.controllers;

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

import de.dfki.cwm.data.QuratorDocument;
import de.dfki.cwm.exceptions.WorkflowException;

@Entity
public class ControllerConnection {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Integer id;

	String type;

	String method;
	
	String endpoint;
	//@Transient
	
	
	String bodyContent;
	
	HashMap<String, ConnectionParameter> parameters;

	HashMap<String, ConnectionHeader> headers;

	HashMap<String, ConnectionAuthorization> authorization;

	public ControllerConnection() throws Exception {
		type = "dummy";
		endpoint = "";
		method = "";
		parameters = new HashMap<String, ConnectionParameter>();
		headers = new HashMap<String, ConnectionHeader>();
		authorization = new HashMap<String, ConnectionAuthorization>();
	}

	public ControllerConnection(JSONObject connection) throws Exception {
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
		parameters = new HashMap<String, ConnectionParameter>();
		headers = new HashMap<String, ConnectionHeader>();
		authorization = new HashMap<String, ConnectionAuthorization>();

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
				ConnectionParameter cp = new ConnectionParameter(name, type, defaultValue, required);
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
				ConnectionAuthorization cp = new ConnectionAuthorization(name, type, defaultValue, required);
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
				ConnectionHeader cp = new ConnectionHeader(name, type, defaultValue, required);
				headers.put(cp.name, cp);
			}
		}
	}
	
	public HttpRequest getRequest(String document, boolean isContent, JSONArray inputParameters) throws Exception {
//    	String content = null;
		
		
		HashMap<String, String> hmParameters = new HashMap<String, String>();
		if(inputParameters!=null) {
			for (int i = 0; i < inputParameters.length(); i++) {
				JSONObject jsonObj = inputParameters.getJSONObject(i);
				Iterator<String> it = jsonObj.keys();
				while(it.hasNext()) {
					String s = it.next();
					hmParameters.put(s, jsonObj.getString(s));
				}
			}
		}

//		System.out.println("GENERATING CONNECTION FOR CONTROLLER: "+endpoint);
		
		QuratorDocument qDocument = new QuratorDocument();
		qDocument.unserializeFrom(document, "text/turtle");
    	
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
					body = qDocument.getText();
				}
				else if(defaultValue.contains("documentContentNIF")) {
//					if(isContent) {
//						body = content;
//					}
//					else {
//						body = NIFReader.model2String(modelContent, RDFSerialization.TURTLE);
//					}
					body = (String) qDocument.serializeTo("text/turtle");
				}
				else if(defaultValue.contains("jsonTemplate")) {
					String templateName = defaultValue.substring(defaultValue.indexOf("_")+1);
					body = defineAndFillTemplate(templateName,inputParameters);
				}
				else if(defaultValue.contains("documentURI")) {
					if(isContent) {
						throw new Exception("documentURI body can not be defined with variable isContent=true.");
					}
					else {
						body = document;
					}
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
    		ConnectionParameter param = parameters.get(key);
    		
    		if(hmParameters!=null && hmParameters.containsKey(param.name)) {
        		request = request.queryString(param.name, hmParameters.get(param.name));
//        		System.out.println("SET PARAMETER: "+param.name+ " --> "+inputParameters.get(param.name));
    		}
    		else {
        		request = request.queryString(param.name, param.getDefaultValue());
//        		System.out.println("SET PARAMETER: "+param.name+ " --> "+param.getDefaultValue());
    		}
		}
    	for (String key : headers.keySet()) {
    		ConnectionHeader header = headers.get(key);
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
    		ConnectionAuthorization auth = authorization.get(key);
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
    		else {
    			throw new Exception ("Unsupported Authorization method in Controller Connection");
    		}
		}
    	return request;
	}

	private String defineAndFillTemplate(String templateName, JSONArray inputParameters) throws Exception {
		String template = "";
		if(templateName.equalsIgnoreCase("questionTemplate")) {
			for (int i = 0; i < inputParameters.length(); i++) {
				JSONObject jsonObj = inputParameters.getJSONObject(i);
				if(jsonObj!=null && jsonObj.has("question")) {
		    		String q = jsonObj.getString("question");
		    		template = "{\"question\":\""+q+"\"}";
				}
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

	public HashMap<String, ConnectionParameter> getParameters() {
		return parameters;
	}

	public void setParameters(HashMap<String, ConnectionParameter> parameters) {
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
			ConnectionParameter cp = parameters.get(s);
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
