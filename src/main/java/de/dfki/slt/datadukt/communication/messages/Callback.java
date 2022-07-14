package de.dfki.slt.datadukt.communication.messages;

import org.json.JSONObject;

public class Callback {

	String queueName;
//	String url;

//	public enum ResultType {
//		JSON,
//		TEXT
//		};
//		
//	ResultType expectedResult;
//
//	ResultTemplate template;
	
	
	public JSONObject getJSON() throws Exception {
		JSONObject callbackJson = new JSONObject();
		callbackJson.put("queueName", queueName);
//		callbackJson.put("url", url);
//		callbackJson.put("expectedresult", expectedResult.name());
//		JSONObject resulttemplate = template.getJSON();
//		callbackJson.put("resulttemplate", resulttemplate);
		return callbackJson;
	}	

}
