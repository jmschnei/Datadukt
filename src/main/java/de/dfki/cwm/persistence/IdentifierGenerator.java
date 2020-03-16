package de.dfki.cwm.persistence;

import java.util.Date;

public class IdentifierGenerator {

	public static String createWorkflowTemplateId() {
		// TODO Auto-generated method stub
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


}
