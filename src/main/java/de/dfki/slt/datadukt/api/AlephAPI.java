package de.dfki.slt.datadukt.api;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.dfki.slt.datadukt.engine.CWMEngine;
import de.dfki.slt.datadukt.exceptions.WorkflowException;


/**
 * @author Julian Moreno Schneider julian.moreno_schneider@dfki.de
 * @modified_by 
 * @project CurationWorkflowManager
 * @date 22.04.2020
 * @date_modified 
 * @company DFKI
 * @description Class that manages the endpoints for the WorkflowExecution elements: creation, deletion, execution for the Aleph integration
 *
 */
@RestController
@RequestMapping("/wfmanager")
public class AlephAPI{

	Logger logger = Logger.getLogger(AlephAPI.class);

	@Autowired
	CWMEngine service;

	@RequestMapping(value = "/sanityCheck", method = {RequestMethod.GET, RequestMethod.POST})
	public ResponseEntity<String> sanityCheck(HttpServletRequest request,
			@RequestBody(required = false) String postBody) throws Exception {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/plain");
		ResponseEntity<String> response = new ResponseEntity<String>("Aleph Webcontroller is working properly.", responseHeaders, HttpStatus.OK);
		return response;
	}

	@RequestMapping(value = "/{workflowId}/processDocument", method = {RequestMethod.POST })
	public ResponseEntity<String> listWorkflowExecutions(HttpServletRequest request,
			@PathVariable(name = "workflowId") String workflowExecutionId,
			//@RequestParam(value="workflowExecutionId", required=false) String workflowExecutionId,
			@RequestParam(value = "priority", required = false) boolean priority,
			@RequestParam(value = "synchronous", required = false) boolean synchronous,
			@RequestHeader(value = "Accept", required = false) String acceptHeader,
			@RequestHeader(value = "Content-Type", required = false) String contentTypeHeader,
			@RequestBody(required = false) String postBody) throws Exception {
		
		if(workflowExecutionId == null || workflowExecutionId.equalsIgnoreCase("")) {
			String msg = "Unspecified workflowExecutionId.";
			logger.error(msg);
			throw new WorkflowException(msg);
		}
		if(!contentTypeHeader.equalsIgnoreCase("application/json")) {
			String msg = "Content-Type must be 'application/json'. No other type is supported.";
			logger.error(msg);
			throw new WorkflowException(msg);
		}
		if(postBody == null || postBody.equalsIgnoreCase("")) {
			String msg = "Unspecified document content. POST body cannot be empty, it must contain a JSON object with a 'text' element.";
			logger.error(msg);
			throw new WorkflowException(msg);
		}
		JSONObject json = new JSONObject(postBody);
		HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "application/json");
		HttpStatus httpStatus = null;
		String responseString = null;
		try {
//			QuratorDocument qd = new QuratorDocument(json.getString("text"));
//			String content = QuratorSerialization.toRDF(qd, "TURTLE");
			String content = json.getString("text");
			Object result = service.executeWorkflow(content,workflowExecutionId, priority);
			if(result!=null){
				if(synchronous) {
		        	String resultQD = service.getWorkflowExecutionOutput(workflowExecutionId,contentTypeHeader,true);
		        	JSONObject jsonOutput = new JSONObject();
		        	jsonOutput.put("annotatedDocument", resultQD);
		        	responseString = jsonOutput.toString();
		        	responseString = resultQD;
					httpStatus=HttpStatus.OK;
				}
				else {
		        	JSONObject jsonOutput = new JSONObject();
		        	jsonOutput.put("msg", "The workflow is being executed.");
		        	jsonOutput.put("output_callback", "/"+workflowExecutionId+"/"+result+"/getOutput");
					responseString = jsonOutput.toString();
					httpStatus=HttpStatus.ACCEPTED;
				}
			}
			else{
	        	JSONObject jsonOutput = new JSONObject();
	        	jsonOutput.put("msg", "ERROR: The workflow has NOT been executed correctly.");
				responseString = jsonOutput.toString();
				httpStatus=HttpStatus.INTERNAL_SERVER_ERROR;
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			//throw e;
			e.printStackTrace();
        	JSONObject jsonOutput = new JSONObject();
        	jsonOutput.put("msg", e.getMessage());
			responseString = jsonOutput.toString();
			httpStatus=HttpStatus.INTERNAL_SERVER_ERROR;
		}
		ResponseEntity<String> response = new ResponseEntity<String>(responseString, responseHeaders, httpStatus);
		return response;
	}

	@RequestMapping(value = "/{workflowId}/{documentId}/getOutput", method = {RequestMethod.GET})
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


