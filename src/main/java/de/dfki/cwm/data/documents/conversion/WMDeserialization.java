/**
 * 
 */
package de.dfki.cwm.data.documents.conversion;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.JsonLDWriteContext;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.RDFWriter;
import org.json.JSONObject;
import org.semarglproject.vocab.RDF;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.utils.JsonUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.dfki.cwm.data.Format;
import de.dfki.cwm.data.documents.BaseAnnotation;
import de.dfki.cwm.data.documents.LabelAnnotation;
import de.dfki.cwm.data.documents.LabelPositionAnnotation;
import de.dfki.cwm.data.documents.LinkAnnotation;
import de.dfki.cwm.data.documents.LinkedLabelPositionAnnotation;
import de.dfki.cwm.data.documents.NumericAnnotation;
import de.dfki.cwm.data.documents.PositionAnnotation;
import de.dfki.cwm.data.documents.TextAnnotation;
import de.dfki.cwm.data.documents.WMAnnotationUnit;
import de.dfki.cwm.data.documents.WMDocument;
import de.dfki.cwm.data.documents.WMDocumentPart;

/**
 * @author Julian Moreno Schneider julian.moreno_schneider@dfki.de
 * @modified_by 
 * @project java
 * @date 25 Feb 2020
 * @date_modified 10.06.2020
 * @company DFKI
 * @description 
 * 
 */
public class WMDeserialization {

	public static WMDocument workflowManagerDocumentFromFormat(String content, String sFormat, HashMap<String, String> metadata) throws Exception {
		Format format = Format.getFormat(sFormat);
		switch (format) {
		case JSONLD:
			return workflowmanagerDocumentFromJSON(content);
		case TURTLE:
			
		case RDF:
		case RDFXML:
			return fromRDF(content, sFormat);
		case WAV:
		case MP3:
			return fromAudio(content,metadata);
		case URI:
			return fromURI(content,metadata);
		case TEXT:
			return fromPlainText(content,metadata);
		default:
			throw new Exception("Input format ["+sFormat+"] is not supported.");
		}
	}

	public static WMDocument fromURI(String content,HashMap<String, String> metadata) {
		WMDocument qd = null;

		// TODO This method has to be implemeneted.
		
		return qd;
	}

	public static WMDocument fromAudio(String content,HashMap<String, String> metadata) {
		WMDocument qd = new WMDocument(content);
		qd.addType("qont:AudioDocument");
		if(metadata!=null && !metadata.isEmpty()) {
			for (String key : metadata.keySet()) {
				qd.addMetadata(key, metadata.get(key));
			}
		}
		qd.addMetadata("qont:WMDocumentId", content);
		return qd;
	}

	public static WMDocument fromPlainText(String content,HashMap<String, String> metadata) {
		WMDocument qd = new WMDocument(content);
		if(metadata!=null && !metadata.isEmpty()) {
			for (String key : metadata.keySet()) {
				qd.addMetadata(key, metadata.get(key));
			}
		}
		return qd;
	}

