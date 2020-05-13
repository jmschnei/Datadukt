package de.dfki.cwm.components.output;

import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import de.dfki.cwm.components.WorkflowComponent;
import de.dfki.cwm.exceptions.WorkflowException;
import de.dfki.cwm.persistence.DataManager;
import de.qurator.commons.BaseAnnotation;
import de.qurator.commons.QuratorDocument;
import de.qurator.commons.conversion.QuratorDeserialization;

public class XMLOutputComponent extends OutputComponent {

	public XMLOutputComponent() {
	}

	@Override
	public String executeComponent(String document, HashMap<String, String> parameters, boolean priority, DataManager manager, String outputCallback, String statusCallback, boolean persist, boolean isContent) throws WorkflowException{
		try {
			return executeComponent(document, priority, manager, outputCallback, statusCallback, persist, isContent);
		}
		catch(Exception e) {
			e.printStackTrace();
			throw new WorkflowException(e.getMessage());
		}		
	}

	@Override
	public String executeComponent(String document, boolean priority, DataManager manager, String outputCallback, String statusCallback, boolean persist, boolean isContent) throws WorkflowException {
		//Annotate the NIF text with the annotations.

		// TODO 

		String annotatedContent = document;
		try{
			String high = "";
			QuratorDocument qd = QuratorDeserialization.fromRDF(document, "TURTLE");
			
			String anno = qd.getText();
			//Get all the annotated information that we want to use for highlighting. 
			List<BaseAnnotation> annotations = qd.getAnnotations();
//			Map<String,Map<String,String>> map = NIFReader.extractEntitiesExtended(jena);
//			LinkedList<Map<String,String>> list = new LinkedList<Map<String,String>>();
//
//			String initTag = "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#beginIndex";
//			String endTag = "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#endIndex";
//			String typeTag = "http://www.w3.org/2005/11/its/rdf#taClassRef";
//
//			if (map != null && !map.isEmpty()){
//				Set<String> keys = map.keySet();
//				for (String k : keys) {
//					// System.out.println("Key: "+k);
//					Map<String, String> internalMap = map.get(k);
//
//					int init = Integer.parseInt(internalMap.get(initTag));
//					// int end = Integer.parseInt(internalMap.get(endTag));
//					boolean added = false;
//
//					for (int i = 0; i < list.size() && !added; i++) {
//						Map<String, String> mapL = list.get(i);
//						int auxInit = Integer.parseInt(mapL.get(initTag));
//						if (init < auxInit) {
//							added = true;
//							list.add(i, internalMap);
//						}
//					}
//
//					if (!added) {
//						list.add(internalMap);
//					}
//					// Set<String> kes2 = internalMap.keySet();
//					// for (String k2 : kes2) {
//					// System.out.println("\t" + k2 + " <--> " +
//					// internalMap.get(k2));
//					// }
//				}
//			}
//			
//			int offset = 0;
//			for (Map<String,String> mm : list) {
//				int init = Integer.parseInt(mm.get(initTag));
//				int end = Integer.parseInt(mm.get(endTag));
//				String type = mm.get(typeTag);
//				String label = "";
//				if(type.contains("Location")){
//					label = "label-warning";
//				}
//				else if(type.contains("Organisation")){
//					label = "label-info";
//				}
//				else if(type.contains("Person")){
//					label = "label-success";
//				}
//				else if(type.contains("TemporalEntity")){
//					label = "label-primary";
//				}
//				else{
//					label = "label-default";
//				}
//
////						System.out.println("\toffset: "+offset+" INIT: "+init+" END: "+end+"  type:"+type);
//				
//				if(offset>init){
//					high = high + "(<span class=\"label "+label+"\">";
//					high = high + anno.substring(init, end);
//					high = high + "</span>)";
//
//					//TODO Consider painting when the ending is longer than the previous clashing endind.
//				}
//				else{
//					high = high + anno.substring(offset, init);
//					high = high + "<span class=\"label "+label+"\">";
//					high = high + anno.substring(init, end);
//					high = high + "</span>";
//				}
//				
//				offset = end;
////						Set<String> kes2 = mm.keySet();
////						for (String k2 : kes2) {
////							System.out.println("\t" + k2 + " <--> " + mm.get(k2));
////						}
//			}
//			high = high + anno.substring(offset);
//			
//			String translated = NIFReader.extractITSRDFTarget(jena);
//			String language = NIFReader.extractITSRDFTargetLanguage(jena);
//			if(translated!=null){
//				high = high + "<div class=\"col-lg-1\"></div>";
//				high = high + "<div class=\"translateText col-lg-11 col-md-5 alert alert-danger\">";
//				high = high + "<span class=\"label label-default\">"+language+"</span>"+translated+"</div>";
//			}
			return high;
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("ERROR at generating the highlighted content of the document");
		}
		return annotatedContent;
	}
	
	public JSONObject getJSONRepresentation() throws Exception {
		JSONObject json = new JSONObject();
		json.put("componentName", getWorkflowComponentName());
		json.put("componentId", getWorkflowComponentId());
		return json;
	}

	public String startExecuteComponent(String documentId, boolean priority, DataManager dataManager, String outputCallback, String statusCallback, boolean persist, boolean isContent) throws WorkflowException {
		return "DONE";
	}

	public void setComponentsList(List<WorkflowComponent> componentsList) {
	}


}
