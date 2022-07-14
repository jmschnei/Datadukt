package de.dfki.slt.datadukt.conversion;

import org.json.JSONArray;
import org.json.JSONObject;

import de.dfki.slt.datadukt.controllers.Controller;
import de.dfki.slt.datadukt.controllers.restapi.ELGRestApiController;
import de.dfki.slt.datadukt.persistence.DataManager;

public class ELGNewParser {

	public static void main(String[] args) throws Exception {
		String srvMetadata = "{}";
		ELGNewParser ep = new ELGNewParser();
		Controller c = ep.parseELGControllerFromJSON(new JSONObject(srvMetadata),null);
		System.out.println(c.getJSONRepresentation().toString(2));
	}

	public Controller parseELGControllerFromJSON (JSONObject json, DataManager cm) throws Exception {
		Controller c = null;
		JSONObject connection = json.getJSONObject("connection");
		
		JSONArray authorization = connection.getJSONArray("authorization");
		ELGTokenParser etp = new ELGTokenParser();
		String initialToken = "eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJlNTJmMmMxYi01N2Y1LTQxYjEtOGJkZS05MjA1OGFjZjQ1YjUifQ.eyJpYXQiOjE2MjQ0NTM5MDQsImp0aSI6ImNjMTVhZTlmLWJiNDAtNGQ1Zi04NTNhLTIyMGYyZjc1NzZjYSIsImlzcyI6Imh0dHBzOi8vbGl2ZS5ldXJvcGVhbi1sYW5ndWFnZS1ncmlkLmV1L2F1dGgvcmVhbG1zL0VMRyIsImF1ZCI6Imh0dHBzOi8vbGl2ZS5ldXJvcGVhbi1sYW5ndWFnZS1ncmlkLmV1L2F1dGgvcmVhbG1zL0VMRyIsInN1YiI6ImE1N2U3NGI3LTVjNGEtNGJjOC04NmI1LWEyMWJmNDIzZTkxYyIsInR5cCI6Ik9mZmxpbmUiLCJhenAiOiJlbGctb29iIiwic2Vzc2lvbl9zdGF0ZSI6IjBjYzQzNmI5LTdjMGMtNDhhMy1iMGZiLWQxZjUxMDViZDMxYSIsInNjb3BlIjoiRUxHLXByb2ZpbGUgcHJvZmlsZSBlbWFpbCBvZmZsaW5lX2FjY2VzcyJ9.ioYyBowIxvf71wejvQ1qkKxg6E0Kg4PU9QXFaKIJowY";

		
		String token = etp.getRefreshToken(initialToken);
		System.out.println("DEBUG: Obtained token: "+token);
//		authorization.put(new JSONObject("{\"name\": \"tokenauth\",\"type\": \"tokenauth\",\"default_value\": \""+token+"\",\"required\": true}"));
		authorization.put(new JSONObject("{\"name\": \"elgtokenauth\",\"type\": \"elgtokenauth\",\"default_value\": \""+initialToken+"\",\"required\": true}"));
		connection.remove("authorization");
		connection.put("authorization", authorization);

		json.remove("connection");
		json.put("connection", connection);
		c = new ELGRestApiController(json, cm);
		return c;
	}

}
