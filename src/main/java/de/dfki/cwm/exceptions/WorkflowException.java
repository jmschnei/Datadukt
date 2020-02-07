package de.dfki.cwm.exceptions;

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
