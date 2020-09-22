package de.dfki.cwm.components.input;

import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import de.dfki.cwm.components.WorkflowComponent;
import de.dfki.cwm.data.Format;
import de.dfki.cwm.data.Language;
import de.dfki.cwm.exceptions.WorkflowException;
import de.dfki.cwm.persistence.DataManager;
import de.qurator.commons.QuratorDocument;
import de.qurator.commons.conversion.QuratorDeserialization;
import de.qurator.commons.conversion.QuratorSerialization;

/**
 * @author julianmorenoschneider
 * @project CurationWorkflowManager
 * @date 07.02.2020
 * @company DFKI
 * @description Component to convert the input information into a Workflow Document (Qurator Document?).
 * 
 */
public class ConversionInputComponent extends InputComponent {

	Format inputFormat;
	Language language;
	
	public ConversionInputComponent(Format inputFormat, Language language) {
		setWorkflowComponentName("ConversionInputComponent_"+inputFormat);
		setWorkflowComponentId("ConversionInputComponent_"+inputFormat);
		setWorkflowComponentType("ConverisonInput");
		this.inputFormat = inputFormat;
		this.language = language;
	}

	@Override
	public String executeComponent(String content, boolean priority, DataManager manager, String outputCallback, String statusCallback, boolean persist, boolean isContent) throws WorkflowException{
		try {
			QuratorDocument qd = null;
			switch (inputFormat) {
			case TEXT:
				qd = new QuratorDocument(content);
				break;
			case TURTLE:
			case WAV:
			case MP3:
				return content;
			case RDF:
				qd = QuratorDeserialization.fromRDF(content, inputFormat.toString());
				break;
			case JSONLD:
				qd = QuratorDeserialization.fromJSONLD(content);
				break;
				
//			case JSON:
//				qd = QuratorDeserialization.fromJSON(content);
//				model = NIFConverter.unserializeRDF(content, "application/json");
//				break;
//			case RDFXML:
//				qd = QuratorDeserialization.fromRDFXML(content);
//				model = NIFConverter.unserializeRDF(content, "application/rdf-xml");
//				break;
			default:
				throw new WorkflowException("The INPUT FORMAT ["+inputFormat+"] is not supported.");
			}
			return QuratorSerialization.toRDF(qd, "TURTLE");
		}
		catch(Exception e) {
			e.printStackTrace();
			throw new WorkflowException(e.getMessage());
		}
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

	public JSONObject getJSONRepresentation() throws Exception {
		JSONObject json = new JSONObject();
		json.put("name", getWorkflowComponentName());
		json.put("id", getWorkflowComponentId());
		return json;
	}

	public String startExecuteComponent(String documentId, boolean priority, DataManager dataManager, String outputCallback, String statusCallback, boolean persist, boolean isContent) throws WorkflowException {
		return "DONE";
	}

	public void setComponentsList(List<WorkflowComponent> componentsList) {
	}


}
