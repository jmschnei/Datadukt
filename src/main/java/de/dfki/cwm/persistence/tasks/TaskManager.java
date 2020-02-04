package de.dfki.cwm.persistence.tasks;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TaskManager {

	Logger logger = Logger.getLogger(TaskManager.class);

	@Autowired
	TaskRepository taskRepository;
	
    public HashMap<String, Task> tasks = new HashMap<String, Task>();

    public Task save(Task t) {
    	Task t_tosave = t;
    	taskRepository.save(t_tosave);
		tasks.put(t.taskId, t);
		return t;
    }
    
	public List<Task> findAll(){
		Collection<Task> collection = tasks.values();
		List<Task> list = new LinkedList<Task>();
		for (Task t : collection) {
			list.add(t);
		}
		return list;
	}

	@Transactional
	public Task findOneByTaskId(String taskId) throws Exception {
		Task t = null;
		if(tasks.containsKey(taskId)) {
//			System.out.println("RETURNING TASK FROM HASHMAP");
			return tasks.get(taskId);
		}
		else if ((t=taskRepository.findOneByTaskId(taskId))!=null) {
//			System.out.println("RETURNING TASK FROM REPOSITORY");
			return t;
		}
		else {
			logger.warn("The Task ["+taskId+"] does not exist.");
			return null;
		}
	}

	@Transactional
	public void deleteByTaskId(String taskId) {
		if ((taskRepository.findOneByTaskId(taskId))!=null) {
			taskRepository.deleteByTaskId(taskId);
		}
		if(tasks.containsKey(taskId)) {
			tasks.remove(taskId);
		}
	}
}
