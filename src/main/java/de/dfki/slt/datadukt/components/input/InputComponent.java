package de.dfki.slt.datadukt.components.input;

import de.dfki.slt.datadukt.components.WorkflowComponent;
import de.dfki.slt.datadukt.data.Format;
import de.dfki.slt.datadukt.data.Language;
import de.dfki.slt.datadukt.exceptions.WorkflowException;
import de.dfki.slt.datadukt.persistence.DataManager;

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
		Format inFormat = Format.getFormat(sInputFormat);
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
		System.out.println("DEBUG: Defining input component for format "+inFormat);
		switch (inFormat) {
		case TEXT:
			if(inputContent) {
				return new ConversionInputComponent(inFormat, language);
			}
			else {
				return new EmptyInputComponent(inFormat);
			}
		case RDF:
			return new EmptyInputComponent(inFormat);
		case JSON:
		case JSONLD:
		case RDFXML:
		case WAV:
		case MP3:
			return new ConversionInputComponent(inFormat, language);
		case URI:
		case NONE:
			return new EmptyInputComponent(inFormat);
		default:
			throw new Exception("InputFormat not supported. Value must be: JSON, RDF, JSONLD, RDFXML or URI.");
		}
	}
}
