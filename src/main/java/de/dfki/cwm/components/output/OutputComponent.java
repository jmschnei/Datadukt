package de.dfki.cwm.components.output;

import de.dfki.cwm.components.WorkflowComponent;
import de.dfki.cwm.data.Format;
import de.dfki.cwm.exceptions.WorkflowException;
import de.dfki.cwm.persistence.DataManager;

/**
 * @author julianmorenoschneider
 * @project CurationWorkflowManager
 * @date 07.02.2020
 * @company DFKI
 * @description This class represents the output component of a workflow, that based on the output Format decides what has 
 * 				to be done with the results of the workflow.
 * 
 */
public abstract class OutputComponent extends WorkflowComponent{

	/* (non-Javadoc)
	 * @see de.dfki.cwm.components.WorkflowComponent#executeComponent(java.lang.String, boolean, de.dfki.cwm.persistence.DataManager, java.lang.String, java.lang.String, boolean, boolean)
	 */
	@Override
	public abstract String executeComponent(String content, boolean priority, DataManager manager, String outputCallback, String statusCallback, boolean persist, boolean isContent) throws WorkflowException;

	/**
	 * @description This method creates an OutputComponent element based on the input parameters (Output format).
	 * @param outputFormat Format that the result has to be converted in.
	 * @return A component that can convert the result into the needed format.
	 * @throws Exception
	 */
	public static OutputComponent defineOutput(String sOutputFormat) throws Exception {
		Format outputFormat = Format.getFormat(sOutputFormat);
		return defineOutput(outputFormat);
	}

	/**
	 * @description This method creates an OutputComponent element based on the input parameters (Output format).
	 * @param outputFormat Format that the result has to be converted in.
	 * @return A component that can convert the result into the needed format.
	 * @throws Exception
	 */
	public static OutputComponent defineOutput(Format outputFormat) throws Exception {
		switch(outputFormat) {
//		case TEXT:
//			return new ConversionOutputComponent(outputFormat);
		case RDF:
			return new ConversionOutputComponent(outputFormat);
		case TURTLE:
			return new ConversionOutputComponent(outputFormat);
		case JSONLD:
			return new ConversionOutputComponent(outputFormat);
//		case RDFXML:
//			return new XMLOutputComponent();
		case URI:
			return new URIOutputComponent();
		case ALEPH:
			return new AlephOutputComponent();
//		case JSON:
//			return new ConversionOutputComponent(outputFormat);
		default:
			throw new Exception("OutputFormat not supported. Value must be: TEXT, RDF, JSONLD, XML.");
		}
	}

}
