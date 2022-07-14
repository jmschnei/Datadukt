package de.dfki.slt.datadukt.persistence;

import java.util.Date;

public class IdentifierGenerator {

	public static String createWorkflowTemplateId() {
		String id = (new Date()).getTime()+"";
		return id;		
	}

	public static String createWorkflowExecutionId() {
		String id = (new Date()).getTime()+"";
		return id;
	}

	public static String createTaskId() {
		String id = (new Date()).getTime()+"";
		return id;
	}

	public static String createControllerId() {
		String id = (new Date()).getTime()+"";
		return id;
	}

	public static String createWorkflowInstanceId() {
		String id = (new Date()).getTime()+"";
		return id;		
	}


}
