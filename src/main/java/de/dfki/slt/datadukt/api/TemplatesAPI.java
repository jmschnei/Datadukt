package de.dfki.slt.datadukt.api;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.dfki.slt.datadukt.engine.CWMEngine;

/**
 * @author julianmorenoschneider
 * @project Datadukt
 * @date 17.04.2020
 * @date_modified 14.07.2022
 * @company DFKI
 * @description RestController defining endpoints for the management of WorkflowTemplates elements.
 */
@RestController
@RequestMapping("/datadukt/templates")
public class TemplatesAPI{

	Logger logger = Logger.getLogger(TemplatesAPI.class);

	@Autowired
	CWMEngine service;

	/**
	 * Method to retrieve the list of existing Templates in Datadukt
	 * @param templateId If specified, only the information related to Template=templateId is listed
	 * @return A JSON object containing the available Templates in Datadukt
	 * @throws Exception
	 */
	@RequestMapping(method = {RequestMethod.GET })
	public ResponseEntity<String> listWorkflowTemplates(HttpServletRequest request,
			@RequestParam(value="workflowTemplateId", required=false) String workflowTemplateId,
			@RequestBody(required = false) String postBody) throws Exception {
		JSONArray json = service.listWorkflowTemplates(workflowTemplateId);
		String responseString = json.toString();
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "application/json");
		ResponseEntity<String> response = new ResponseEntity<String>(responseString, responseHeaders, HttpStatus.OK);
		return response;
	}

	/**
	 * Method to create a new Template in Datadukt
	 * @param templateId Identification for the template (optional). If not provided, Datadukt will automatically generate one. 
	 * @param postBody JSON object containing the definition of the Template
	 *        TODO Include json example
	 * @return The identification of the Template (templateId)
	 * @throws Exception
	 */
	@RequestMapping(method = {RequestMethod.POST})
	public ResponseEntity<String> createWorkflowTemplate(
			HttpServletRequest request,
			@RequestHeader(value = "Accept", required = false) String acceptHeader,
			@RequestHeader(value = "Content-Type", required = false) String contentTypeHeader,
			@RequestParam(value="workflowTemplateId", required=false) String workflowTemplateId,
			@RequestBody(required = false) String postBody) throws Exception {
		if(postBody == null || postBody.equalsIgnoreCase("")) {
			String msg = "Unspecified workflow template definition.";
			throw new Exception(msg);
		}
		try{
			workflowTemplateId = service.createWorkflowTemplate(postBody,workflowTemplateId);
		}
		catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/plain");
		ResponseEntity<String> response = new ResponseEntity<String>(workflowTemplateId, responseHeaders, HttpStatus.OK);
		return response;
	}

	/**
	 * Method that deletes a specific Template (defined by templateId).
	 * @param templateId Identification of the template to be deleted.
	 * @return Message mentioning if the template has been deleted or not.
	 * @throws Exception If templateId does not exist.
	 */
	@RequestMapping(method = {RequestMethod.DELETE })
	public ResponseEntity<String> deleteWorkflowTemplate(HttpServletRequest request,
			@RequestParam(value="workflowTemplateId") String workflowTemplateId,
			@RequestBody(required = false) String postBody) throws Exception {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/plain");
		String responseString = "";
		HttpStatus httpStatus;
		try {
			boolean result = service.deleteWorkflowTemplate(workflowTemplateId);
			System.out.println(result);
			if(result){
				responseString = "The workflow template has been deleted correctly.";
				httpStatus = HttpStatus.OK;
			}
			else{
				responseString = "ERROR: The workflow template has NOT been deleted.";
				httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			}
		}
		catch(Exception e) {
			responseString = e.getMessage();
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		ResponseEntity<String> response = new ResponseEntity<String>(responseString, responseHeaders, httpStatus);
		return response;
	}

}


