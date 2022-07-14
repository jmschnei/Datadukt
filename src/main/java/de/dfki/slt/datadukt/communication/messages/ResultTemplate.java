package de.dfki.slt.datadukt.communication.messages;

import org.json.JSONObject;

public class ResultTemplate {


	public JSONObject getJSON() throws Exception {
		JSONObject resulttemplate = new JSONObject();
		resulttemplate.put("documentId", "FILL_IN");
		resulttemplate.put("finished", "[true|false]");
		return resulttemplate;
	}	


}
