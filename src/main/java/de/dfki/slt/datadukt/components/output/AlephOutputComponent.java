package de.dfki.slt.datadukt.components.output;

import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import de.dfki.slt.datadukt.components.WorkflowComponent;
import de.dfki.slt.datadukt.data.documents.BaseAnnotation;
import de.dfki.slt.datadukt.data.documents.Label;
import de.dfki.slt.datadukt.data.documents.LabelAnnotation;
import de.dfki.slt.datadukt.data.documents.LabelPositionAnnotation;
import de.dfki.slt.datadukt.data.documents.WMDocument;
import de.dfki.slt.datadukt.data.documents.conversion.WMDeserialization;
import de.dfki.slt.datadukt.exceptions.WorkflowException;
import de.dfki.slt.datadukt.persistence.DataManager;

public class AlephOutputComponent extends OutputComponent {

		public AlephOutputComponent() {
		}

		@Override
		public String executeComponentSynchronous(String document, HashMap<String, String> parameters, boolean priority, DataManager manager, String outputCallback, String statusCallback, boolean persist, boolean isContent) throws WorkflowException{
			return executeComponent(document, parameters, priority, manager, outputCallback, statusCallback, persist, isContent);
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
				
//				System.out.println("---------------");
//				System.out.println("---------------");
//				System.out.println("---------------");
//				System.out.println(content);
//				System.out.println("---------------");
//				System.out.println("---------------");
//				System.out.println("---------------");
				
				WMDocument qd = WMDeserialization.fromRDF(content, "TURTLE");
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
