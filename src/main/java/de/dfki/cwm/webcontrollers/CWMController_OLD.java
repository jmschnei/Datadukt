package de.dfki.cwm.webcontrollers;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.dfki.cwm.engine.CWMEngine;

@RestController
@RequestMapping("/cwm")
public class CWMController_OLD{

	Logger logger = Logger.getLogger(CWMController_OLD.class);

/**
	@RequestMapping(value = "/workflowmanager", method = {RequestMethod.DELETE })
	public ResponseEntity<String> deleteWorkflow(
			HttpServletRequest request,
			@RequestParam(value="workflowId") String workflowId,
			@RequestBody(required = false) String postBody) throws Exception {

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/plain");
		String responseString = "";
		HttpStatus httpStatus;
		try {
			boolean result = service.deleteWorkflow(workflowId);
			if(result){
				responseString = "The workflow has been deleted correctly.";
				httpStatus = HttpStatus.OK;
			}
			else{
				responseString = "ERROR: The workflow has NOT been deleted.";
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

	@RequestMapping(value = "/workflowmanager/{workflowId}", method = {RequestMethod.PUT })
	public ResponseEntity<String> defineWorkflowPUT(
			HttpServletRequest request,
			@PathVariable String workflowId,
			@RequestHeader(value = "Accept", required = false) String acceptHeader,
			@RequestHeader(value = "Content-Type", required = false) String contentTypeHeader,
			@RequestBody(required = false) String postBody) throws Exception {

		if(postBody == null || postBody.equalsIgnoreCase("")) {
			String msg = "Unspecified workflow definition.";
			throw new Exception(msg);
		}
		JSONObject json;
		try{
			json = service.defineWorkflow(workflowId, postBody);
		}
		catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		//		String responseString = "The workflow ["+json.getString("workflowId")+"] has been correctly defined and stored in the system.";
		HttpHeaders responseHeaders = new HttpHeaders();
		//		responseHeaders.add("Content-Type", "text/plain");
		//		ResponseEntity<String> response = new ResponseEntity<String>(responseString, responseHeaders, HttpStatus.OK);
		responseHeaders.add("Content-Type", "application/json");
		ResponseEntity<String> response = new ResponseEntity<String>(json.toString(), responseHeaders, HttpStatus.OK);
		return response;
	}

	@RequestMapping(value = "/workflowmanager", method = {RequestMethod.POST})
	public ResponseEntity<String> defineWorkflowPOST(
			HttpServletRequest request,
			@RequestParam(value = "workflowId", required = false) String workflowId,
			@RequestHeader(value = "Accept", required = false) String acceptHeader,
			@RequestHeader(value = "Content-Type", required = false) String contentTypeHeader,
			@RequestBody(required = false) String postBody) throws Exception {

		if(postBody == null || postBody.equalsIgnoreCase("")) {
			String msg = "Unspecified workflow definition.";
			throw new Exception(msg);
		}
		JSONObject json;
		try{
			json = service.defineWorkflow(workflowId, postBody);
		}
		catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		//		String responseString = "The workflow ["+json.getString("workflowId")+"] has been correctly defined and stored in the system.";
		HttpHeaders responseHeaders = new HttpHeaders();
		//		responseHeaders.add("Content-Type", "text/plain");
		//		ResponseEntity<String> response = new ResponseEntity<String>(responseString, responseHeaders, HttpStatus.OK);
		responseHeaders.add("Content-Type", "application/json");
		ResponseEntity<String> response = new ResponseEntity<String>(json.toString(), responseHeaders, HttpStatus.OK);
		return response;
	}

	@RequestMapping(value = "/workflowmanager/services", method = {RequestMethod.GET })
	public ResponseEntity<String> listServices(
			HttpServletRequest request,
			@RequestBody(required = false) String postBody) throws Exception {
		JSONObject json = service.listServices();
		String responseString = json.toString();
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/plain");
		ResponseEntity<String> response = new ResponseEntity<String>(responseString, responseHeaders, HttpStatus.OK);
		return response;
	}

	@RequestMapping(value = "/workflowmanager/{serviceId}", method = {RequestMethod.GET })
	public ResponseEntity<String> listService(
			HttpServletRequest request,
			@PathVariable String serviceId,
			@RequestBody(required = false) String postBody) throws Exception {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/plain");
		String responseString = "";
		HttpStatus httpStatus;
		try {
			JSONObject json = service.listService(serviceId);
			responseString = json.toString();
			httpStatus = HttpStatus.OK;
		}
		catch(Exception e) {
			responseString = e.getMessage();
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		ResponseEntity<String> response = new ResponseEntity<String>(responseString, responseHeaders, httpStatus);
		return response;
	}

	@RequestMapping(value = "/workflowmanager/services", method = {RequestMethod.DELETE })
	public ResponseEntity<String> deleteService(
			HttpServletRequest request,
			@RequestParam(value="serviceId") String serviceId,
			@RequestBody(required = false) String postBody) throws Exception {

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/plain");
		String responseString = "";
		HttpStatus httpStatus;
		try {
			boolean result = service.deleteService(serviceId);
			if(result){
				responseString = "The workflow has been deleted correctly.";
				httpStatus = HttpStatus.OK;
			}
			else{
				responseString = "ERROR: The workflow has NOT been deleted.";
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

	@RequestMapping(value = "/workflowmanager", method = {RequestMethod.POST})
	public ResponseEntity<String> defineServicePOST(
			HttpServletRequest request,
			@RequestParam(value = "serviceId", required = false) String serviceId,
			@RequestHeader(value = "Accept", required = false) String acceptHeader,
			@RequestHeader(value = "Content-Type", required = false) String contentTypeHeader,
			@RequestBody(required = false) String postBody) throws Exception {

		if(postBody == null || postBody.equalsIgnoreCase("")) {
			String msg = "Unspecified workflow definition.";
			throw new Exception(msg);
		}
		JSONObject json;
		try{
			json = service.defineService(serviceId, postBody);
		}
		catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		//		String responseString = "The workflow ["+json.getString("workflowId")+"] has been correctly defined and stored in the system.";
		HttpHeaders responseHeaders = new HttpHeaders();
		//		responseHeaders.add("Content-Type", "text/plain");
		//		ResponseEntity<String> response = new ResponseEntity<String>(responseString, responseHeaders, HttpStatus.OK);
		responseHeaders.add("Content-Type", "application/json");
		ResponseEntity<String> response = new ResponseEntity<String>(json.toString(), responseHeaders, HttpStatus.OK);
		return response;
	}
*/

}


