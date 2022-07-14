package de.dfki.slt.datadukt.persistence.workflowtemplates;

import org.json.JSONObject;

public class TemplateParser {
	
	/**
	 * This method evaluates a Template Description in order to check if it conforms to the Lynx Workflow Description Schema (LWDS)
	 * @param templateDescription {@link JSONObject} containing the description of the template
	 * @return true if the description conforms to LWDS
	 */
	public static boolean verifyTemplateDescription(String templateDescription) {
		try {
			JSONObject json = new JSONObject(templateDescription);
			return verifyTemplateDescription(json);
		}
		catch(Exception e) {
			e.printStackTrace();
			System.out.println("ERROR: The JSON format of the input string is not valid!!");
			return false;
		}
	}
	
	/**
	 * This method evaluates a Template Description in order to check if it conforms to the Lynx Workflow Description Schema (LWDS)
	 * @param templateDescription String containing  the JSON description of the template
	 * @return true if the description conforms to LWDS
	 */
	public static boolean verifyTemplateDescription(JSONObject templateDescription) {

		//TODO Check required fields.
		
		//TODO Check optional fields.
		
		//TODO Check components.
		
		return true;
	}
	
}
