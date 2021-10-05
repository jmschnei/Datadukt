package de.dfki.cwm.api;

import java.io.InputStream;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
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
import org.springframework.web.multipart.MultipartFile;

import de.dfki.cwm.data.Format;
import de.dfki.cwm.data.documents.WMDocument;
import de.dfki.cwm.data.documents.conversion.WMDeserialization;
import de.dfki.cwm.data.documents.conversion.WMSerialization;
import de.dfki.cwm.engine.CWMEngine;
import de.dfki.cwm.exceptions.WorkflowException;
import de.dfki.cwm.storage.FileStorage;

/**
 * @author julianmorenoschneider
 * @project CurationWorkflowManager
 * @date 20.04.2020
 * @date_modified 
 * @company DFKI
 * @description Class that manages the endpoints for the WorkflowExecution elements: creation, deletion, execution.
 *
 */
@RestController
@RequestMapping("/cwm/workflowexecutions")
public class WorkflowExecutionAPI{

	Logger logger = Logger.getLogger(WorkflowExecutionAPI.class);

	@Autowired
	CWMEngine engine;
	
	@Autowired
	FileStorage fileStorage;

	@RequestMapping(method = {RequestMethod.GET })
	public ResponseEntity<String> listWorkflowExecutions(HttpServletRequest request,
			@RequestParam(value="workflowExecutionId", required=false) String workflowExecutionId,
			@RequestBody(required = false) String postBody) throws Exception {
		JSONArray json = engine.listWorkflowExecutions(workflowExecutionId);
		String responseString = json.toString();
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "application/json");
		ResponseEntity<String> response = new ResponseEntity<String>(responseString, responseHeaders, HttpStatus.OK);
		return response;
	}

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
			workflowExecutionId = engine.createWorkflowExecution(postBody, workflowExecutionId);
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
			boolean result = engine.deleteWorkflowExecution(workflowExecutionId);
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

	/**
	@RequestMapping(value = "/workflowmanager/executeWorkflow", method = {RequestMethod.POST})
	public ResponseEntity<String> executeWorkflow(
			HttpServletRequest request,
//			@RequestParam(value = "workflowExecutionId", required = false) String workflowExecutionId,
			@RequestParam(value = "workflowTemplateId", required = false) String workflowTemplateId,
			@RequestParam(value = "statusCallback", required = false) String statusCallback,
			@RequestParam(value = "outputCallback", required = false) String outputCallback,
			@RequestParam(value = "outputFormat", required = false) String outputFormat,
			@RequestParam(value = "inputFormat", required = false) String inputFormat,
			@RequestParam(value = "inputLanguage", required = false) String inputLanguage,
			@RequestParam(value = "priority", required = false) boolean priority,
			@RequestParam(value = "persist", required = false) boolean persist,
			@RequestParam(value = "isContent", required = false) boolean isContent,
			@RequestParam(value = "parameters", required = false) String parameters,
			@RequestBody(required = false) String postBody) throws Exception {
		String workflowExecutionId = workflowTemplateId+"_"+(new Date()).getTime();
		try {
			JSONObject json = new JSONObject();
			json.put("workflowExecutionName",workflowExecutionId);
			json.put("workflowExecutionId",workflowExecutionId);
			json.put("workflowTemplateId", workflowTemplateId);
			json.put("priority", priority);
			json.put("statusCallback", statusCallback);
			json.put("outputCallback",outputCallback);
			json.put("output", outputFormat);
			json.put("input", inputFormat);
			json.put("language", inputLanguage);
			json.put("persist", persist);
			json.put("isContent", isContent);
			if(parameters!=null && !parameters.equalsIgnoreCase("")) {
				JSONObject parametersJSON = new JSONObject(parameters);
				json.put("parameters", parametersJSON);
			}
			workflowExecutionId = service.createWorkflowExecution(json, workflowExecutionId);
		}
		catch(Exception e) {
			e.printStackTrace();
			throw new Exception ("Error creating the WorkflowExecution");
		}
		
		System.out.println("(((((((((((((((((((((-----))))))))))))))))))))))");		
		System.out.println("(((((((((((((((((((((-----))))))))))))))))))))))");		
		System.out.println("(((((((((((((((((((((-----))))))))))))))))))))))");		
		

		HttpStatus httpStatus = HttpStatus.ACCEPTED;
		String responseString = workflowExecutionId;
		try {
			Object result = service.executeWorkflow(postBody, workflowExecutionId, priority);
			if(result==null){
				responseString = "ERROR: The workflow has NOT been executed correctly.";
				httpStatus=HttpStatus.INTERNAL_SERVER_ERROR;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			throw new Exception ("Error creating the WorkflowExecution");
		}
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/plain");
		ResponseEntity<String> response = new ResponseEntity<String>(responseString, responseHeaders, httpStatus);
		return response;
	}
	*/

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
			Object result = engine.executeWorkflow(workflowExecutionId, priority);
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
        	String result = engine.getWorkflowExecutionOutput(workflowExecutionId,contentTypeHeader,keepWaiting);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.add("Content-Type", "text/plain");
    		ResponseEntity<String> response = new ResponseEntity<String>(result, responseHeaders, HttpStatus.OK);
            return response;
        } catch (Exception e) {
        	logger.error(e.getMessage());
            throw e;
        }
    }

	@RequestMapping(value = "/{workflowId}/processDocument", method = {RequestMethod.POST })
	public ResponseEntity<String> processDocument(HttpServletRequest request,
			@PathVariable(name = "workflowId") String workflowExecutionId,
			//@RequestParam(value="workflowExecutionId", required=false) String workflowExecutionId,
			@RequestParam(value = "priority", required = false) boolean priority,
			@RequestParam(value = "synchronous", required = false) boolean synchronous,
//			@RequestParam(value = "inputSemanticFormat", required = false) String inputSemanticFormat,
//			@RequestParam(value = "outputSemanticFormat", required = false) String outputSemanticFormat,
			@RequestHeader(value = "Accept", required = false) String acceptHeader,
			@RequestHeader(value = "Content-Type", required = false) String contentTypeHeader,
			@RequestParam(value = "file", required=false) MultipartFile file,
			@RequestParam(value = "request", required=false) String sRequest,
			@RequestParam(value = "content", required=false) String sContent
//			@RequestBody(required = false) String body
			) throws Exception {

		if(sRequest==null || sRequest.equalsIgnoreCase("")) {
			String msg = "Unspecified 'request' JSON parameter.";
			logger.error(msg);
			return badParameter(msg);
		}
		JSONObject json = new JSONObject(sRequest);

		Format iFormat = null; 
		Format iSemanticFormat = null; 
		Format oFormat = null; 
		Format oSemanticFormat = null;
		String inputSemanticFormat = json.getString("inputSemanticFormat");
		String outputSemanticFormat = json.getString("outputSemanticFormat");
		String contentPlace = json.getString("content");
		try {
			if(workflowExecutionId == null || workflowExecutionId.equalsIgnoreCase("")) {
				String msg = "Unspecified workflowExecutionId.";
				logger.error(msg);
				return badParameter(msg);
			}
//			if(contentTypeHeader==null || contentTypeHeader.equalsIgnoreCase("")) {
//				String msg = "Content-Type must be established.";
//				logger.error(msg);
//				return badParameter(msg);
//			}
//			else {
//				iFormat = Format.getFormat(contentTypeHeader);
//			}
			if(inputSemanticFormat==null || inputSemanticFormat.equalsIgnoreCase("")) {
				String msg = "inputSemanticFormat must be established.";
				logger.error(msg);
				return badParameter(msg);
			}
			else {
				iSemanticFormat = Format.getFormat(inputSemanticFormat);
			}
			if(acceptHeader==null || acceptHeader.equalsIgnoreCase("")) {
				String msg = "Accept Header must be established.";
				logger.error(msg);
				return badParameter(msg);
			}
			else {
				oFormat = Format.getFormat(acceptHeader);
			}
			if(outputSemanticFormat==null || outputSemanticFormat.equalsIgnoreCase("")) {
				String msg = "outputSemanticFormat parameter must be established.";
				logger.error(msg);
				return badParameter(msg);
			}
			else {
				oSemanticFormat = Format.getFormat(outputSemanticFormat);
			}
		}
		catch(Exception e) {
			return badParameter(e.getMessage());
		}
		String content = "";
//		if(iFormat==Format.MULTIPART) {
//			content = fileStorage.generateTemporaryFile(file, iFormat);
//		}
//		else if(iSemanticFormat.isMultimedia()) {
		if(iSemanticFormat.isMultimedia()) {
			InputStream is = file.getInputStream();
			content = fileStorage.generateTemporaryFile(is, iFormat);
		}
		else {
			if(contentPlace.equalsIgnoreCase("text")) {
				content = sContent;
			}
			else if (contentPlace.equalsIgnoreCase("file")){
				StringWriter writer = new StringWriter();
				IOUtils.copy(file.getInputStream(), writer, "utf-8");
				content = writer.toString();
				if(content.length()==0) {
					return badParameter("Content must be provided in 'file' parameter. It can not be empty.");
				}
			}
			else {
				return badParameter("Content must be provided in body or in 'file' parameter. It can not be empty.");
			}
		}
		HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", oFormat.toString());
		HttpStatus httpStatus = null;
		String responseString = null;
		try {
			WMDocument wmd = WMDeserialization.workflowManagerDocumentFromFormat(content,iSemanticFormat.toString(),null);
//			System.out.println(wmd.toRDF("TURTLE"));
			Object result = engine.executeWorkflow(wmd,workflowExecutionId, priority);
			if(result!=null){
				if(synchronous) {
		        	String resultQD = engine.getWorkflowExecutionOutput(workflowExecutionId,contentTypeHeader,true);		        	
					// Convert output.
		        	responseString = WMSerialization.toFormat(resultQD,oFormat.toString(),oSemanticFormat.toString());
			        responseHeaders.add("Content-Type", oFormat.toString());
					httpStatus=HttpStatus.OK;
				}
				else {
					System.out.println(result);
					String sResult = (String) result;
					String weId = sResult.substring(sResult.indexOf(' ')+1);
					System.out.println(weId);
		        	JSONObject jsonOutput = new JSONObject();
		        	jsonOutput.put("response", "success");
		        	jsonOutput.put("msg", "The workflow is being executed.");
//		        	jsonOutput.put("workflowId", workflowExecutionId);
//		        	jsonOutput.put("output_callback", "/"+workflowExecutionId+"/getOutput");
		        	jsonOutput.put("workflowId", weId);
		        	jsonOutput.put("output_callback", "/"+weId+"/getOutput");
					responseString = jsonOutput.toString();
			        responseHeaders.add("Content-Type", "application/json");
					httpStatus=HttpStatus.ACCEPTED;
				}
			}
			else{
	        	JSONObject jsonOutput = new JSONObject();
	        	jsonOutput.put("response", "error");
	        	jsonOutput.put("msg", "The workflow has NOT been executed correctly.");
				responseString = jsonOutput.toString();
		        responseHeaders.add("Content-Type", "application/json");
				httpStatus=HttpStatus.INTERNAL_SERVER_ERROR;
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			//throw e;
			e.printStackTrace();
        	JSONObject jsonOutput = new JSONObject();
        	jsonOutput.put("response", "error");
        	jsonOutput.put("msg", e.getMessage());
			responseString = jsonOutput.toString();
			httpStatus=HttpStatus.INTERNAL_SERVER_ERROR;
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

	@RequestMapping(value = "/{workflowId}/getOutput", method = {RequestMethod.GET})
	public ResponseEntity<String> getDocumentOutput(
			HttpServletRequest request,
			@RequestHeader(value = "Accept", required = false) String acceptHeader,
			@RequestHeader(value = "Content-Type", required = false) String contentTypeHeader,
			@PathVariable(name = "workflowId") String workflowExecutionId,
			@RequestParam(value = "keepWaiting", required = false) boolean keepWaiting,
            @RequestBody(required = false) String postBody) throws Exception {
		
		if (workflowExecutionId == null || workflowExecutionId.equalsIgnoreCase("")) {
			logger.error("Error: 'analysisId' input parameter can not be null or empty.");
			throw new Exception("Error: 'analysisId' input parameter can not be null or empty.");
		}
       	try {
        	String result = engine.getWorkflowExecutionOutput(workflowExecutionId,contentTypeHeader,keepWaiting);
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


