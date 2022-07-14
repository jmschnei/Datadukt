package de.dfki.slt.datadukt.conversion;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import de.dfki.slt.datadukt.controllers.Controller;
import de.dfki.slt.datadukt.controllers.restapi.ELGRestApiController;
import de.dfki.slt.datadukt.data.Format;
import de.dfki.slt.datadukt.persistence.DataManager;

public class ELGParser {

	public static void main(String[] args) throws Exception {
//		String serviceMetadata = "{\"pk\":487,\"metadata_record_identifier\":{\"pk\":488,\"metadata_record_identifier_scheme\":\"http://w3id.org/meta-share/meta-share/elg\",\"value\":\"ELG-MDR-LRS-280220-00000487\"},\"metadata_creation_date\":\"2020-02-28\",\"metadata_last_date_updated\":\"2020-04-02\",\"metadata_curator\":[{\"pk\":448,\"actor_type\":\"Person\",\"surname\":{\"en\":\"Janosik\"},\"given_name\":{\"en\":\"Miro\"},\"personal_identifier\":[{\"pk\":18,\"personal_identifier_scheme\":\"http://purl.org/spar/datacite/orcid\",\"value\":\"0000-0002-8179-525X\"},{\"pk\":17,\"personal_identifier_scheme\":\"http://w3id.org/meta-share/meta-share/elg\",\"value\":\"ELG-ENT-PER-280220-00000448\"}],\"email\":[\"Miroslav.Janosik@sail-labs.com\"]}],\"complies_with\":\"http://w3id.org/meta-share/meta-share/ELG-SHARE\",\"metadata_creator\":{\"pk\":448,\"actor_type\":\"Person\",\"surname\":{\"en\":\"Janosik\"},\"given_name\":{\"en\":\"Miro\"},\"personal_identifier\":[{\"pk\":18,\"personal_identifier_scheme\":\"http://purl.org/spar/datacite/orcid\",\"value\":\"0000-0002-8179-525X\"},{\"pk\":17,\"personal_identifier_scheme\":\"http://w3id.org/meta-share/meta-share/elg\",\"value\":\"ELG-ENT-PER-280220-00000448\"}],\"email\":[\"Miroslav.Janosik@sail-labs.com\"]},\"source_of_metadata_record\":null,\"source_metadata_record\":null,\"revision\":null,\"described_entity\":{\"pk\":618,\"entity_type\":\"LanguageResource\",\"physical_resource\":null,\"resource_name\":{\"en\":\"SAIL LABS Speech-to-text for German\"},\"resource_short_name\":{\"en\":\"SAIL-ASR-de\"},\"description\":{\"en\":\"SAIL LABS MediaMiningIndexer ASR - automatic speech recognition speech-to-text engine that provides transcription of audio with spoken sentences into text with timestamps and confidence scores, in a variety of languages.\"},\"lr_identifier\":[{\"pk\":78,\"lr_identifier_scheme\":\"http://w3id.org/meta-share/meta-share/elg\",\"value\":\"ELG-ENT-LRS-280220-00000618\"}],\"logo\":\"\",\"version\":\"1.4\",\"version_date\":null,\"update_frequency\":null,\"revision\":null,\"additional_info\":[{\"pk\":78,\"landing_page\":\"https://gitlab.com/european-language-grid/sail/sail-documents/-/blob/master/SAIL_LABS_services.md\",\"email\":\"Miroslav.Janosik@sail-labs.com\"}],\"contact\":[],\"mailing_list_name\":null,\"discussion_url\":null,\"citation_text\":null,\"ipr_holder\":[],\"keyword\":[{\"en\":\"Automatic Speech Recognition\"},{\"en\":\"ASR\"},{\"en\":\"Speech to Text\"},{\"en\":\"German\"}],\"domain\":[],\"subject\":[],\"resource_provider\":[{\"pk\":394,\"actor_type\":\"Organization\",\"organization_name\":{\"en\":\"SAIL LABS Technology\"},\"organization_identifier\":[{\"pk\":5,\"organization_identifier_scheme\":\"http://w3id.org/meta-share/meta-share/elg\",\"value\":\"ELG-ENT-ORG-280220-00000394\"}],\"website\":[\"https://www.sail-labs.com\"]}],\"publication_date\":\"2020-03-24\",\"resource_creator\":[{\"pk\":394,\"actor_type\":\"Organization\",\"organization_name\":{\"en\":\"SAIL LABS Technology\"},\"organization_identifier\":[{\"pk\":5,\"organization_identifier_scheme\":\"http://w3id.org/meta-share/meta-share/elg\",\"value\":\"ELG-ENT-ORG-280220-00000394\"}],\"website\":[\"https://www.sail-labs.com\"]}],\"creation_start_date\":null,\"creation_end_date\":null,\"funding_project\":[],\"creation_mode\":null,\"creation_details\":null,\"has_original_source\":[],\"original_source_description\":null,\"is_created_by\":[],\"intended_application\":[\"http://w3id.org/meta-share/omtd-share/SpeechRecognition\"],\"actual_use\":[],\"validated\":null,\"validation\":[],\"is_documented_by\":[{\"pk\":619,\"title\":{\"en\":\"User manual for SAIL LABS ASR services\"},\"document_identifier\":[{\"pk\":148,\"document_identifier_scheme\":\"http://w3id.org/meta-share/meta-share/other\",\"value\":\"https://gitlab.com/european-language-grid/sail/sail-documents/blob/master/asr_manual.md\"},{\"pk\":147,\"document_identifier_scheme\":\"http://w3id.org/meta-share/meta-share/elg\",\"value\":\"ELG-ENT-DOC-280220-00000619\"}]}],\"is_described_by\":[],\"is_cited_by\":[],\"is_reviewed_by\":[],\"is_part_of\":[],\"is_part_with\":[],\"is_similar_to\":[],\"is_exact_match_with\":[],\"has_metadata\":[],\"is_archived_by\":[],\"is_continuation_of\":[],\"replaces\":[],\"is_version_of\":null,\"relation\":[],\"support_type\":null,\"lr_subclass\":{\"pk\":78,\"lr_type\":\"ToolService\",\"function\":[\"http://w3id.org/meta-share/omtd-share/SpeechRecognition\"],\"software_distribution\":[{\"pk\":78,\"software_distribution_form\":\"http://w3id.org/meta-share/meta-share/dockerImage\",\"execution_location\":\"http://localhost:8080/asr/process/\",\"download_location\":\"\",\"docker_download_location\":\"registry.gitlab.com/european-language-grid/sail/sail-asr-de:release\",\"service_adapter_download_location\":\"\",\"access_location\":\"\",\"demo_location\":\"\",\"is_described_by\":[],\"additional_hw_requirements\":\"\",\"command\":\"\",\"web_service_type\":null,\"operating_system\":null,\"licence_terms\":[{\"pk\":617,\"licence_terms_name\":{\"en\":\"SAIL LABS commercial terms of use for ELG services\"},\"licence_terms_url\":[\"https://gitlab.com/european-language-grid/sail/sail-documents/blob/master/SAIL-LABS_ELG_LICENSE.md\"],\"licence_identifier\":[{\"pk\":773,\"licence_identifier_scheme\":\"http://w3id.org/meta-share/meta-share/elg\",\"value\":\"ELG-ENT-LIC-280220-00000617\"}]}],\"cost\":null,\"membership_institution\":null,\"attribution_text\":null,\"copyright_statement\":null,\"availability_start_date\":null,\"availability_end_date\":null,\"distribution_rights_holder\":[]}],\"language_dependent\":true,\"input_content_resource\":[{\"pk\":155,\"processing_resource_type\":\"http://w3id.org/meta-share/meta-share/file1\",\"samples_location\":\"\",\"language\":[{\"pk\":223,\"language_tag\":\"de\",\"language_id\":\"de\",\"script_id\":null,\"region_id\":null,\"variant_id\":null,\"language_variety_name\":null}],\"media_type\":\"http://w3id.org/meta-share/meta-share/audio\",\"data_format\":[\"http://w3id.org/meta-share/omtd-share/mp3\",\"http://w3id.org/meta-share/omtd-share/wav\"],\"character_encoding\":null,\"annotation_type\":null,\"segmentation_level\":null,\"typesystem\":null,\"annotation_schema\":null,\"annotation_resource\":null,\"modality_type\":null,\"modality_type_details\":null}],\"output_resource\":[{\"pk\":156,\"processing_resource_type\":\"http://w3id.org/meta-share/meta-share/file1\",\"samples_location\":\"\",\"language\":[{\"pk\":224,\"language_tag\":\"de\",\"language_id\":\"de\",\"script_id\":null,\"region_id\":null,\"variant_id\":null,\"language_variety_name\":null}],\"media_type\":\"http://w3id.org/meta-share/meta-share/text\",\"data_format\":[\"http://w3id.org/meta-share/omtd-share/Json\"],\"character_encoding\":[\"http://w3id.org/meta-share/meta-share/UTF-8\"],\"annotation_type\":null,\"segmentation_level\":null,\"typesystem\":null,\"annotation_schema\":null,\"annotation_resource\":null,\"modality_type\":null,\"modality_type_details\":null}],\"requires_lr\":[],\"typesystem\":null,\"annotation_schema\":null,\"annotation_resource\":[],\"ml_model\":[],\"framework\":null,\"formalism\":null,\"method\":null,\"implementation_language\":\"\",\"requires_software\":null,\"required_hardware\":null,\"running_environment_details\":null,\"running_time\":\"\",\"trl\":\"http://w3id.org/meta-share/meta-share/trl7\",\"evaluated\":false,\"evaluation\":[],\"previous_annotation_types_policy\":null,\"parameter\":[]}},\"storage_object\":{\"id\":487,\"identifier\":\"e8842a7dd7314756ad180ea0a308d82a\",\"status\":\"p\",\"etag\":\"\",\"last_modified\":null,\"size\":0,\"deleted\":false},\"owners\":[3],\"service_info\":{\"elg_gui_url\":\"https://live.european-language-grid.eu/dev/gui-ie/index-asr.html?dir=ltr\",\"elg_execution_location\":\"https://live.european-language-grid.eu/execution/async/processAudio/sailasrde\",\"elg_execution_location_sync\":\"https://live.european-language-grid.eu/execution/processAudio/sailasrde\",\"follows_elg_specs\":true,\"tool_type\":\"Speech Recognition\"}}";
		String srvMetadata = ELGServiceConfigCrawler.retrieveELGConfigMetadata("487");
		ELGParser ep = new ELGParser();
		Controller c = ep.parseControllerFromELGMetadata(srvMetadata,null);
	}

