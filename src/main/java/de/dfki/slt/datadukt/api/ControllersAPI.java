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
 * @description RestController defining endpoints for the management of Controller elements.
 *
 */
@RestController
@RequestMapping("/datadukt/controllers")
public class ControllersAPI{

	Logger logger = Logger.getLogger(ControllersAPI.class);

	@Autowired
	CWMEngine service;

	/**
	 * Method to retrieve the list of existing Controllers in Datadukt
	 * @param controllerId If specified, only the information related to Controller=controllerId is listed
	 * @return A JSON object containing the available Controllers in Datadukt
	 * @throws Exception
	 */
	@RequestMapping(method = {RequestMethod.GET })
	public ResponseEntity<String> listControllers(HttpServletRequest request,
			@RequestParam(value="controllerId", required=false) String controllerId,
			@RequestBody(required = false) String postBody) throws Exception {
		JSONArray json = service.listControllers(controllerId);
		String responseString = json.toString();
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "application/json");
		ResponseEntity<String> response = new ResponseEntity<String>(responseString, responseHeaders, HttpStatus.OK);
		return response;
	}

	/**
	 * Method to create a new Controller in Datadukt
	 * @param controllerId Identification for the controller (optional). If not provided, Datadukt will automatically generate one. 
	 * @param postBody JSON object containing the definition of the Controller
	 *        TODO Include json example
	 * @return The identification of the Controller (controllerId)
	 * @throws Exception
	 */
	@RequestMapping(method = {RequestMethod.POST})
	public ResponseEntity<String> createController(
			HttpServletRequest request,
			@RequestHeader(value = "Accept", required = false) String acceptHeader,
			@RequestHeader(value = "Content-Type", required = false) String contentTypeHeader,
			@RequestParam(value="controllerId", required=false) String controllerId,
			@RequestBody(required = false) String postBody) throws Exception {
		if(postBody == null || postBody.equalsIgnoreCase("")) {
			String msg = "Unspecified workflow template definition.";
			throw new Exception(msg);
		}
		String responseString = "";
		HttpStatus httpStatus;
		try{
		}
		catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		try{
			controllerId = service.createController(postBody, controllerId);
			responseString = controllerId;
			httpStatus = HttpStatus.OK;
			logger.info("Controller "+controllerId+" correctly created.");
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
	 * Method that deletes a specific Çontroller (defined by controllerId).
	 * @param controllerId Identification of the controller to be deleted.
	 * @return Message mentioning if the controller has been deleted or not.
	 * @throws Exception If controllerId does not exist.
	 */	
	@RequestMapping(method = {RequestMethod.DELETE })
	public ResponseEntity<String> deleteController(HttpServletRequest request,
			@RequestParam(value="controllerId", required = true) String controllerId,
			@RequestBody(required = false) String postBody) throws Exception {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/plain");
		String responseString = "";
		HttpStatus httpStatus;
		try {
			boolean result = service.deleteController(controllerId);
			if(result){
				responseString = "Controller="+controllerId+" has been deleted correctly.";
				httpStatus = HttpStatus.OK;
			}
			else{
				responseString = "ERROR: The controller has NOT been deleted.";
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


