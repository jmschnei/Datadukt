package de.dfki.cwm.webcontrollers;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.request.HttpRequest;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import de.dfki.cwm.controllers.Controller;
import de.dfki.cwm.controllers.restapi.RestApiController;
import de.qurator.commons.QuratorDocument;
import de.qurator.commons.conversion.QuratorSerialization;

public class Test {

	
	public static void main(String[] args) throws Exception{
		
		
		Connection rabbitMQConnection;
		Channel rabbitMQChannel;

		Channel rabbitMQChannelPublishing;
		Channel rabbitMQChannelConsuming;
		ConnectionFactory factory = new ConnectionFactory();

//		String userName = "guest";
//		String password = "guest";
//		String virtualHost="/";
//		String hostName = "localhost";
//		int portNumber = 8081;
//		factory.setUsername(userName);
//		factory.setPassword(password);
//		factory.setVirtualHost(virtualHost);
//		factory.setHost(hostName);
//		factory.setPort(portNumber);
//		System.out.println("RABBITMQ features: " + hostName+" "+portNumber+" "+virtualHost);
//		rabbitMQConnection = factory.newConnection();

		JSONObject json = new JSONObject(FileUtils.readFileToString(new File("src/main/resources/controllers/NERController.json")));
		RestApiController c = new RestApiController(json, null);
		
		QuratorDocument qd = new QuratorDocument("The test to be processed is this one, and probably it can be annotated by Microsoft or Google and distributed to a company located in Berlin, which benefits Salvador Dali.");
    	HttpRequest hrwb = c.controllerConnection.getRequest(QuratorSerialization.toRDF(qd, "TURTLE"),true,null);
//		hrwb = hrwb.queryString("documentURI", document);	
    	System.out.println(hrwb.getUrl());
    	System.out.println(new String(hrwb.getBody().getEntity().getContent().readAllBytes()));
    	System.out.println(hrwb.getHttpMethod());
    	System.out.println(hrwb.getHeaders());
    	HttpResponse<String> response371 = hrwb.asString();
    	System.out.println("BODY: "+response371.getBody());
	}
}