	public Controller parseControllerFromELGId (String srvId, DataManager cm) throws Exception {
		String srvMetadata = ELGServiceConfigCrawler.retrieveELGConfigMetadata(srvId);
		return parseControllerFromELGMetadata(srvMetadata, cm);
	}

	public Controller parseControllerFromELGMetadata (String metadata, DataManager cm) throws Exception {
		Controller c = null;
		JSONObject json = new JSONObject(metadata);
//		System.out.println(json.toString(2));
		
		String srvId = "";
		String srvUrlSync = "";
		String srvUrlAsync = "";
		
		String in_media_type = null;
		String in_processing_resource_type = null;		
		List<String> in_data_formats = new LinkedList<String>();
		List<String> in_character_encoding = new LinkedList<String>();
		String out_media_type = null;
		String out_processing_resource_type = null;
		List<String> out_data_formats = new LinkedList<String>();
		List<String> out_character_encoding = new LinkedList<String>();
		String function =null;
		for (String key : json.keySet()) {
//			System.out.println(key);
//			System.out.println(json.get(key));
			switch (key) {
			case "service_info":
//				System.out.println(json.getJSONObject(key).toString(2));
				srvUrlAsync = json.getJSONObject(key).getString("elg_execution_location");
				srvUrlSync = json.getJSONObject(key).getString("elg_execution_location_sync");
				break;
			case "described_entity":
//				System.out.println(json.getJSONObject(key).toString(2));
//				System.out.println(json.getJSONObject(key).getJSONObject("lr_subclass").toString(2));

				String lr_type = json.getJSONObject(key).getJSONObject("lr_subclass").getString("lr_type");
				System.out.println("DEBUG: Type: "+lr_type);
				function = json.getJSONObject(key).getJSONObject("lr_subclass").getJSONArray("function").getString(0);
				System.out.println("DEBUG: Function: "+function);

				JSONObject jsonInput = json.getJSONObject(key).getJSONObject("lr_subclass").getJSONArray("input_content_resource").getJSONObject(0);
//				System.out.println(jsonInput.toString(2));
				System.out.println("DEBUG: INPUT:");
				in_media_type = jsonInput.getString("media_type");
				System.out.println("\tDEBUG: MediaType: "+in_media_type);
				in_processing_resource_type = jsonInput.getString("processing_resource_type");
				System.out.println("\tDEBUG: Processing Resource Type: "+in_processing_resource_type);
				
				JSONArray array = jsonInput.getJSONArray("data_format");
				for (int i = 0; i < array.length(); i++) {
					in_data_formats.add(array.getString(i));
				}
				System.out.println("\tDEBUG: Data formats: "+in_data_formats);
				
				if(jsonInput.get("character_encoding") instanceof JSONArray) {
					JSONArray array2 = jsonInput.getJSONArray("character_encoding");
					for (int i = 0; i < array2.length(); i++) {
						in_character_encoding.add(array2.getString(i));
					}
				}
				System.out.println("\tDEBUG: Character encodings: "+in_character_encoding);
				
				
				JSONObject jsonOutput = json.getJSONObject(key).getJSONObject("lr_subclass").getJSONArray("output_resource").getJSONObject(0);
//				System.out.println(jsonOutput.toString(2));
				System.out.println("DEBUG: OUTPUT:");
				
				out_media_type = jsonOutput.getString("media_type");
				System.out.println("\tDEBUG: MediaType: "+out_media_type);
				out_processing_resource_type = jsonOutput.getString("processing_resource_type");
				System.out.println("\tDEBUG: Processing Resource Type: "+out_processing_resource_type);
				
				JSONArray array3 = jsonOutput.getJSONArray("data_format");
				for (int i = 0; i < array3.length(); i++) {
					out_data_formats.add(array3.getString(i));
				}
				System.out.println("\tDEBUG: Data formats: "+out_data_formats);
				
				if(jsonOutput.get("character_encoding") instanceof JSONArray) {
					JSONArray array4 = jsonOutput.getJSONArray("character_encoding");
					for (int i = 0; i < array4.length(); i++) {
						out_character_encoding.add(array4.getString(i));
					}
				}
				System.out.println("\tDEBUG: Character encodings: "+out_character_encoding);
				
				break;
			case "pk":
				srvId = json.get(key).toString();
				break;
			default:
				break;
			}
		}
		
		String controllerId = "ELG_"+srvId;
		JSONObject definition = new JSONObject();
		definition.put("controllerName","ELG Service "+srvId);
		definition.put("serviceId", function);
		definition.put("controllerId", controllerId);
		definition.put("connectionType", "restapi");

		JSONObject queues = new JSONObject();
		queues.put("nameInputNormal",controllerId+"_input_normal");
		queues.put("nameInputPriority",controllerId+"_input_prio");
		queues.put("nameOutputNormal",controllerId+"_output_normal");
		queues.put("nameOutputPriority",controllerId+"_output_prio");
		definition.put("queues", queues);

		// TODO Define ELG formats based on API.
		
		definition.put("input", new JSONObject("{\"format\": \""+in_media_type.substring(in_media_type.lastIndexOf('/')+1)+"\"}"));
		definition.put("output", new JSONObject("{\"format\": \""+out_media_type.substring(out_media_type.lastIndexOf('/')+1)+"\"}"));
		
		JSONObject connection = new JSONObject();
		connection.put("connection_type", "restapi");
		connection.put("method","POST");
//		connection.put("endpoint_url",srvUrlSync);
		connection.put("endpoint_url",srvUrlAsync);

		// TODO Include needed parameters
		
		JSONArray parameters = new JSONArray();
		parameters.put(new JSONObject("{\"name\": \"informat\",\"type\": \"parameter\",\"default_value\": \"txt\",\"required\": true}"));
		parameters.put(new JSONObject("{\"name\": \"informat\",\"type\": \"parameter\",\"default_value\": \"txt\",\"required\": true}"));
		parameters.put(new JSONObject("{\"name\": \"informat\",\"type\": \"parameter\",\"default_value\": \"txt\",\"required\": true}"));
		parameters.put(new JSONObject("{\"name\": \"informat\",\"type\": \"parameter\",\"default_value\": \"txt\",\"required\": true}"));
		parameters.put(new JSONObject("{\"name\": \"informat\",\"type\": \"parameter\",\"default_value\": \"txt\",\"required\": true}"));
		connection.put("parameters", parameters);

		// TODO Include suitable body values
		
		connection.put("body", new JSONObject("{\"content\": \"storedfile\"}"));
		
		JSONArray headers = new JSONArray();
		String oData = out_data_formats.get(0);
		String iData = in_data_formats.get(0);
		Format oFormat = Format.getFormat(oData);
		Format iFormat = Format.getFormat(iData);
		headers.put(new JSONObject("{\"name\": \"Accept\",\"type\": \"header\",\"default_value\": \""+oFormat.toString()+"\",\"required\": true}"));
		headers.put(new JSONObject("{\"name\": \"Content-Type\",\"type\": \"header\",\"default_value\": \""+iFormat.toString()+"\",\"required\": true}"));
		connection.put("headers", headers);

		JSONArray authorization = new JSONArray();
		ELGTokenParser etp = new ELGTokenParser();
//		String token = etp.getELGServiceToken(srvId);
		String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJ0MVhwNXlCb1VpREM0emxOdTcyeUNwT3hKRG1fQTdObVRkSjZRcVRiQW5nIn0.eyJqdGkiOiJlODJjNDQ5ZC00OTljLTQzNjMtYTFkZC1mMjQ0ZTk1ZTJmMzEiLCJleHAiOjE2MDA5ODYwMDQsIm5iZiI6MCwiaWF0IjoxNjAwOTc1MjA0LCJpc3MiOiJodHRwczovL2xpdmUuZXVyb3BlYW4tbGFuZ3VhZ2UtZ3JpZC5ldS9hdXRoL3JlYWxtcy9FTEciLCJhdWQiOiJlbGdfZ2F0ZWtlZXBlciIsInN1YiI6ImE1N2U3NGI3LTVjNGEtNGJjOC04NmI1LWEyMWJmNDIzZTkxYyIsInR5cCI6IkJlYXJlciIsImF6cCI6InJlYWN0LWNsaWVudCIsIm5vbmNlIjoiNWVmMmZiMGMtYTRmZS00NzUwLTgyZGYtNDYyNjk2ZTVhM2E4IiwiYXV0aF90aW1lIjoxNjAwOTc1MjAzLCJzZXNzaW9uX3N0YXRlIjoiMGZhMjQzZjktY2RkMy00YzdjLTg3M2QtMzc3YTY4ODM1YTY4IiwiYWNyIjoiMCIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwczovL2xpdmUuZXVyb3BlYW4tbGFuZ3VhZ2UtZ3JpZC5ldS8iXSwicmVzb3VyY2VfYWNjZXNzIjp7InJlYWN0LWNsaWVudCI6eyJyb2xlcyI6WyJjb25zdW1lciJdfX0sInNjb3BlIjoib3BlbmlkIHByb2ZpbGUgZW1haWwiLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwibmFtZSI6Ikp1bGlhbiBNb3Jlbm8gU2NobmVpZGVyIiwicHJlZmVycmVkX3VzZXJuYW1lIjoianVsaWFuLm1vcmVub19zY2huZWlkZXJAZGZraS5kZSIsImdpdmVuX25hbWUiOiJKdWxpYW4iLCJmYW1pbHlfbmFtZSI6Ik1vcmVubyBTY2huZWlkZXIiLCJlbWFpbCI6Imp1bGlhbi5tb3Jlbm9fc2NobmVpZGVyQGRma2kuZGUifQ.IZWn2tK14keAi8smXRF41bUgf7vqvt3J0QAMMeErMbBhN3reBO9oO2rQFdRZcnHatiVXKB9u_uZUOYaulbyUAY5eDYrFA0iklxLGEYv72HQixPyE-Z0dPQK8bqgMQQb6C_xpQsnE-wy6HXReskbsFQQnpAtbDlR07eZxPPgtuXA3ISZOlEx_IaJMpAwqZ4QAUCRRzsCLrSHP8F1YIPvAT9oatNy94Djk7Y7JZIV8CGnsPgWaMy3pbj6a05d3ewVCh6VKtFv8HYMpGiHMw7qou9MhKFM68e-OA00XuBJ3MKwhlCihTcoaYFdlC02NiL2CnP7FXZWBShFxg4H8-7UpBg";
		System.out.println("DEBUG: Obtained token: "+token);
		authorization.put(new JSONObject("{\"name\": \"tokenauth\",\"type\": \"tokenauth\",\"default_value\": \""+token+"\",\"required\": true}"));
		connection.put("authorization", authorization);

		definition.put("connection", connection);
		
		c = new ELGRestApiController(definition, cm);
		System.out.println(c.getJSONRepresentation().toString(2));
		return c;
	}

	public Controller parseControllerFromJSON(JSONObject json, DataManager dataManager2) throws Exception{
		String srvId = (json.has("serviceId")) ? json.getString("serviceId"): "null";
		if(srvId==null) {
			return null;
		}
		Controller c = parseControllerFromELGId(srvId, dataManager2);
		if(json.has("controllerId")) {
			c.setControllerId(json.getString("controllerId"));
		}
		return c;
	}
}
