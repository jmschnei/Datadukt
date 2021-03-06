package de.dfki.slt.datadukt.api;

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

import de.dfki.slt.datadukt.engine.CWMEngine;

/**
 * @author julianmorenoschneider
 * @project Datadukt
 * @date 17.04.2020
 * @date_modified 14.07.2022
 * @company DFKI
 * @description Controller including endpoints specific for the Datadukt tool.
 */
@RestController
@RequestMapping("/datadukt")
public class DataduktWebcontroller{

	Logger logger = Logger.getLogger(DataduktWebcontroller.class);

	@Autowired
	CWMEngine service;

	/**
	 * Endpoint to proof that Datadukt is running properly
	 * @return Message about Datadukt running properly.
	 * @throws Exception In case Datadukt is not properly running.
	 */
	@RequestMapping(value = "/sanityCheck", method = { RequestMethod.GET })
	public ResponseEntity<String> sanityCheck() throws Exception {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/plain");
		ResponseEntity<String> response = new ResponseEntity<String>("The restcontroller of Datadukt is working properly", responseHeaders, HttpStatus.OK);
		return response;
	}

	/**
	 * @param restart
	 * @param postBody
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/initialize", method = { RequestMethod.POST})
	public ResponseEntity<String> initialize(
			@RequestParam(value="restart", required=true) boolean restart,
			@RequestBody(required = false) String postBody) throws Exception {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/plain");
		try {
			if(postBody==null || postBody.equalsIgnoreCase("")) {
				service.startWorkflowManager();
			}
			else {
				Properties properties = new Properties();
				String [] lines = postBody.split("\n");
				for (String s : lines) {
					String parts[] = s.split("=");
					properties.put(parts[0], parts[1]);
				}
				service.startWorkflowManager(properties,restart);
			}
			ResponseEntity<String> response = new ResponseEntity<String>("Workflow Manager started correctly", responseHeaders, HttpStatus.OK);
			return response;
		}
		catch(Exception e) {
			e.printStackTrace();
			ResponseEntity<String> response = new ResponseEntity<String>(e.getLocalizedMessage(), responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
			return response;
		}
	}

	@RequestMapping(value = "/stopRabbitMQManager", method = { RequestMethod.POST})
	public ResponseEntity<String> stop(
			@RequestBody(required = false) String postBody) throws Exception {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/plain");
		try {
			service.stopWorkflowManager(null);
			ResponseEntity<String> response = new ResponseEntity<String>("Workflow Manager stopped correctly", responseHeaders, HttpStatus.OK);
			return response;
		}
		catch(Exception e) {
			e.printStackTrace();
			ResponseEntity<String> response = new ResponseEntity<String>(e.getLocalizedMessage(), responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
			return response;
		}
	}

	/**
	 * TODO define a method for establishing new configuration properties (let say rabbitmq and others).
	 */
	
	/**
	 * TODO maybe define an endpoint for starting the connection with specific properties, to avoid initialization error if the properties are not correct.
	 */

}


