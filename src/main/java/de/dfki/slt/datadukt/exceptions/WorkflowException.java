package de.dfki.slt.datadukt.exceptions;

/**
 * @author julianmorenoschneider
 * @project Datadukt
 * @date 17.04.2020
 * @date_modified 15.07.2022
 * @company DFKI
 * @description WorkflowException defining errors 
 *
 */
public class WorkflowException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public WorkflowException() {
		super();
	}

	public WorkflowException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public WorkflowException(String message, Throwable cause) {
		super(message, cause);
	}

	public WorkflowException(String message) {
		super(message);
	}

	public WorkflowException(Throwable cause) {
		super(cause);
	}
	
}
