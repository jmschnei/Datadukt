package de.dfki.cwm.webcontrollers;

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

import de.dfki.cwm.engine.CWMEngine;
import de.dfki.cwm.exceptions.WorkflowException;

@RestController
@RequestMapping("/cwm/workflowexecutions")
public class WorkflowExecutionWebcontroller{

	Logger logger = Logger.getLogger(WorkflowExecutionWebcontroller.class);

	@Autowired
	CWMEngine service;

	@RequestMapping(method = {RequestMethod.GET })
	public ResponseEntity<String> listWorkflowExecutions(HttpServletRequest request,
			@RequestParam(value="workflowExecutionId", required=false) String workflowExecutionId,
			@RequestBody(required = false) String postBody) throws Exception {
		JSONArray json = service.listWorkflowExecutions(workflowExecutionId);
		String responseString = json.toString();
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "application/json");
		ResponseEntity<String> response = new ResponseEntity<String>(responseString, responseHeaders, HttpStatus.OK);
		return response;
	}

//	@RequestMapping(value = "/workflowmanager/workflowexecutions", method = {RequestMethod.POST})
	@RequestMapping(method = {RequestMethod.POST})
	public ResponseEntity<String> createWorkflowExecutions(
			HttpServletRequest request,
			@RequestHeader(value = "Accept", required = false) String acceptHeader,
			@RequestHeader(value = "Content-Type", required = false) String contentTypeHeader,
			@RequestParam(value="workflowExecutionId", required=false) String workflowExecutionId,
			@RequestBody(required = false) String postBody) throws Exception {
		if(postBody == null || postBody.equalsIgnoreCase("")) {
			String msg = "Unspecified workflow template definition.";
			throw new Exception(msg);
		}
		try{
			workflowExecutionId = service.createWorkflowExecution(postBody, workflowExecutionId);
		}
		catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/plain");
		ResponseEntity<String> response = new ResponseEntity<String>(workflowExecutionId, responseHeaders, HttpStatus.OK);
		return response;
	}

	@RequestMapping(method = {RequestMethod.DELETE })
	public ResponseEntity<String> deleteWorkflowExecution(HttpServletRequest request,
			@RequestParam(value="workflowExecutionId") String workflowExecutionId,
			@RequestBody(required = false) String postBody) throws Exception {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/plain");
		String responseString = "";
		HttpStatus httpStatus;
		try {
			boolean result = service.deleteWorkflowExecution(workflowExecutionId);
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

//	@RequestMapping(value = "/workflowmanager/executeWorkflow", method = {RequestMethod.POST})
//	public ResponseEntity<String> executeWorkflow(
//			HttpServletRequest request,
////			@RequestParam(value = "workflowExecutionId", required = false) String workflowExecutionId,
//			@RequestParam(value = "workflowTemplateId", required = false) String workflowTemplateId,
//			@RequestParam(value = "statusCallback", required = false) String statusCallback,
//			@RequestParam(value = "outputCallback", required = false) String outputCallback,
//			@RequestParam(value = "outputFormat", required = false) String outputFormat,
//			@RequestParam(value = "inputFormat", required = false) String inputFormat,
//			@RequestParam(value = "inputLanguage", required = false) String inputLanguage,
//			@RequestParam(value = "priority", required = false) boolean priority,
//			@RequestParam(value = "persist", required = false) boolean persist,
//			@RequestParam(value = "isContent", required = false) boolean isContent,
//			@RequestParam(value = "parameters", required = false) String parameters,
//			@RequestBody(required = false) String postBody) throws Exception {
//		String workflowExecutionId = workflowTemplateId+"_"+(new Date()).getTime();
//		try {
//			JSONObject json = new JSONObject();
//			json.put("workflowExecutionName",workflowExecutionId);
//			json.put("workflowExecutionId",workflowExecutionId);
//			json.put("workflowTemplateId", workflowTemplateId);
//			json.put("priority", priority);
//			json.put("statusCallback", statusCallback);
//			json.put("outputCallback",outputCallback);
//			json.put("output", outputFormat);
//			json.put("input", inputFormat);
//			json.put("language", inputLanguage);
//			json.put("persist", persist);
//			json.put("isContent", isContent);
//			if(parameters!=null && !parameters.equalsIgnoreCase("")) {
//				JSONObject parametersJSON = new JSONObject(parameters);
//				json.put("parameters", parametersJSON);
//			}
//			workflowExecutionId = service.createWorkflowExecution(json, workflowExecutionId);
//		}
//		catch(Exception e) {
//			e.printStackTrace();
//			throw new Exception ("Error creating the WorkflowExecution");
//		}
//		
//		System.out.println("(((((((((((((((((((((-----))))))))))))))))))))))");		
//		System.out.println("(((((((((((((((((((((-----))))))))))))))))))))))");		
//		System.out.println("(((((((((((((((((((((-----))))))))))))))))))))))");		
//		
//
//		HttpStatus httpStatus = HttpStatus.ACCEPTED;
//		String responseString = workflowExecutionId;
//		try {
//			Object result = service.executeWorkflow(postBody, workflowExecutionId, priority);
//			if(result==null){
//				responseString = "ERROR: The workflow has NOT been executed correctly.";
//				httpStatus=HttpStatus.INTERNAL_SERVER_ERROR;
//			}
//		}
//		catch(Exception e) {
//			e.printStackTrace();
//			throw new Exception ("Error creating the WorkflowExecution");
//		}
//		HttpHeaders responseHeaders = new HttpHeaders();
//		responseHeaders.add("Content-Type", "text/plain");
//		ResponseEntity<String> response = new ResponseEntity<String>(responseString, responseHeaders, httpStatus);
//		return response;
//	}

	@RequestMapping(value = "/execute", method = {RequestMethod.GET})
	public ResponseEntity<String> executeWorkflowExecution(
			HttpServletRequest request,
			@RequestParam(value = "workflowExecutionId", required = false) String workflowExecutionId,
			@RequestParam(value = "priority", required = false) boolean priority,
			@RequestBody(required = false) String postBody) throws Exception {
		if(workflowExecutionId == null || workflowExecutionId.equalsIgnoreCase("")) {
			String msg = "Unspecified workflowExecutionId.";
			logger.error(msg);
			throw new WorkflowException(msg);
		}
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/plain");
		HttpStatus httpStatus = null;
		String responseString = null;
		try {
			Object result = service.executeWorkflow(workflowExecutionId, priority);
			if(result!=null){
				responseString = "The workflow is being executed.";
				httpStatus=HttpStatus.ACCEPTED;
			}
			else{
				responseString = "ERROR: The workflow has NOT been executed correctly.";
				httpStatus=HttpStatus.INTERNAL_SERVER_ERROR;
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			//throw e;
			//e.printStackTrace();
			responseString = e.getMessage();
			httpStatus=HttpStatus.INTERNAL_SERVER_ERROR;
		}
		ResponseEntity<String> response = new ResponseEntity<String>(responseString, responseHeaders, httpStatus);
		return response;
	}

	@RequestMapping(value = "/getOutput", method = {RequestMethod.GET})
	public ResponseEntity<String> getOutput(
			HttpServletRequest request,
			@RequestHeader(value = "Accept", required = false) String acceptHeader,
			@RequestHeader(value = "Content-Type", required = false) String contentTypeHeader,
			@RequestParam(value = "workflowExecutionId", required = false) String workflowExecutionId,
			@RequestParam(value = "keepWaiting", required = false) boolean keepWaiting,
            @RequestBody(required = false) String postBody) throws Exception {
		
		if (workflowExecutionId == null || workflowExecutionId.equalsIgnoreCase("")) {
			logger.error("Error: 'analysisId' input parameter can not be null or empty.");
			throw new Exception("Error: 'analysisId' input parameter can not be null or empty.");
		}
       	try {
        	String result = service.getWorkflowExecutionOutput(workflowExecutionId,contentTypeHeader,keepWaiting);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.add("Content-Type", "text/plain");
    		ResponseEntity<String> response = new ResponseEntity<String>(result, responseHeaders, HttpStatus.OK);
            return response;
        } catch (Exception e) {
        	logger.error(e.getMessage());
            throw e;
        }
    }

}


