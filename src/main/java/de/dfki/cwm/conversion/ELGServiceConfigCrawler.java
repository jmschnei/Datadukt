package de.dfki.cwm.conversion;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

public class ELGServiceConfigCrawler {
	
	public static String retrieveELGConfigMetadata(String srvId) {
		String metadata = null;
		try {
			HttpResponse<String> response = Unirest.get("https://live.european-language-grid.eu/catalogue_backend/api/registry/metadatarecord/"+srvId+"/").asString();
			if(response.getStatus()==200) {
				metadata = response.getBody();
			}
			else {
				System.out.println("ERROR requesting ELG metadata catalog endpoint: "+response.getBody());
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		return metadata;
	}
}
