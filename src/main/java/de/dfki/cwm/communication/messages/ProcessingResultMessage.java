package de.dfki.cwm.communication.messages;

import org.json.JSONObject;

import de.dfki.cwm.communication.rabbitmq.RabbitMQMessage;

public class ProcessingResultMessage extends RabbitMQMessage{

	private String content;
	private String status;
	private String controllerId;
	private String workflowId;
	
	public ProcessingResultMessage() {
		super();
	}
	
	public ProcessingResultMessage(String url, String status, String controllerId, String workflowId) {
		super();
		this.content = url;
		this.status = status;
		this.controllerId = controllerId;
		this.workflowId = workflowId;
	}
	
	public byte[] getByteArray(){
		try{
			JSONObject json = new JSONObject();
			json.put("document", content);
			json.put("status", status);
			json.put("controllerId", controllerId);
			json.put("workflowId", workflowId);
			return json.toString().getBytes();
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public String toString(){
		try{
			JSONObject json = new JSONObject();
			json.put("document", content);
			json.put("status", status);
			json.put("controllerId", controllerId);
			json.put("workflowId", workflowId);
			return json.toString();
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
