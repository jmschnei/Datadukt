package de.dfki.cwm.data.documents.conversion;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import de.dfki.cwm.data.Format;
import de.dfki.cwm.data.documents.Label;
import de.dfki.cwm.data.documents.LabelAnnotation;
import de.dfki.cwm.data.documents.LabelPositionAnnotation;
import de.dfki.cwm.data.documents.TextAnnotation;
import de.dfki.cwm.data.documents.WMDocument;
import de.dfki.cwm.data.documents.conversion.nif.NIFConversionService;
import de.dfki.cwm.data.documents.conversion.nif.NifAnnotation;
import de.dfki.cwm.data.documents.conversion.nif.NifDocument;

public class Conversion {

	static Logger logger = Logger.getLogger(Conversion.class);

	public WMDocument fromString(WMDocument qdInput, String content, Format format) {
		switch (format) {
		case NIFv20:
			return fromNIF20(content);
		case NIFv21:
			return fromNIF21(content);
		case JSONTOPIC:
			boolean hasLabels = false;
			JSONObject obj = new JSONObject(content);
			LabelAnnotation la = new LabelAnnotation();
			if(obj.has("prediction")) {
				JSONArray array = obj.getJSONArray("prediction");
				for (int i = 0; i < array.length(); i++) {
					hasLabels = true;
					String pred = array.getString(i);
			    	Label l2 = new Label();
			    	l2.add("qont:Topic", pred);
					la.addLabel(l2);
				}
			}
			if(hasLabels) {
				qdInput.addDocumentAnnotation(la);
			}
			return qdInput;
		case TEXT:
			LabelAnnotation la_text = new LabelAnnotation();
			Label l2 = new Label();
			l2.add("qont:Translation", content);
			la_text.addLabel(l2);
			qdInput.addDocumentAnnotation(la_text);
			return qdInput;
		default:
			return null;
		}
	}