	/**
	 * Creates a QuratorDocument object from a JSON string. It tries first a strict
	 * Jackson deserialization, if it fails, follows a lenient version.
	 *
	 * @param json Which must only contain @id and a few other fields. Very
	 * tolerant as long as it is syntactically ok.
	 */
	public static WMDocument workflowmanagerDocumentFromJSON(String json) throws IOException {
//		System.out.println("-------------------------------------------");
//		System.out.println("-------------------------------------------");
//		System.out.println("-------------------------------------------");
//		System.out.println("-------------------------------------------");
//		System.out.println(json);
//		System.out.println("-------------------------------------------");
//		System.out.println("-------------------------------------------");
//		System.out.println("-------------------------------------------");
//		System.out.println("-------------------------------------------");
		JSONObject obj1 = new JSONObject(json);
//		System.out.println(obj1.toString(2));
//		JSONArray array = obj1.getJSONArray("annotations");
		obj1.remove("document_annotations");
		obj1.remove("annotations");
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		WMDocument qd = mapper.readValue(obj1.toString(), WMDocument.class);
		
//		System.out.println(QuratorSerialization.toRDF(qd, "TURTLE"));
		JsonNode jsonNodeRoot = mapper.readTree(json);
//		System.out.println("NODE: " + jsonNodeRoot.toString());
		
		String[] annotationTypes = {"document_annotations", "annotations"};
		for (String at : annotationTypes) {
			JsonNode jsonNodeAnnotations = jsonNodeRoot.get(at);

//			System.out.println("NODE: " + jsonNodeAnnotations.toString());
//			System.out.println(jsonNodeRoot.has("annotations"));
			if(jsonNodeRoot.has(at)) {
//				System.out.println("HAS ANNOTATIONS");
				Iterator<JsonNode> nodes = jsonNodeAnnotations.elements();
				while(nodes.hasNext()) {
//					System.out.println("NEXT NODE");
		//			String typ = "";
					JsonNode n = nodes.next();
//					System.out.println("N: "+n.toString());
					JsonNode types = n.get("@type");
//					System.out.println("TYPES: "+types);
//					System.out.println(types.iterator());
					String t = null;
					if(types.isArray()) {
//						System.out.println("ARRAY");
						for (JsonNode n2 : types) {
							t = n2.asText();
							switch (t) {
							case "qont:LabelPositionAnnotation":
								LabelPositionAnnotation lpa = mapper.readValue(n.toString(), LabelPositionAnnotation.class);
								qd.addAnnotation(lpa);
								break;
//								System.out.println("CREADO");
//								System.out.println("LPA: "+lpa.toString());
							case "qont:LabelAnnotation":
//								System.out.println("ADDING LABELANNOTATION");
								LabelAnnotation la = mapper.readValue(n.toString(), LabelAnnotation.class);
								qd.addDocumentAnnotation(la);
								break;
//								System.out.println("CREADO");
//								System.out.println("LPA: "+lpa.toString());
							case "qont:TextAnnotation":
								TextAnnotation ta = mapper.readValue(n.toString(), TextAnnotation.class);
//								System.out.println("ADDING TEXTANNOTATION");
								qd.addDocumentAnnotation(ta);
								break;
//								System.out.println("CREADO");
//								System.out.println("LPA: "+lpa.toString());
							default:
//								System.out.println("OOTTRROO");
//								break;
							}
						}
					}
					else {
//						System.out.println("NOT ARRAY");
						t = types.asText();
						switch (t) {
						case "qont:LabelPositionAnnotation":
							LabelPositionAnnotation lpa = mapper.readValue(n.toString(), LabelPositionAnnotation.class);
							qd.addAnnotation(lpa);
//							break;
//							System.out.println("CREADO");
//							System.out.println("LPA: "+lpa.toString());
						case "qont:LabelAnnotation":
							LabelAnnotation la = mapper.readValue(n.toString(), LabelAnnotation.class);
//							System.out.println("ADDING LABELANNOTATION");
							qd.addDocumentAnnotation(la);
//							break;
//							System.out.println("CREADO");
//							System.out.println("LPA: "+lpa.toString());
						case "qont:TextAnnotation":
							TextAnnotation ta = mapper.readValue(n.toString(), TextAnnotation.class);
//							System.out.println("ADDING TEXTANNOTATION");
							qd.addDocumentAnnotation(ta);
//							break;
//							System.out.println("CREADO");
//							System.out.println("LPA: "+lpa.toString());
						default:
//							System.out.println("OOTTRROO");
//							break;
						}
					}
//					System.out.println(t);
				}
			}
//			System.out.println("ESTAMOS AQUI");
			//String year = jsonNodeYear.asText();		
		}
//		System.out.println(QuratorSerialization.toRDF(qd, "TURTLE"));
		return qd;
	}

