package de.dfki.cwm.controllers.restapi;

public class RestApiParameter {

	String name;
	String type;
	String defaultValue;
	boolean required;
	
	public RestApiParameter(String name, String type, String defaultValue, boolean required) {
		super();
		this.name = name;
		this.type = type;
		this.defaultValue = defaultValue;
		this.required = required;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}
	
}
