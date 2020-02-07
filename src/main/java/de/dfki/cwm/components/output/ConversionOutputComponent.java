package de.dfki.cwm.components.output;

import java.util.HashMap;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.json.JSONObject;

import de.dfki.cwm.components.WorkflowComponent;
import de.dfki.cwm.data.Format;
import de.dfki.cwm.exceptions.WorkflowException;
import de.dfki.cwm.persistence.DataManager;
import de.dfki.nif.conversion.RDFConstants.RDFSerialization;
import de.dfki.nif.processing.NIFConverter;

public class ConversionOutputComponent extends OutputComponent {

	Format outputFormat;
	
	public ConversionOutputComponent(Format outputFormat) {
		this.outputFormat = outputFormat;
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
			//Convert the NIF into the corresponding output.
			Model model = NIFConverter.unserializeRDF(content, "text/turtle");
			RDFSerialization format = null;
			switch(outputFormat) {
			case TEXT:
				format = RDFSerialization.PLAINTEXT;
				break;
			case RDF:
				return content;
			case TURTLE:
				return content;
			case JSONLD:
				format = RDFSerialization.JSON_LD;
			case RDFXML:
				format = RDFSerialization.RDF_XML;
			case JSON:
				format = RDFSerialization.JSON;
			default:
				throw new Exception("OutputFormat not supported. Value must be: TEXT, RDF, JSONLD, XML.");
			}
			String outputContent = NIFConverter.serializeRDF(model, format.toString());
			return outputContent;
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