	/**
	 * Reads a QuratorDocument represented in RDF.
	 * Please note: the document to be read must have id and of type qont:QuratorDocument.
	 * Otherwise, won't work!
	 * @param rdf RDF in a String
	 * @param syntax Predefined values for lang are "RDF/XML", "N-TRIPLE",
	 * "TURTLE" (or "TTL") and "N3". null represents the default language,
	 * "RDF/XML". "RDF/XML-ABBREV" is a synonym for "RDF/XML".
	 */
	public static WMDocument fromRDF(String rdf, String syntax) throws IOException {
		try{
			if(syntax.equals("JSON-LD")){
				return fromJSONLD(rdf);
			}
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			StringReader reader = new StringReader(rdf);
			Model model = ModelFactory.createDefaultModel();
			model.read(reader, null, syntax);
			
//			model.write(System.out, "TTL");
			RDFWriter w = RDFWriter.create()
					.format(RDFFormat.JSONLD_FRAME_PRETTY)
					.context(contextBuilderWMDocument())
					.source(model)
					.build();
			w.output(bos);
			String jsonld = new String(bos.toByteArray());
//			System.out.println(jsonld);
			WMDocument doc = workflowmanagerDocumentFromJSON(jsonld);
			if (!doc.getId().startsWith("http"))
				doc.setId("http://qurator-project.de/res/"+doc.getId());
			doc.setAnnotations(parseBaseAnnotations(rdf, syntax));
//			System.out.println(doc.toJSON(false));
			return doc;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Gets the JSON-LD options writer
	 */
	public static JsonLDWriteContext contextBuilderWMDocument()  {
		JsonLdOptions options = new JsonLdOptions();
		options.setProcessingMode("json-ld-1.1");
		options.setOmitGraph(true);
		options.setFrameExpansion(false);
		options.setEmbed(true);
		options.setCompactArrays(true);
		options.setBase("http://qurator-projekt.de/res/");
		JsonLDWriteContext ctx = new JsonLDWriteContext();
		ctx.setOptions(options);
		ctx.setFrame(getWMDocumentFrame());
		return ctx;
	}

	/**
	 * Retrieves the context object for the JSON-LD. The authoritative document
	 * is online (http://lynx-project.eu/doc/jsonld/lynxdocument.json) , but we
	 * load a local version here for efficiency reasons.
	 */
	private static Object getWMDocumentFrame()  {
		String fileName = "frames/qdocfr.json";
//		String fileName = "frames/quratordocument.json";
		try {
			return (Object) JsonUtils.fromInputStream(WMDocument.class.getClassLoader().getResourceAsStream(fileName));
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}

	public static JsonLDWriteContext contextBuilderBaseAnnotation()  {
		JsonLdOptions options = new JsonLdOptions();
		options.setProcessingMode("json-ld-1.1");
		options.setOmitGraph(true);
		options.setFrameExpansion(false);
		options.setEmbed(true);
		options.setCompactArrays(true);
		options.setBase("http://qurator-projekt.de/res/");
		JsonLDWriteContext ctx = new JsonLDWriteContext();
		ctx.setOptions(options);
		ctx.setFrame(getBaseAnnotationFrame());
		return ctx;
	}    

	/**
	 * Retrieves the context object for the JSON-LD. The authoritative document
	 * is online (http://lynx-project.eu/doc/jsonld/lynxdocument.json) , but we
	 * load a local version here for efficiency reasons.
	 */
	private static Object getBaseAnnotationFrame()  {
		String fileName = "frames/quratorannotation.json";
		try {
			return (Object) JsonUtils.fromInputStream(WMDocument.class.getClassLoader().getResourceAsStream(fileName));
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}

	public static WMDocument fromJSONLD(String rdf) throws IOException {
		try{
			return workflowmanagerDocumentFromJSON(rdf);
		} catch (Exception e) {
			e.printStackTrace();
			WMDocument doc = fromJSONLenient(rdf);
			return doc;
		}
	}

//	/**
//	 *  Parse annotation from RDF format
//	 *
//	 *  @param rdf RDF in a String
//	 *  @param syntax Predefined values for lang are "RDF/XML", "N-TRIPLE",
//	 * "TURTLE" (or "TTL") and "N3". null represents the default language,
//	 * "RDF/XML". "RDF/XML-ABBREV" is a synonym for "RDF/XML".
//	 */
//	public static List<QuratorAnnotation> parseAnnotations(String rdf, String syntax) throws IOException {
//		try{
//			List<QuratorAnnotation> list = new LinkedList<QuratorAnnotation>();
//			StringReader reader = new StringReader(rdf);
//			Model model = ModelFactory.createDefaultModel();
//			model.read(reader, null, syntax);
//			// get all the annotations, these are marked as instances having the property nif:referenceContext
//			String uri = "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#"; 
//			//	            ResIterator iter = model.listSubjectsWithProperty(ResourceFactory.createProperty(RDF.TYPE), ResourceFactory.createResource(uri+"OffsetBasedString")); //vvvOffsetBasedString Annotation
//			ResIterator iter = model.listSubjectsWithProperty(ResourceFactory.createProperty(RDF.TYPE), ResourceFactory.createResource("http://lkg.lynx-project.eu/def/LynxAnnotation")); 
//			if (iter.hasNext()) {
//				// if we have annotations, iterate through all annotations
//				while (iter.hasNext()) {
//					Resource annotationResource = iter.nextResource();
//					Model annotationModel = annotationResource.listProperties().toModel();
//					StmtIterator iter2 = model.listStatements(annotationResource, ResourceFactory.createProperty(uri, "annotationUnit"), (RDFNode) null  );
//					if(iter2.hasNext()){
//						while(iter2.hasNext()){
//							annotationModel = annotationModel
//									.union(model.listStatements(iter2.nextStatement().getObject().asResource(), null, (RDFNode) null).toModel());
//						}
//					}
//					//addAnnotation(QuratorAnnotation.fromModel(annotationModel));
//					list.add(quratorAnnotationFromModel(annotationModel));
//				}
//			}
//			return list;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}

	/**
	 *  Parse annotation from RDF format
	 *
	 *  @param rdf RDF in a String
	 *  @param syntax Predefined values for lang are "RDF/XML", "N-TRIPLE",
	 * "TURTLE" (or "TTL") and "N3". null represents the default language,
	 * "RDF/XML". "RDF/XML-ABBREV" is a synonym for "RDF/XML".
	 */
	public static List<BaseAnnotation> parseBaseAnnotations(String rdf, String syntax) throws IOException {
		try{
			List<BaseAnnotation> list = new LinkedList<BaseAnnotation>();
			StringReader reader = new StringReader(rdf);
			Model model = ModelFactory.createDefaultModel();
			model.read(reader, null, syntax);
			
			
			// get all the annotations, these are marked as instances having the property nif:referenceContext
			String uri = "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#";
			//	            ResIterator iter = model.listSubjectsWithProperty(ResourceFactory.createProperty(RDF.TYPE), ResourceFactory.createResource(uri+"OffsetBasedString")); //vvvOffsetBasedString Annotation
			ResIterator iter = model.listSubjectsWithProperty(ResourceFactory.createProperty(RDF.TYPE), ResourceFactory.createResource(uri+"OffsetBasedString")); 
			if (iter.hasNext()) {
				// if we have annotations, iterate through all annotations
				while (iter.hasNext()) {
					Resource annotationResource = iter.nextResource();
					Model annotationModel = annotationResource.listProperties().toModel();
//					System.out.println(annotationResource.toString());
					if(model.contains(annotationResource, ResourceFactory.createProperty(RDF.TYPE), ResourceFactory.createResource(uri+"Context"))) {
//						System.out.println(annotationResource.getURI());
					}
					else {
						StmtIterator iter2 = model.listStatements(annotationResource, ResourceFactory.createProperty(RDF.TYPE), (RDFNode) null);
						String type = "";
						if(iter2.hasNext()){
							while(iter2.hasNext()){
								Resource typeResource = iter2.next().getResource();
								if(typeResource.getURI().contains("Annotation")) {
//								if(typeResource.getURI().contains("Annotation") || typeResource.getURI().contains("DocumentPart")) {
									type = typeResource.getURI().substring(typeResource.getURI().lastIndexOf("/")+1);
//									System.out.println(type);
								}
							}
						}
//						System.out.println(type);
						StmtIterator iter3 = model.listStatements(annotationResource, ResourceFactory.createProperty("http://qurator-projekt.de/ontology/LabelUnit"), (RDFNode) null);
						if (iter3.hasNext()) {
							annotationModel = annotationModel
									.union(model.listStatements(iter3.nextStatement().getObject().asResource(), null, (RDFNode) null).toModel());
						}
						BaseAnnotation ba = quratorBaseAnnotationFromModel(annotationModel,type);
						if(ba!=null) {
							list.add(ba);
						}
					}
//					StmtIterator iter2 = model.listStatements(annotationResource, ResourceFactory.createProperty(uri, "annotationUnit"), (RDFNode) null);
//					if(iter2.hasNext()){
//						while(iter2.hasNext()){
//							annotationModel = annotationModel
//									.union(model.listStatements(iter2.nextStatement().getObject().asResource(), null, (RDFNode) null).toModel());
//						}
//					}
//					//addAnnotation(QuratorAnnotation.fromModel(annotationModel));
				}
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String sanitize(String s) {
		s = s.replace("\"", "");
		return s;
	}

	/**
	 * Manual parsing of a json string, which is by far more lenient than
	 * jackson. Policy: be as lenient and flexible as possible to read, be
	 * strict to write
	 * Requirements: the document must have @id
	 *
	 * @param json A WMDocument or something resembling a WMDocument.
	 * @return A WMDocument or null.
	 */
	public static WMDocument fromJSONLenient(String json) {
		try {
			WMDocument doc = new WMDocument("");
			JsonObject jobj = new JsonParser().parse(json).getAsJsonObject();
			String docId = doc.getId();
			String docText = doc.getText();
			//We load the id in the most fault-tolerant way
			Set<Map.Entry<String, JsonElement>> entriesx = jobj.entrySet();
			for (Map.Entry<String, JsonElement> entry : entriesx) {
				String property = entry.getKey();
				if (property.equals("@id") || property.equals("id")) {
					JsonElement value = entry.getValue();
					if (value.isJsonPrimitive()) {
						docId = (String) value.getAsString();
					}
				}
			}
			//If there is no id, we dont want to continue.
			if (docId.isEmpty()) {
				if (jobj.get("@id") != null) {
					return null;
				}
				docId = jobj.get("@id").toString();
			}
			docId = sanitize(docId);

			if (docText == null) {
				if (jobj.get("text") != null) {
					JsonElement telem = jobj.get("text");
					if (telem.isJsonObject())
					{
						JsonObject jelem = telem.getAsJsonObject();
						docText = jelem.get("@value").getAsString();
					}
					else
						docText = jobj.get("text").getAsString();
				}
			}

			if (jobj.get("@type") != null) {
				doc.setType(getAll(jobj, "@type"));
			}
			else if (jobj.get("type") != null) {
				doc.setType(getAll(jobj, "type"));
			}

			if (jobj.get("offset_ini") != null) {
				doc.setOffsetIni(jobj.get("offset_ini").getAsInt());
			}

			if (jobj.get("offset_end") != null) {
				doc.setOffsetEnd(jobj.get("offset_end").getAsInt());
			}

			//We then parse the metadata: an array of string-array
			if (jobj.get("metadata") != null) {
				JsonObject metadata = jobj.get("metadata").getAsJsonObject();
				Set<Map.Entry<String, JsonElement>> entries = metadata.entrySet();//will return members of your object
				for (Map.Entry<String, JsonElement> entry : entries) {
					String property = entry.getKey();
					List<String> list = getAll(metadata, property);
					doc.addMetadata(property, list);
				}
			}

//			//We then parse the translations
//			if (jobj.get("translations") != null) {
//				JsonObject translations = jobj.get("translations").getAsJsonObject();
//				Set<Map.Entry<String, JsonElement>> entries = translations.entrySet();//will return members of your object
//				for (Map.Entry<String, JsonElement> entry : entries) {
//					String property = entry.getKey();
//					String value = translations.get(property).getAsString();
//					doc.translations.put(property, value);
//				}
//			}

			//We then parse the parts
			if (jobj.get("parts") != null) {
				JsonArray parts = jobj.get("parts").getAsJsonArray();
				int psize = parts.size();
				for (int i = 0; i < psize; i++) {
					WMDocumentPart part = new WMDocumentPart(null, null, 0, 0, null);
					JsonObject jpart = parts.get(i).getAsJsonObject();

					if (jpart.get("@id") != null) {
						part.id = jpart.get("@id").getAsString();
					}
					else if (jpart.get("id") != null) {
						part.id = jpart.get("id").getAsString();
					}

					if (jpart.get("@type") != null) {
						part.types = getAll(jpart, "@type");
					}
					else if (jpart.get("type") != null) {
						part.types = getAll(jpart, "type");
					}
					else if (jpart.get("@type") == null && jpart.get("type") == null){
						part.setTypes(new ArrayList<>());
					}

					if (jpart.get("offset_ini") != null) {
						part.offset_ini = jpart.get("offset_ini").getAsInt();
					}

					if (jpart.get("offset_end") != null) {
						part.offset_end = jpart.get("offset_end").getAsInt();
					}

					if (jpart.get("title") != null) {
						part.title = jpart.get("title").getAsString();
					}

					if (jpart.get("parent") != null) {
						JsonElement eparent = jpart.get("parent");
						if (eparent.isJsonPrimitive())
							part.referenceContext = eparent.getAsString();
						if (eparent.isJsonObject())
						{
							JsonObject oparent = eparent.getAsJsonObject();
							JsonElement elem22 =  oparent.get("@id");
							if (elem22!=null)
								part.referenceContext = elem22.getAsString();
							else{
								elem22 =  oparent.get("id");
								part.referenceContext = elem22.getAsString();
							}
						}
					}
					doc.addPart(part);
				}
			}

			//We finally parse the annotations
			if (jobj.get("annotations") != null) {
				JsonArray jannotations = jobj.get("annotations").getAsJsonArray();
				int asize = jannotations.size();
				for (int i = 0; i < asize; i++) {
					LabelPositionAnnotation ann = new LabelPositionAnnotation();
					//QuratorAnnotation ann = new QuratorAnnotation(null, 0, 0, null);
					JsonObject jann = jannotations.get(i).getAsJsonObject();

					if (jann.get("@type") != null) {
						ann.types = getAll(jann, "@type");
					}
					else if (jann.get("type") != null) {
						ann.types = getAll(jann, "type");
					}
					if (jann.get("@id") != null) {
						ann.id = jann.get("@id").getAsString();
					}
					else if (jann.get("id") != null) {
						ann.id = jann.get("id").getAsString();
					}
					if (jann.get("offset_init") != null) {
						ann.offset_ini = jann.get("offset_ini").getAsInt();
					}
					if (jann.get("offset_end") != null) {
						ann.offset_end = jann.get("offset_end").getAsInt();
					}
					if (jann.get("anchorOf") != null) {
						ann.anchorOf = jann.get("anchorOf").getAsString();
					}
					if (jann.get("referenceContext") != null) {
						ann.referenceContext = jann.get("referenceContext").getAsString();
					}
					if (jann.get("annotationUnit")!=null)
					{
						if (jann.get("annotationUnit") != null) {
							JsonArray jannotationunits = jann.get("annotationUnit").getAsJsonArray();
							int ausize = jannotationunits.size();
							for (int k = 0; k < ausize; k++) {
								WMAnnotationUnit au = new WMAnnotationUnit();
								JsonObject jau = jannotations.get(k).getAsJsonObject();
								if (jau.get("taClassRef") != null) {
									au.annotationProperties.put("taClassRef", jau.get("taClassRef").getAsString());
								}
								if (jau.get("taIdentRef") != null) {
									au.annotationProperties.put("taIdentRef", jau.get("taIdentRef").getAsString());
								}
							}
						}
					}
					doc.addAnnotation(ann);
				}
			}
			return doc;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}    

	/**
	 * Gets a list of Strings from an object, regardless if they are string or
	 * list. It is fault tolerant. It is aware that we had also uris in a more complex manner.
	 * @param metadata
	 * @param property The property to be retrieved
	 */
	private static List<String> getAll(JsonObject metadata, String property) {
		List<String> list = new ArrayList<String>();
		JsonElement elem = metadata.get(property);
		if (elem.isJsonArray()) {
			JsonArray arr = elem.getAsJsonArray();
			int size = arr.size();
			for (int i = 0; i < size; i++) {
				JsonElement elem2 = arr.get(i);
				if (elem2.isJsonPrimitive())
				{
					list.add(elem2.getAsString());
				}
				else if (elem2.isJsonObject())
				{
					JsonObject o2 = elem2.getAsJsonObject();
					JsonElement elem3 = o2.get("uri");
					if (elem3!=null && elem3.isJsonPrimitive())
						list.add(elem3.getAsString());
				}
			}
		}
		if (elem.isJsonPrimitive()) {
			list.add(elem.getAsString());
		}
		return list;
	}

//    /**
//     * Reads a JSON with an annotation or an array of annotations.
//     * Returns an array of annotations, with either one annotation or many, 
//     * or nothing, if there was an error.
//     * @param json JSON with an annotation or an array of annotations
//     * @return An array of annotations.
//     */
//    public static List<QuratorGeneration> quratorGenerationListFromJSON(String json) {
//        List<QuratorGeneration> annotations = new ArrayList<QuratorGeneration>();
//        
//        try {
//            ObjectMapper mapper = new ObjectMapper();
//            json=json.trim();
//            if (json.startsWith("["))
//            {
//                List<LinkedHashMap> anns = (List<LinkedHashMap>)mapper.readValue(json, ArrayList.class);
//                for(LinkedHashMap lhm : anns)
//                {
//                    QuratorGeneration ann = mapper.convertValue(lhm, QuratorGeneration.class);
//                    annotations.add(ann);
//                }
//            }
//            if (json.startsWith("{"))
//            {
//                QuratorGeneration ann = mapper.readValue(json, QuratorGeneration.class);
//                annotations.add(ann);
//            }
//            return annotations;
//            
//        } catch (Exception e) {
//            e.printStackTrace();
//            return annotations;
//        }
//    }

//    public static QuratorGeneration fromModel(Model model) {
//        ObjectMapper mapper = new ObjectMapper();
//        model.setNsPrefixes(setPrefixes());
//        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        RDFWriter w = RDFWriter.create()
//                .format(RDFFormat.JSONLD_FRAME_PRETTY)
//                .source(model)
//                .context(contextBuilder())
//                .build();
//        w.output(bos);
//        String jsonld = new String(bos.toByteArray());
//        QuratorGeneration annotation = null;
//        try {
//            annotation = mapper.readValue(jsonld, QuratorGeneration.class);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return annotation;
//    }
    
	public static Map<String, String> setPrefixes(){
		Model model = ModelFactory.createDefaultModel();
		Map<String, String> prefixes = model.getNsPrefixMap();
		prefixes.put("nif-ann", "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-annotation#");
		prefixes.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		prefixes.put("xsd", "http://www.w3.org/2001/XMLSchema#");
		prefixes.put("itsrdf", "http://www.w3.org/2005/11/its/rdf#");
		prefixes.put("nif", "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#");
		prefixes.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		prefixes.put("lkg", "http://lkg.lynx-project.eu/def/");
		prefixes.put("lkg-res", "http://lkg.lynx-project.eu/res/");
		prefixes.put("eli", "http://data.europa.eu/eli/ontology#");
		prefixes.put("owl", "http://www.w3.org/2002/07/owl#");
		prefixes.put("foaf", "http://xmlns.com/foaf/spec/");
		prefixes.put("dct", "http://purl.org/dc/terms/");
		prefixes.put("dbo", "http://dbpedia.org/ontology/");
		prefixes.put("qont", "http://qurator-projekt.de/ontology/");
		return prefixes;
	}

//    /**
//     * Reads a JSON with an annotation or an array of annotations.
//     * Returns an array of annotations, with either one annotation or many, 
//     * or nothing, if there was an error.
//     * @param json JSON with an annotation or an array of annotations
//     * @return An array of annotations.
//     */
//    public static List<QuratorAnnotation> quratorAnnotationListFromJSON(String json) {
//        List<QuratorAnnotation> annotations = new ArrayList<QuratorAnnotation>();
//        try {
//            ObjectMapper mapper = new ObjectMapper();
//            json=json.trim();
//            if (json.startsWith("["))
//            {
//                List<LinkedHashMap> anns = (List<LinkedHashMap>)mapper.readValue(json, ArrayList.class);
//                for(LinkedHashMap lhm : anns)
//                {
//                    QuratorAnnotation ann = mapper.convertValue(lhm, QuratorAnnotation.class);
//                    annotations.add(ann);
//                }
//            }
//            if (json.startsWith("{"))
//            {
//                QuratorAnnotation ann = mapper.readValue(json, QuratorAnnotation.class);
//                annotations.add(ann);
//            }
//            return annotations;
//            
//        } catch (Exception e) {
//            e.printStackTrace();
//            return annotations;
//        }
//    }

//    public static QuratorAnnotation quratorAnnotationFromModel(Model model) {
//        ObjectMapper mapper = new ObjectMapper();
//        model.setNsPrefixes(setPrefixes());
//        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        RDFWriter w = RDFWriter.create()
//                .format(RDFFormat.JSONLD_FRAME_PRETTY)
//                .source(model)
//                .context(contextBuilder())
//                .build();
//        w.output(bos);
//        String jsonld = new String(bos.toByteArray());
//        QuratorAnnotation annotation = null;
//        try {
//            annotation = mapper.readValue(jsonld, QuratorAnnotation.class);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return annotation;
//    }

    public static BaseAnnotation quratorBaseAnnotationFromModel(Model model,String type) {
        ObjectMapper mapper = new ObjectMapper();
        model.setNsPrefixes(setPrefixes());
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        model.write(System.out, "TTL");
        RDFWriter w = RDFWriter.create()
                .format(RDFFormat.JSONLD_FRAME_PRETTY)
                .source(model)
                .context(contextBuilderBaseAnnotation())
                .build();
        w.output(bos);
        String jsonld = new String(bos.toByteArray());
//        System.out.println(jsonld);
        BaseAnnotation annotation = null;
        try {
        	switch (type) {
			case "LabelPositionAnnotation":
	            annotation = mapper.readValue(jsonld, LabelPositionAnnotation.class);
				break;
			case "LinkedLabelPositionAnnotation":
	            annotation = mapper.readValue(jsonld, LinkedLabelPositionAnnotation.class);
				break;
			case "LabelAnnotation":
	            annotation = mapper.readValue(jsonld, LabelAnnotation.class);
				break;
			case "TextAnnotation":
	            annotation = mapper.readValue(jsonld, TextAnnotation.class);
				break;
			case "LinkAnnotation":
	            annotation = mapper.readValue(jsonld, LinkAnnotation.class);
				break;
			case "PositionAnnotation":
	            annotation = mapper.readValue(jsonld, PositionAnnotation.class);
				break;
			case "NumericAnnotation":
	            annotation = mapper.readValue(jsonld, NumericAnnotation.class);
				break;
			case "BaseAnnotation":
	            annotation = mapper.readValue(jsonld, BaseAnnotation.class);
				break;
			case "WMDocumentPart":
	            annotation = mapper.readValue(jsonld, WMDocumentPart.class);
				break;
			default:
				System.out.println("ERROR: type not supported: "+type);
				break;
			}
        } catch (IOException e) {
            e.printStackTrace();
        }
        return annotation;
    }

    public static void main(String[] args) throws Exception {
////		String jsonString = FileUtils.readFileToString(new File("src/test/resources/qd_withAnnotation.json"),"utf-8");
//////		Assert.assertEquals(jsonString, qd_withAnnotation.toJSON());
////		WMDocument qdGenerated = QuratorDeserialization.WMDocumentFromJSON(jsonString);
////		System.out.println(qdGenerated.toJSON());
//    	
//		String text = "Microsoft hat neue Büros in Berlin geöffnet. Europa muss dazu etwas machen. Merkel und Makron treffen sich nächste Woche, über die GDPR zu sprechen.";
//		
//		WMDocument qd = new WMDocument(text);
//
//		List<Label> labels1 = new LinkedList<Label>();
//		Map<String, String> properties1 = new HashMap<String, String>();
//		properties1.put("taClassRef", "http://dbpedia.org/ontology/Organization");
//		properties1.put("", "");
//		labels1.add(new Label(properties1));
//		int si1 = 0;
//		int ei1 = 9;
//		LabelPositionAnnotation ann1 = new LabelPositionAnnotation("", si1, ei1, text.substring(si1, ei1), labels1);
//		qd.addAnnotation(ann1);
//
//		List<Label> labels2 = new LinkedList<Label>();
//		Map<String, String> properties2 = new HashMap<String, String>();
//		properties2.put("taClassRef", "http://dbpedia.org/ontology/Location");
//		properties2.put("", "");
//		labels2.add(new Label(properties2));
//		int si2 = 28;
//		int ei2 = 34;
//		LabelPositionAnnotation ann2 = new LabelPositionAnnotation("", si2, ei2, text.substring(si2, ei2), labels2);
//		qd.addAnnotation(ann2);
//
//		List<Label> labels3 = new LinkedList<Label>();
//		Map<String, String> properties3 = new HashMap<String, String>();
//		properties3.put("taClassRef", "http://dbpedia.org/ontology/Location");
//		properties3.put("", "");
//		labels3.add(new Label(properties3));
//		int si3 = 45;
//		int ei3 = 51;
//		LabelPositionAnnotation ann3 = new LabelPositionAnnotation("", si3, ei3, text.substring(si3, ei3), labels3);
//		qd.addAnnotation(ann3);
//
//		String rdfText = qd.toRDF("TURTLE");
////		System.out.println(rdfText);
////		NELDService serv = new NELDService();
////		String result = serv.analyzeSynchronous(qd.toRDF("TURTLE"), "text/turtle", "text/turtle", null, true, null);
//
//		WMDocument doc = QuratorDeserialization.fromRDF(rdfText, "TURTLE");
//
//		System.out.println(qd.toJSON(false));

		String rdfString = FileUtils.readFileToString(new File("src/test/resources/qd_withAll2.rdf"),"utf-8");
		System.out.println(rdfString);
		WMDocument qdGenerated = WMDeserialization.fromRDF(rdfString, "TURTLE");
		System.out.println(WMSerialization.toRDF(qdGenerated,"TURTLE"));
	}
}
