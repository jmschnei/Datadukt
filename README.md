# CurationWorkflowManager

The Workflow Manager orchestrates the different services of the Lynx platform. It's basic components are:

* **Tasks** Unitary pieces of work. One can think of them as specific methods of a service. They are atomic, in the sense that they don't require each other.
* **Workflow Templates** Directed Acyclig Graphs (DAG) whose nodes are tasks and whose edges indicate the sequence of execution. They are abstract just as tasks, their instantiations we call Jobs. The output of a task execution is fed as input into the next (according to the DAG) task execution.
* **Workflow Executions/Job**  Instance of Pipeline, with a particular (possibly empty) input for each task comprising it.
* **Controllers** A thin process that specifies how to connect computing infraestructure to the workflow manager. One such processor must be created for each outside service, regardless of how many tasks can be executed during it. This connector is used to ensure that resources for a given service are not overused by different tasks.

In general, a workflow execution is a DAG of task runs. An instance of a workflow template is a workflow execution, which consists of a series of sequential task executions on a given input. Each task execution sends a request to the corresponding controller. The controllers knows how many tasks can be simultaneously executed by a given service, and sends the executions accordingly. When an execution returns, the controller notifies the corresponding task executions, that must not be the same that contacted it.

## Workflow Templates

`https://<<lynx-server>>/workflowmanager/templates`

### GET

Retrieves a list of available Workflow Templates.

* Input: ---
* Output: JSON array containing a list of Workflow Templates.

### POST

This API generates a Workflow Template based on a JSON Description.

* Input:
  * Body: JSON description of the workflow template.
* Output: 
  * workflowTemplateId: identifier of the created workflow template.

### DELETE

This API deletes an existing Workflow Template.

* Input:
  * workflowTemplateId: identifier of the template to be deleted.
* Output: HTTP 200


## Workflow Executions

`https://<<lynx-server>>/workflowmanager/workflowexecutions`

### GET

Retrieves a list of Workflow Executions.

* Input: ---
* Output: JSON array containing a list of Workflow Executions.

### POST

This API generates a Workflow Execution based on a JSON Description. The JSON description must contain:
* the workflowTemplateId
* the definition of the input
* the definition of the output
* the status callback url
* the output callback url

* Input:
  * Body: JSON description of the workflow execution.
* Output: 
  * workflowExecutionId: identifier of the created workflow execution.

### DELETE

This API deletes an existing Workflow Execution.

* Input:
  * workflowExecutionId: identifier of the workflow execution to be deleted.
* Output: HTTP 200


`https://<<lynx-server>>/workflowmanager/workflowexecutions/execute`

### GET

This endpoint executes a concrete workflow execution in an asynchronous was.

* Input:
  * workflowExecutionId: identifier of the workflow execution to be executed.
* Output: HTTP 202


## Tasks

`https://<<lynx-server>>/workflowmanager/tasks`

### GET

Retrieves a list of Tasks.

* Input: ---
* Output: JSON array containing a list of Tasks.

### POST

This API generates a Task based on a JSON Description. The JSON description must contain:
* 
* 
* 

* Input:
  * Body: JSON description of the task.
* Output: 
  * taskId: identifier of the created task.

### DELETE

This API deletes an existing Task.

* Input:
  * taskId: identifier of the task to be deleted.
* Output: HTTP 200

## Controllers

The WorkflowManager preloads all the controllers that are located (JSON file) in a folder, which is specified in the configuration file (it can also be specified in the parameters of an 'initializeWorkflowManager' call). The property for defining the controllers folder is: `controllers_folder=<path_to_controllers_folder>`

### Endpoint

`https://<<lynx-server>>/workflowmanager/controllers`

### GET

Retrieves a list of Controllers.

* Input: ---
* Output: JSON array containing a list of Controllers.

### POST

This API generates a Controller based on a JSON Description. The JSON description must contain:
* 
* 
* 

* Input:
  * Body: JSON description of the Controller.
* Output: 
  * controllerId: identifier of the created controller.

### DELETE

This API deletes an existing Controller.

* Input:
  * controllerId: identifier of the controller to be deleted.
* Output: HTTP 200


# TODOs

This section enumerates the list of things that are not yet implemented.

* Include Asynchronous calls to the services from the Controllers.
* Allow the duplication of Controllers or allow the controllers to manage more than one call simultaneously.
* Implement the storage of annotation/enrichment results in case they have to be persisted in the LKG (that could be implemented in the commons.NifManagement instead of here).
* 
