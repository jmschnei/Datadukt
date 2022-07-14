package de.dfki.slt.datadukt.communication.messages;

import org.json.JSONObject;

import de.dfki.slt.datadukt.communication.rabbitmq.RabbitMQMessage;

public class NIFURLMessage extends RabbitMQMessage{

	/**
	 * document can be documentId in case isContent=false or the content of the document in case isContent=true
	 */
	private String document;
	private String workflowId;
	private String callback;
	
	private boolean persist;
	private boolean isContent;
	
	public NIFURLMessage() {
		super();
	}

	public NIFURLMessage(String documentId, String workflowId, String callback, boolean persist, boolean isContent) {
		super();
		this.document = documentId;
		this.workflowId = workflowId;
		this.callback = callback;
		this.persist = persist;
		this.isContent = isContent;
	}

	public byte[] getByteArray(){
		try{
			JSONObject json = new JSONObject();
			json.put("document", document);
			json.put("workflowId", workflowId);
			json.put("callback", callback);
			json.put("persist", persist);
			json.put("isContent", isContent);
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
			json.put("document", document);
			json.put("workflowId", workflowId);
			json.put("callback", callback);
			json.put("persist", persist);
			json.put("isContent", isContent);
			return json.toString();
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public String getUrl() {
		return document;
	}

	public void setUrl(String url) {
		this.document = url;
	}

	public String getCallback() {
		return callback;
	}

	public void setCallback(String callback) {
		this.callback = callback;
	}

	public String getDocumentId() {
		return document;
	}

	public void setDocumentId(String documentId) {
		this.document = documentId;
	}

	public String getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}

	public boolean isPersist() {
		return persist;
	}

	public void setPersist(boolean persist) {
		this.persist = persist;
	}

	public boolean isContent() {
		return isContent;
	}

	public void setContent(boolean isContent) {
		this.isContent = isContent;
	}

}
