package de.dfki.cwm.components.output;

import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import de.dfki.cwm.components.WorkflowComponent;
import de.dfki.cwm.data.Format;
import de.dfki.cwm.data.documents.WMDocument;
import de.dfki.cwm.data.documents.conversion.WMDeserialization;
import de.dfki.cwm.data.documents.conversion.WMSerialization;
import de.dfki.cwm.exceptions.WorkflowException;
import de.dfki.cwm.persistence.DataManager;

public class ConversionOutputComponent extends OutputComponent {

	Format outputFormat;
	
	public ConversionOutputComponent(Format outputFormat) {
		this.outputFormat = outputFormat;
	}

	@Override
	public String executeComponentSynchronous(String document, HashMap<String, String> parameters, boolean priority, DataManager manager, String outputCallback, String statusCallback, boolean persist, boolean isContent) throws WorkflowException{
		return executeComponent(document, parameters, priority, manager, outputCallback, statusCallback, persist, isContent);
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
	public String executeComponent(String content, boolean priority, DataManager manager, String outputCallback, String statusCallback, boolean persist, boolean isContent) throws WorkflowException {
		try {
			switch(outputFormat) {
			case RDF:
				return content;
			case TURTLE:
				return content;
			case JSONLD:
				WMDocument qd = WMDeserialization.fromRDF(content, "TURTLE");
				return WMSerialization.toJSON(qd, true);
//			case RDFXML:
//				format = RDFSerialization.RDF_XML;
//			case JSON:
//				format = RDFSerialization.JSON;
//			case TEXT:
//				format = RDFSerialization.PLAINTEXT;
//				break;
			default:
				throw new Exception("OutputFormat not supported. Value must be: TEXT, RDF, JSONLD, XML.");
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
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
