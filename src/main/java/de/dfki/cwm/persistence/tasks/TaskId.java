package de.dfki.cwm.persistence.tasks;

import java.io.Serializable;

public class TaskId implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
    
	String taskId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
