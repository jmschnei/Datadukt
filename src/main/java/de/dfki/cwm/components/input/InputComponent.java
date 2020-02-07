package de.dfki.cwm.components.input;

import de.dfki.cwm.components.WorkflowComponent;
import de.dfki.cwm.data.Format;
import de.dfki.cwm.data.Language;
import de.dfki.cwm.exceptions.WorkflowException;
import de.dfki.cwm.persistence.DataManager;

/**
 * @author julianmorenoschneider
 * @project CurationWorkflowManager
 * @date 07.02.2020
 * @company DFKI
 * @description This class represents the input component of a workflow, that based on the input Format decides what has 
 * 				to be done with the input information.
 * 
 */
public abstract class InputComponent extends WorkflowComponent{

	/* (non-Javadoc)
	 * @see de.dfki.cwm.components.WorkflowComponent#executeComponent(java.lang.String, boolean, de.dfki.cwm.persistence.DataManager, java.lang.String, java.lang.String, boolean, boolean)
	 */
	@Override
	public abstract String executeComponent(String content, boolean priority, DataManager manager, String outputCallback, String statusCallback, boolean persist, boolean isContent) throws WorkflowException;

	/**
	 * @description This method creates an InputComponent element based on the input parameters.
	 * @param inFormat
	 * @param language
	 * @param inputPersist
	 * @param inputContent
	 * @return
	 * @throws Exception
	 */
	public static InputComponent defineInput(String sInputFormat, String sLanguage, boolean inputPersist, boolean inputContent) throws Exception {
		Format inFormat = Format.valueOf(sInputFormat);
		Language language = Language.getLanguage(sLanguage);
		return defineInput(inFormat, language, inputPersist, inputContent);
	}

	/**
	 * @description This method creates an InputComponent element based on the input parameters.
	 * @param inFormat
	 * @param language
	 * @param inputPersist
	 * @param inputContent
	 * @return
	 * @throws Exception
	 */
	public static InputComponent defineInput(Format inFormat, Language language, boolean inputPersist, boolean inputContent) throws Exception {
		switch (inFormat) {
		case TEXT:
			if(inputContent) {
				return new ConversionInputComponent(inFormat, language);
			}
			else {
				return new EmptyInputComponent(inFormat);
			}
		case RDF:
			return new ConversionInputComponent(inFormat, language);
		case JSON:
			return new ConversionInputComponent(inFormat, language);
		case JSONLD:
			return new ConversionInputComponent(inFormat, language);
		case RDFXML:
			return new ConversionInputComponent(inFormat, language);
		case URI:
			return new EmptyInputComponent(inFormat);
		default:
			throw new Exception("InputFormat not supported. Value must be: JSON, RDF, JSONLD, RDFXML or URI.");
		}
	}
}
