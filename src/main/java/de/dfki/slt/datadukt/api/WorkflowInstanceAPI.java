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
import de.dfki.slt.datadukt.storage.FileStorage;

/**
 * @author julianmorenoschneider
 * @project CurationWorkflowManager
 * @date 20.04.2020
 * @date_modified 15.04.2022
 * @company DFKI
 * @description Class that manages the endpoints for the WorkflowInstance elements: creation, deletion, execution.
 *
 */
@RestController
@RequestMapping("/datadukt/workflowinstances")
public class WorkflowInstanceAPI{

	Logger logger = Logger.getLogger(WorkflowInstanceAPI.class);

	@Autowired
	CWMEngine engine;
	
	@Autowired
	FileStorage fileStorage;

	@RequestMapping(method = {RequestMethod.GET })
	public ResponseEntity<String> listWorkflowInstances(HttpServletRequest request,
			@RequestParam(value="workflowIntanceId", required=false) String workflowIntanceId,
			@RequestBody(required = false) String postBody) throws Exception {
		JSONArray json = engine.listWorkflowInstances(workflowIntanceId);
		String responseString = json.toString();
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "application/json");
		ResponseEntity<String> response = new ResponseEntity<String>(responseString, responseHeaders, HttpStatus.OK);
		return response;
	}

	@RequestMapping(method = {RequestMethod.POST})
	public ResponseEntity<String> createWorkflowInstances(
			HttpServletRequest request,
			@RequestHeader(value = "Accept", required = false) String acceptHeader,
			@RequestHeader(value = "Content-Type", required = false) String contentTypeHeader,
			@RequestParam(value="workflowIntanceId", required=false) String workflowIntanceId,
			@RequestBody(required = false) String postBody) throws Exception {
		if(postBody == null || postBody.equalsIgnoreCase("")) {
			String msg = "Unspecified workflow template definition.";
			throw new Exception(msg);
		}
		try{
			workflowIntanceId = engine.createWorkflowInstance(postBody, workflowIntanceId);
		}
		catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/plain");
		ResponseEntity<String> response = new ResponseEntity<String>(workflowIntanceId, responseHeaders, HttpStatus.OK);
		return response;
	}

	@RequestMapping(method = {RequestMethod.DELETE })
	public ResponseEntity<String> deleteWorkflowInstance(HttpServletRequest request,
			@RequestParam(value="workflowIntanceId") String workflowIntanceId,
			@RequestBody(required = false) String postBody) throws Exception {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/plain");
		String responseString = "";
		HttpStatus httpStatus;
		try {
			boolean result = engine.deleteWorkflowInstance(workflowIntanceId);
			if(result){
				responseString = "The workflow execution has been deleted correctly.";
				httpStatus = HttpStatus.OK;
			}
			else{
				responseString = "ERROR: The workflow execution has NOT been deleted.";
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

	private ResponseEntity<String> badParameter(String msg) {
		HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "application/json");
		String responseString = "{\"response\":\"error\",\"message\":\""+msg+"\"}";
		ResponseEntity<String> response = new ResponseEntity<String>(responseString, responseHeaders, HttpStatus.BAD_REQUEST);
		return response;
	}

}


