package de.dfki.slt.datadukt.gui;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.dfki.slt.datadukt.data.documents.WMDocument;
import de.dfki.slt.datadukt.engine.CWMEngine;
import de.dfki.slt.datadukt.persistence.workflowexecutions.WorkflowExecution;
import de.dfki.slt.datadukt.persistence.workflowtemplates.WorkflowTemplate;

@Controller
@RequestMapping("/cwm/gui/sp")
public class WebController {

	String footerText = "&copy 2021 Speaker Project. All Rights Reserved";

	@Autowired
	CWMEngine engine;
	
    /**
     * @return The index website of the GUI for the Workflow Manager
     */
    @GetMapping("/")
    public String greeting(org.springframework.ui.Model model) {
        return "index";
    }

    @GetMapping("/list")
    public String search(org.springframework.ui.Model model,HttpServletRequest request) throws Exception{
    	model.addAttribute("controllers", "");
    	model.addAttribute("templates", "");
    	model.addAttribute("executions", "");
    	System.out.println("We arrive here.");
    	try {
			List<de.dfki.slt.datadukt.controllers.Controller> controllers = engine.listControllersObject(null);
	    	model.addAttribute("contros", controllers);
			String controllersHTML = "";
			for (de.dfki.slt.datadukt.controllers.Controller controller : controllers) {
				controllersHTML += controller.getControllerId() + "--" + controller.getControllerName()  + "--" + controller.getName()  + "<br/>";
			}
	    	model.addAttribute("controllers", controllersHTML);

	    	
	    	List<WorkflowTemplate> templates = engine.listWorkflowTemplatesObject(null);
	    	model.addAttribute("templas", templates);
			String templatesHTML = "";
			for (WorkflowTemplate template : templates) {				
				templatesHTML += template.getName() + "--" + template.getWorkflowId()  + "--" + template.getWorkflowTemplateId()  + "<br/>";
			}
	    	model.addAttribute("templates", templatesHTML);
			
	    	List<WorkflowExecution> workflows = engine.listWorkflowExecutionsObject(null);
	    	model.addAttribute("execus", workflows);
			String workflowsHTML = "";
	    	for (WorkflowExecution execution : workflows) {
				workflowsHTML += execution.getOutput();
			}
	    	model.addAttribute("executions", workflowsHTML);
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    	}
    	System.out.println("Here too.");
    	return "list";
    }

    @GetMapping("/search-query")
    public String searchQuery(org.springframework.ui.Model model,
			@RequestParam(value = "inputtext", required = false) String inputtext,
    		HttpServletRequest request) {
    	model.addAttribute("sourceText", inputtext);
    	WMDocument qd = null;
    	String annotatedText = null;
    	model.addAttribute("annotatedText", annotatedText);
    	return "resultsearch";
    }

    @GetMapping("/results")
    public String results(org.springframework.ui.Model model,HttpServletRequest request) {
    	return "resultsearch";
    }

    @GetMapping("/results3")
    public String results3(org.springframework.ui.Model model,HttpServletRequest request) {
    	return "results3";
    }

    @GetMapping("/document")
    public String document(org.springframework.ui.Model model,
			@RequestParam(value = "documentId", required = false) String documentId,
    		HttpServletRequest request) {
    	return "document";
    }

}
