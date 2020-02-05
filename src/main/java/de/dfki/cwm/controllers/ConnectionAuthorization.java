package de.dfki.cwm.controllers;

public class ConnectionAuthorization {

	String name;
	String type;
	String defaultValue;
	boolean required;
	
	String user;
	String password;
	
	public ConnectionAuthorization(String name, String type, String defaultValue, boolean required) {
		super();
		this.name = name;
		this.type = type;
		this.defaultValue = defaultValue;
		this.required = required;
		
		if(name.equalsIgnoreCase("basicauth")) {
			user = defaultValue.substring(0, defaultValue.indexOf(':'));
			password = defaultValue.substring(defaultValue.indexOf(':')+1);
		}
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
