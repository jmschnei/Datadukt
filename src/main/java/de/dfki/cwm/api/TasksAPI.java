package de.dfki.cwm.api;

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

/**
 * @author julianmorenoschneider
 * @project CurationWorkflowManager
 * @date 17.04.2020
 * @date_modified 
 * @company DFKI
 * @description Controller defining endpoints for the management of Task elements.
 *
 */
@RestController
@RequestMapping("/cwm/tasks")
public class TasksAPI{

	Logger logger = Logger.getLogger(TasksAPI.class);

	@Autowired
	CWMEngine service;

	/**
	 * Method to retrieve the list of existing Tasks in the CWM
	 * @param taskId If specified, only the information related to Task with taskId is listed
	 * @return A JSON object containing the available Tasks in the CWM
	 * @throws Exception
	 */
	@RequestMapping(method = {RequestMethod.GET })
	public ResponseEntity<String> listTasks(HttpServletRequest request,
			@RequestParam(value = "taskId", required = false) String taskId,
			@RequestBody(required = false) String postBody) throws Exception {
		JSONArray json = service.listTasks(taskId);
		String responseString = json.toString();
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "application/json");
		ResponseEntity<String> response = new ResponseEntity<String>(responseString, responseHeaders, HttpStatus.OK);
		return response;
	}

	/**
	 * Method for defining a new Task in the CWM.
	 * @param taskId Identification for the task (optional). If not provided, CWM will automatically generate one. 
	 * @param postBody JSON object containing the definition of the Task.
	 *        TODO Include json example.
	 * @return The identification of the Task (taskId).
	 * @throws Exception
	 */
	@RequestMapping(method = {RequestMethod.POST})
	public ResponseEntity<String> createTask(
			HttpServletRequest request,
			@RequestHeader(value = "Accept", required = false) String acceptHeader,
			@RequestHeader(value = "Content-Type", required = false) String contentTypeHeader,
			@RequestParam(value = "taskId", required = false) String taskId,
			@RequestBody(required = false) String postBody) throws Exception {
		if(postBody == null || postBody.equalsIgnoreCase("")) {
			String msg = "Unspecified Task definition.";
			throw new Exception(msg);
		}
		String responseString = "";
		HttpStatus httpStatus;
		try{
			taskId = service.createTask(postBody,taskId);
			responseString = taskId;
			httpStatus = HttpStatus.OK;
			logger.info("Task "+taskId+" correctly created.");
		}
 		catch(Exception e){
			responseString = e.getMessage();
			logger.error(responseString);
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			e.printStackTrace();
			throw e;
		}
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/plain");
		ResponseEntity<String> response = new ResponseEntity<String>(responseString, responseHeaders, httpStatus);
		return response;
	}

	/**
	 * Method that deletes a specific Task (defined by taskId).
	 * @param taskId Identification of the task to be deleted.
	 * @return Message mentioning if the task has been deleted or not.
	 * @throws Exception If taskId does not exist.
	 */
	@RequestMapping(method = {RequestMethod.DELETE })
	public ResponseEntity<String> deleteTask(HttpServletRequest request,
			@RequestParam(value="taskId", required = true) String taskId,
			@RequestBody(required = false) String postBody) throws Exception {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/plain");
		String responseString = "";
		HttpStatus httpStatus;
		try {
			boolean result = service.deleteTask(taskId);
			if(result){
				responseString = "The Task has been deleted correctly.";
				httpStatus = HttpStatus.OK;
			}
			else{
				responseString = "ERROR: The Task has NOT been deleted.";
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


