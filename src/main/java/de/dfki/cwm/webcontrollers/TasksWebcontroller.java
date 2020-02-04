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

@RestController
@RequestMapping("/cwm/tasks")
public class TasksWebcontroller{

	Logger logger = Logger.getLogger(TasksWebcontroller.class);

	@Autowired
	CWMEngine service;

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


