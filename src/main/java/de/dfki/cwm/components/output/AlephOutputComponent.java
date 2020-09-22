package de.dfki.cwm.components.output;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import de.dfki.cwm.components.WorkflowComponent;
import de.dfki.cwm.exceptions.WorkflowException;
import de.dfki.cwm.persistence.DataManager;
import de.qurator.commons.BaseAnnotation;
import de.qurator.commons.Label;
import de.qurator.commons.LabelAnnotation;
import de.qurator.commons.LabelPositionAnnotation;
import de.qurator.commons.QuratorDocument;
import de.qurator.commons.TextAnnotation;
import de.qurator.commons.conversion.QuratorDeserialization;

public class AlephOutputComponent extends OutputComponent {

		public AlephOutputComponent() {
		}

		@Override
		public String executeComponent(String document, HashMap<String, String> parameters, boolean priority, DataManager manager, String outputCallback, String statusCallback, boolean persist, boolean isContent) throws WorkflowException{
			try {
				return executeComponent(document, priority, manager, outputCallback, statusCallback, persist, isContent);
			}
			catch(Exception e) {
				e.printStackTrace();
				throw new WorkflowException(e.getMessage());
			}
		}

		@Override
		public String executeComponent(String content, boolean priority, DataManager manager, String outputCallback, String statusCallback, boolean persist, boolean isContent) throws WorkflowException {
			try {
				
				System.out.println("---------------");
				System.out.println("---------------");
				System.out.println("---------------");
				System.out.println(content);
				System.out.println("---------------");
				System.out.println("---------------");
				System.out.println("---------------");
				
				QuratorDocument qd = QuratorDeserialization.fromRDF(content, "TURTLE");
				JSONArray arrayE = new JSONArray();
				JSONArray arrayT = new JSONArray();
				List<BaseAnnotation> annotations = qd.getAnnotations();
				List<BaseAnnotation> docannotations = qd.getDocumentAnnotations();
				annotations.addAll(docannotations);
				for (BaseAnnotation ba : annotations) {
//					System.out.println("BA");
//					System.out.println(ba.toJSON());
					if(ba instanceof LabelPositionAnnotation) {
						LabelPositionAnnotation lpa = (LabelPositionAnnotation) ba;
						String anchor = lpa.anchorOf;
						arrayE.put(anchor);
					}
					else if(ba instanceof LabelAnnotation) {
						LabelAnnotation la = (LabelAnnotation) ba;
						List<Label> labels = la.getLabels();
						for (Label l : labels) {
							arrayT.put(l.annotationProperties.get("qont:Topic"));
						}
					}
				}
				JSONObject json = new JSONObject();
				json.put("text", qd.getText());
				json.put("annotations", arrayE);
				json.put("topics", arrayT);
				return json.toString();
//				return qd.toJSON(false);
			}
			catch(Exception e) {
				throw new WorkflowException(e.getMessage());
			}
		}

		public JSONObject getJSONRepresentation() throws Exception {
			JSONObject json = new JSONObject();
			json.put("componentName", getWorkflowComponentName());
			json.put("componentId", getWorkflowComponentId());
			return json;
		}

		public String startExecuteComponent(String documentId, boolean priority, DataManager dataManager, String outputCallback, String statusCallback, boolean persist, boolean isContent) throws WorkflowException {
			return "DONE";
		}

		public void setComponentsList(List<WorkflowComponent> componentsList) {
		}

}
