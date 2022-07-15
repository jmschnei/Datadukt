# Datadukt # 

This is an NLP tasks orchestration tool, i.e., a tool to manage pipelines or workflows of NLP processes.

The name of this tool comes from the combination of 'Data' and 'dukt' (taken from "Aquae-dukt"). 

Datadukt orchestrates different NLP services that can be hosted in different platforms, such as Qurator, Lynx or ELG. 




## Structure of Datadukt ##

It's basic components are:

* **Tasks** Unitary pieces of work. One can think of them as specific methods of a service. They are atomic, in the sense that they don't require each other.
* **Workflow Templates** Directed Acyclig Graphs (DAG) whose nodes are tasks and whose edges indicate the sequence of execution. They are abstract just as tasks, their instantiations we call Jobs. The output of a task execution is fed as input into the next (according to the DAG) task execution.
* **Workflow Executions**  Instance of Pipeline, with a particular (possibly empty) input for each task comprising it.
* **Controllers** A thin process that specifies how to connect computing infraestructure to the workflow manager. One such processor must be created for each outside service, regardless of how many tasks can be executed within it. This connector is used to ensure that resources for a given service are not overused by different tasks.

In general, a workflow execution is a DAG of task runs. An instance of a workflow template is a workflow execution, which consists of a series of sequential task executions on a given input. Each task execution sends a request to the corresponding controller. The controllers knows how many tasks can be simultaneously executed by a given service, and sends the executions accordingly. When an execution returns, the controller notifies the corresponding task executions, that must not be the same that contacted it.


## Structure of the code

### APIs

* **DataduktWebcontroller**: controller including endpoints for the initialization and management of this instance of Datadukt
* **TasksAPI**: Rest controller for creating, deleting and listing existing Tasks in this instance of Datadukt. Url: `https://<<server-url>>/datadukt/tasks`
    * GET: Retrieves a list of Tasks.
        * Input: ---.
        * Output: JSON array containing a list of Tasks.
    * POST: This API generates a Task based on a JSON Description. The JSON description must contain:
        * Input:
            * Body: JSON description of the task.
        * Output: 
            * taskId: identifier of the created task.
    * DELETE: This API deletes an existing Task.
        * Input:
            * taskId: identifier of the task to be deleted.
        * Output: HTTP 200
* **TemplatesAPI**: Rest controller for creating, deleting and listing existing Workflow Templates in this instance of Datadukt. Url: `https://<<server-url>>/datadukt/templates`
    * GET: Retrieves a list of available Workflow Templates
        * Input: No input needed.
        * Output: JSON array containing a list of Workflow Templates.
    * POST: This API generates a Workflow Template based on a JSON Description.
        * Input:
            * Body: JSON description of the workflow template.
        * Output: 
            * workflowTemplateId: identifier of the created workflow template.
    * DELETE: This API deletes an existing Workflow Template.
        * Input:
            * workflowTemplateId: identifier of the template to be deleted
        * Output: HTTP 200
* **WorkflowExecutionAPI**: Rest controller for creating, deleting and listing existing WorkflowExceutions in this instance of Datadukt
    * Workflow Executions Management endpoint. Url: `https://<<server-url>>/datadukt/workflowexecutions`
        * GET: Retrieves a list of Workflow Executions.
            * Input
            * Output: JSON array containing a list of Workflow Executions.
        * POST: This API generates a Workflow Execution based on a JSON Description. The JSON description must contain: the workflowTemplateId, the definition of the input, the definition of the output, the status callback url, the output callback url.
            * Input:
                * Body: JSON description of the workflow execution.
            * Output: 
                * workflowExecutionId: identifier of the created workflow execution.
        * DELETE: This API deletes an existing Workflow Execution.
            * Input:
                * workflowExecutionId: identifier of the workflow execution to be deleted.
            * Output: HTTP 200
    * Execution of Workflows endpoint. Url: `https://<<server-url>>/datadukt/workflowexecutions/execute`
        * POST: This endpoint executes a concrete workflow execution in an asynchronous way.
            * Input:
                * workflowExecutionId: identifier of the workflow execution to be executed.
                * synchronous: boolean value specifying if the workflow has to be executed synchronously (true) or asynchronously (false).
                * Request Body: The document to be processed.
            * Output: HTTP 202
    * Endpoint for retrieving processing result. Url: `https://<<server-url>>/datadukt/workflowexecutions/getOutput`
        * GET: This endpoint returns the result of a WorkflowExecution that was executed in an asynchronous way.
            * Input:
                * workflowExecutionId: identifier of the workflow execution to be executed.
                * keepWaiting: if the workflow has not finished the execution, this value determines if the request has to return inmediately (keepWaiting='false') mentioning that the workflow is still running or wait until the workflow finishes and return the result (keepWaiting='true').
            * Output: 
                * A JSON containing the status of the workflow ('RUNNING' or 'FINISHED') and in case of finished the result of the execution.
* **WorkflowInstanceAPI**: Rest controller for creating, deleting and listing existing WorkflowInstances in this instance of Datadukt
    * fdas
        * sdfasdvf
* **ControllersAPI**: Rest controller for creating, deleting and listing existing Controllers in this instance of Datadukt. Datadukt preloads all the controllers that are located (JSON file) in a folder, which is specified in the configuration file (it can also be specified in the parameters of an 'initializeDatadukt' call). The property for defining the controllers folder is: `controllers_folder=<path_to_controllers_folder>`. Url: `https://<<server-url>>/datadukt/controllers`
    * GET: Retrieves a list of Controllers.
        * Input: ---
        * Output: JSON array containing a list of Controllers.
    * POST: This API generates a Controller based on a JSON Description. The JSON description must contain:
        * 
        * 
        * 
        * Input:
            * Body: JSON description of the Controller.
        * Output: 
            * controllerId: identifier of the created controller.
    * DELETE: This API deletes an existing Controller.
        * Input:
            * controllerId: identifier of the controller to be deleted.
        * Output: HTTP 200





## Run the Workflow Manager ##

RabbitMQ Docker container has to be started with the following command

docker run -d --hostname localhost --name some-rabbit -p 5672:5672 rabbitmq:latest


# TODO #

* Generate a docker-compose file that allows the build of Datadukt and the compose up of RabbitMQ and Datadukt.