	private WMDocument fromNIF20(String content) {
		try {
			WMDocument qd = null;
			NifDocument doc = NIFConversionService.unserializeNIF(content, "text/turtle");
			qd = new WMDocument(doc.getIsString());
			Map<String,NifAnnotation> anns = doc.getAnnotations();
			for (String key : anns.keySet()) {
				NifAnnotation ann = anns.get(key);
				LabelPositionAnnotation lpa = new LabelPositionAnnotation(qd.getId(),ann.getBeginIndex(),ann.getEndIndex(),ann.getAnchor());
 				Map<String, Object> props = ann.getProperties();
				Map<String, String> props2 = new HashMap<String, String>();
				for (String k : props.keySet()) {
					Object o = props.get(k);
					props2.put(k, o.toString());
				}
				Label l = new Label(props2);
				lpa.addLabel(l);
				qd.addAnnotation(lpa);
			}
			return qd;
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private WMDocument fromNIF21(String content) {
		WMDocument qd = null;

		//TODO
		
		
		
		return qd;
	}

	public Object toFormat(WMDocument qd, Format format) {
		switch (format) {
		case TEXT:
			return qd.getText();
		case NIFv20:
			return toNIF20(qd);
		case NIFv21:
			return toNIF21(qd);
		case AUDIO:
			return toAudio(qd);
		default:
			System.out.println();
			logger.error("Conversion to format "+format+" not supported.");
			return null;
		}
	}

	private String toAudio(WMDocument qd) {
		Map<String, Object> metadata = qd.getMetadata();
		if(metadata.containsKey("qont:WMDocumentId")) {
			String s = metadata.get("qont:WMDocumentId").toString();
			System.out.println(s);
			return "file:"+s;
		}
		else {
			return null;
		}
	}

	private String toNIF20(WMDocument qd) {
		
		
		// TODO Auto-generated method stub
		
		
		return null;
	}

	private String toNIF21(WMDocument qd) {

		
		
		return null;
	}

	public static void main(String[] args) throws Exception {
		
		String text = "In Sachsen und Brandenburg hat die CDU bei den Wahlen stark verloren, vor allem an die AfD. Schleswig-Holsteins Ministerpräsident Daniel Günther spricht von einem Alarmsignal.";
		Unirest.setTimeouts(1200000, 1200000);
		String url = "";
		HttpResponse<String> response = null;

		url = "https://demo.qurator.ai/pub/srv-bertner-de/spotEntities";
		response = Unirest.post(url)
				.queryString("informat", "txt")
				.queryString("outformat", "turtle")
				.queryString("input", text)
				.header("Content-Type", "txt").header("Accept", "turtle")
				.basicAuth("qurator", "dd17f230-a879-48cf-9220-55b4fcd4b941")
				.body(text).asString();					

		if(response.getStatus()==200) {
			Conversion c = new Conversion();
			WMDocument qd = c.fromString(null,response.getBody(), Format.NIFv20);
			System.out.println(qd.getText());
			System.out.println(WMSerialization.toRDF(qd, "TTL"));
		}
		else {
			System.err.println("There was an error calling external service ");
			System.out.println(response.getBody());
		}
		
//		String content = "@prefix itsrsdf: <http://www.w3.org/2005/11/its/rdf#> .\n" + 
//				"@prefix nif: <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n" + 
//				"@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" + 
//				"@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .\n" + 
//				"@prefix xml: <http://www.w3.org/XML/1998/namespace> .\n" + 
//				"@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .\n" + 
//				"\n" + 
//				"<http://qurator.ai/documents/1592320033.5184696#char=0,175> a nif:Context,\n" + 
//				"        nif:String ;\n" + 
//				"    nif:beginIndex \"0\"^^xsd:nonNegativeInteger ;\n" + 
//				"    nif:endIndex \"175\"^^xsd:nonNegativeInteger ;\n" + 
//				"    nif:isString \"In Sachsen und Brandenburg hat die CDU bei den Wahlen stark verloren, vor allem an die AfD. Schleswig-Holsteins Ministerpräsident Daniel Günther spricht von einem Alarmsignal.\" .\n" + 
//				"\n" + 
//				"<http://qurator.ai/documents/1592320033.5184696#char=130,144> a nif:String ;\n" + 
//				"    nif:anchorOf \"Daniel Günther\" ;\n" + 
//				"    nif:beginIndex \"130\"^^xsd:nonNegativeInteger ;\n" + 
//				"    nif:endIndex \"144\"^^xsd:nonNegativeInteger ;\n" + 
//				"    itsrsdf:taClassRef \"http://dbpedia.org/ontology/Person\" .\n" + 
//				"\n" + 
//				"<http://qurator.ai/documents/1592320033.5184696#char=15,26> a nif:String ;\n" + 
//				"    nif:anchorOf \"Brandenburg\" ;\n" + 
//				"    nif:beginIndex \"15\"^^xsd:nonNegativeInteger ;\n" + 
//				"    nif:endIndex \"26\"^^xsd:nonNegativeInteger ;\n" + 
//				"    itsrsdf:taClassRef \"http://dbpedia.org/ontology/Location\" .\n" + 
//				"\n" + 
//				"<http://qurator.ai/documents/1592320033.5184696#char=3,10> a nif:String ;\n" + 
//				"    nif:anchorOf \"Sachsen\" ;\n" + 
//				"    nif:beginIndex \"3\"^^xsd:nonNegativeInteger ;\n" + 
//				"    nif:endIndex \"10\"^^xsd:nonNegativeInteger ;\n" + 
//				"    itsrsdf:taClassRef \"http://dbpedia.org/ontology/Location\" .\n" + 
//				"\n" + 
//				"<http://qurator.ai/documents/1592320033.5184696#char=35,38> a nif:String ;\n" + 
//				"    nif:anchorOf \"CDU\" ;\n" + 
//				"    nif:beginIndex \"35\"^^xsd:nonNegativeInteger ;\n" + 
//				"    nif:endIndex \"38\"^^xsd:nonNegativeInteger ;\n" + 
//				"    itsrsdf:taClassRef \"http://dbpedia.org/ontology/Organisation\" .\n" + 
//				"\n" + 
//				"<http://qurator.ai/documents/1592320033.5184696#char=87,90> a nif:String ;\n" + 
//				"    nif:anchorOf \"AfD\" ;\n" + 
//				"    nif:beginIndex \"87\"^^xsd:nonNegativeInteger ;\n" + 
//				"    nif:endIndex \"90\"^^xsd:nonNegativeInteger ;\n" + 
//				"    itsrsdf:taClassRef \"http://dbpedia.org/ontology/Organisation\" .\n" + 
//				"\n" + 
//				"<http://qurator.ai/documents/1592320033.5184696#char=92,111> a nif:String ;\n" + 
//				"    nif:anchorOf \"Schleswig-Holsteins\" ;\n" + 
//				"    nif:beginIndex \"92\"^^xsd:nonNegativeInteger ;\n" + 
//				"    nif:endIndex \"111\"^^xsd:nonNegativeInteger ;\n" + 
//				"    itsrsdf:taClassRef \"http://dbpedia.org/ontology/Location\" .\n" + 
//				"\n" + 
//				"\n" + 
//				"";
		
	}

	public WMDocument fromELGResponse(WMDocument qd, JSONObject resp, Format outputF, String source) throws Exception {
		String text = "";
		String type = resp.getString("type");
		if(type.equalsIgnoreCase("texts")) {
			JSONArray array = resp.getJSONArray("texts");
			for (int i = 0; i < array.length(); i++) {
				JSONObject js = array.getJSONObject(i);
				String content = js.getString("content");
				text = text + content;
			}
			TextAnnotation ta = new TextAnnotation(text, null);
			ta.addMetadata("qont:provenance", source);
			qd.addDocumentAnnotation(ta);
		}
		else if (type.equalsIgnoreCase("annotations")){
			String sourceText = qd.getText();
			JSONObject annos = resp.getJSONObject("annotations");
			for (String  key : annos.keySet()) {
				JSONArray array = annos.getJSONArray(key);
				for (int i = 0; i < array.length(); i++) {
					JSONObject js = array.getJSONObject(i);
					int iStart = js.getInt("start");
					int iEnd = js.getInt("end");
					JSONObject features = js.getJSONObject("features");
			    	Label l = new Label();
			    	for (String s : features.keySet()) {
			    		if(!s.equalsIgnoreCase("nif:anchorOf")) {
					    	l.add(s, features.getString(s));
			    		}
					}
					LabelPositionAnnotation lpa = new LabelPositionAnnotation(qd.getContext(), 
																			iStart, 
																			iEnd, 
																			sourceText.substring(iStart, iEnd));
					lpa.addLabel(l);
					qd.addAnnotation(lpa);
					
/**
 * {
 * "@context":"http://qurator.de/doc/jsonld/quratordocument.json",
 * "@id":"http://qurator-project.de/res/f05787bf",
 * 
 * "@type":["nif:Context","nif:OffsetBasedString","qont:QuratorDocument"],
 * "parts":[],
 * "metadata":{"language":"en","id_local":"f05787bf"},
 * "text":"Microsoft hat neue Büros in Berlin im 1996 geöffnet. Europa muss dazu etwas machen. Merkel und Makron treffen sich nächste Woche, über die GDPR zu sprechen.",
 * "offset_ini":0,
 * "offset_end":156,
 * "annotations":[],
 * "document_annotations":[
 * 		{
 * 			"@id":"http://qurator.de/doc/jsonld/quratordocument.json#offset_38_42",
 * 			"@type":["qont:LabelPositionAnnotation","nif:OffsetBasedString"],
 * 			"offset_ini":38,
 * 			"offset_end":42,
 * 			"anchorOf":"1996",
 * 			"referenceContext":"http://qurator.de/doc/jsonld/quratordocument.json",
 * 			"labelUnits":[
 * 				{
 * 					"@type":["qont:Label"],
 * 					"nif:anchorOf":"1996",
 * 					"http://www.w3.org/2005/11/its/rdf#taClassRef":"http://www.w3.org/2006/time#TemporalEntity",
 * 					"http://www.w3.org/2006/time#intervalStarts":"1996-01-01T00:00:00",
 * 					"http://www.w3.org/2006/time#intervalFinishes":"1996-12-31T23:59:59"
 * 				}
 * 			]
 * 		}
 * ]}

 */
				}
			}
			
			/**
			 * {"response":
			 * {"type":"annotations","annotations":
			 * {"Temporal Expression":
			 * [
			 * 		{
			 * 			"start":38,
			 * 			"end":42,
			 * 			"features":{
			 * 				"nif:anchorOf":"1996",
			 * 				"http://www.w3.org/2005/11/its/rdf#taClassRef":"http://www.w3.org/2006/time#TemporalEntity",
			 * 				"http://www.w3.org/2006/time#intervalStarts":"1996-01-01T00:00:00",
			 * 				"http://www.w3.org/2006/time#intervalFinishes":"1996-12-31T23:59:59"
			 * 			}
			 * 		}
			 * ]
			 * }
			 * }
			 * }
			 */
		}
		else {
			String msg = "The response from ELG Service is not supported or is empty";
			logger.error(msg);
			throw new Exception(msg);
		}
		return qd;
	}
	
}
