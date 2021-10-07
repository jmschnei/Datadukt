/**
 * 
 */
package de.dfki.cwm.data.documents.conversion;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.JsonLDWriteContext;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.RDFWriter;
import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.utils.JsonUtils;

import de.dfki.cwm.data.Format;
import de.dfki.cwm.data.documents.BaseAnnotation;
import de.dfki.cwm.data.documents.Label;
import de.dfki.cwm.data.documents.LabelAnnotation;
import de.dfki.cwm.data.documents.LabelPositionAnnotation;
import de.dfki.cwm.data.documents.WMDocument;
import de.dfki.cwm.exceptions.WorkflowException;

/**
 * @author Julian Moreno Schneider julian.moreno_schneider@dfki.de
 * @modified_by 
 * @project java
 * @date 25 Feb 2020
 * @date_modified 16.06.2020
 * @company DFKI
 * @description 
 * 
 */
public class WMSerialization {

	public static String toFormat(String content, String sFormat, String sSemFormat) throws Exception {
		WMDocument doc = WMDeserialization.fromRDF(content, "TURTLE");
		Format format = Format.getFormat(sFormat);
		Format semFormat = Format.getFormat(sSemFormat);
		switch (format) {
		case WAV:
		case MP3:
			return toAudio(doc, semFormat.toString());
		case JSON:
			return toJSON(doc, semFormat.toString());
		case JSONLD:
		case TURTLE:
		case RDF:
		case RDFXML:
			return toRDF(doc, format.toString());
		case TEXT:
			return toPlainText(doc, semFormat.toString());
		default:
			System.out.println("The Format [] is not supported in method toFormat().");
		}
		
		return null;
	}

	public static String toFormat(WMDocument doc, String sFormat, String sSemFormat) throws Exception {
		Format format = Format.getFormat(sFormat);
		Format semFormat = Format.getFormat(sSemFormat);
		switch (format) {
		case WAV:
		case MP3:
			return toAudio(doc, semFormat.toString());
		case JSON:
			return toJSON(doc, semFormat.toString());
		case JSONLD:
		case TURTLE:
		case RDF:
		case RDFXML:
			return toRDF(doc, format.toString());
		case TEXT:
			return toPlainText(doc, semFormat.toString());
		default:
			System.out.println("The Format [] is not supported in method toFormat().");
		}
		
		return null;
	}

	public static String toAudio(WMDocument doc, String sSemFormat) throws Exception {
		
		
		
		// TODO It has to be implemented.
		
		
		
		
		return null;
	}

	public static String toJSON(WMDocument doc, String sSemFormat) throws Exception {
		
		if(sSemFormat.equalsIgnoreCase(Format.ALEPH.toString())) {
			return toJSONAleph(doc);
		}
		else{
			
			// TODO 
			
			return null;
		}
	}

	public static String toJSONAleph(WMDocument qd) throws Exception {
		try {
			JSONArray arrayE = new JSONArray();
			JSONArray arrayTopic = new JSONArray();
			JSONArray arrayTimex = new JSONArray();
			JSONArray arrayTranslation = new JSONArray();
			List<BaseAnnotation> annotations = qd.getAnnotations();
			List<BaseAnnotation> docannotations = qd.getDocumentAnnotations();
			annotations.addAll(docannotations);
			for (BaseAnnotation ba : annotations) {
//				System.out.println("BA");
//				System.out.println(ba.toJSON());
				if(ba instanceof LabelPositionAnnotation) {
					LabelPositionAnnotation lpa = (LabelPositionAnnotation) ba;
					String anchor = lpa.anchorOf;
					List<Label> labels = lpa.getLabels();
					for (Label l : labels) {
//						if(l.annotationProperties.containsKey("itsrdf:taClassRef")) {
//							String taClassRef = l.annotationProperties.get("qont:Topic");
//							
//							arrayTopic.put(l.annotationProperties.get("qont:Topic"));
//						}
//						if(l.annotationProperties.containsKey("qont:Translation")) {
//							arrayTranslation.put(l.annotationProperties.get("qont:Translation"));
//						}
					}
					arrayE.put(anchor);
				}
				else if(ba instanceof LabelAnnotation) {
					LabelAnnotation la = (LabelAnnotation) ba;
					List<Label> labels = la.getLabels();
					for (Label l : labels) {
						if(l.annotationProperties.containsKey("qont:Topic")) {
							arrayTopic.put(l.annotationProperties.get("qont:Topic"));
						}
						if(l.annotationProperties.containsKey("qont:Translation")) {
							arrayTranslation.put(l.annotationProperties.get("qont:Translation"));
						}
					}
				}
			}
			JSONObject json = new JSONObject();
			json.put("text", qd.getText());
			json.put("annotations", arrayE);
			json.put("topics", arrayTopic);
			json.put("timex", arrayTimex);
			json.put("translations", arrayTranslation);
			return json.toString();
		}
		catch(Exception e) {
			throw new WorkflowException(e.getMessage());
		}
	}

	public static String toPlainText(WMDocument doc, String sSemFormat) throws Exception {
		
		// TODO It has to be implemented.
		
		return null;
	}

	/**
	 * Converts this object into a JSON string. The object is serialized using
	 * Jackson, which transforms well the data into a JSON String. We dont give
	 * Jackson the object itself but a slightly modified version with the
	 * JSON-LD context, id, etc.
	 *
	 * @param explicitContext Whether the context is remote or embedded.
	 */
	public static String toJSON(WMDocument doc, boolean explicitContext) throws IOException {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
			String json = mapper.writeValueAsString(doc);
//			System.out.println(json);
			if (explicitContext) {
				json = json.replace("\"http://qurator.de/doc/jsonld/quratordocument.json\"", getContextExplicit());
			}
			json = json.replace("\"id\"", "\"@id\"");
			json = json.replace("\"type\"", "\"@type\"");
			return json;
		} catch (Exception e) {
			e.printStackTrace();
			return "{}";
		}
	}

	public static String toJSON(Object obj){
		try{
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(obj);
		}catch(Exception e){
			e.printStackTrace();
			return "{}";
		}
	}

	/**
	 * Serializes this object as RDF in the specified syntax.
	 *
	 * @param syntax Predefined values for lang are "RDF/XML", "N-TRIPLE",
	 * "TURTLE" (or "TTL") and "N3". null represents the default language,
	 * "RDF/XML". "RDF/XML-ABBREV" is a synonym for "RDF/XML".
	 */
	public static String toRDF(WMDocument doc, String syntax) {
		try {
			Model model = toRDF(doc);
			StringWriter out = new StringWriter();
			model.write(out, syntax);
			return out.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static Model toRDF(WMDocument doc) {
		try {
			String json1 = toJSON(doc, true);
//			Model model = ModelFactory.createDefaultModel();
			Model model = createModel();
			StringReader reader = new StringReader(json1);
//			System.out.println(json1);
			model.read(reader, null, "JSON-LD");
			return model;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/// Fast cache context retrieving system
	private static String cachecontext;
	private static boolean binitializedcontext = false;

	/**
	 * Gets the explicit context given in a file. If it fails, a cached version
	 * will work...
	 */
	public static String getContextExplicit() {
		if (binitializedcontext == true) {
			return cachecontext;
		}
		try {
//			InputStream is = WMDocument.class.getClassLoader().getResourceAsStream("contexts/quratordocument.json");
			InputStream is = WMDocument.class.getClassLoader().getResourceAsStream("contexts/qdocctx.json");
			StringWriter writer = new StringWriter();
			IOUtils.copy(is, writer, "UTF-8");
			cachecontext = writer.toString();
			binitializedcontext = true;
			return cachecontext;
		} catch (Exception e) {
			e.printStackTrace();
			return cachecontext;
		}
	}

	public static Model createModel(){
		Model model = ModelFactory.createDefaultModel();
		model.setNsPrefixes(setPrefixes());
		return model;
	}

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
		prefixes.put("qont", "http://qurator-project.de/ontology/");
		return prefixes;
	}

	public String convertSerialization(String rdf, String syntax_from, RDFFormat syntax_to) throws IOException {
		StringReader reader = new StringReader(rdf);
		Model model = createModel();
		model.read(reader, null, syntax_from);
		return RDFWriter.create()
				.format(syntax_to)
				.source(model)
				.context(contextBuilder())
				.build().asString();
	}

	/**
	 * Gets the JSON-LD options writer
	 */
	public static JsonLDWriteContext contextBuilder()  {
		JsonLdOptions options = new JsonLdOptions();
		options.setProcessingMode("json-ld-1.1");
		options.setOmitGraph(true);
		options.setFrameExpansion(false);
		options.setEmbed(true);
		options.setCompactArrays(true);
		options.setBase("http://lynx-project.eu/res/");
		JsonLDWriteContext ctx = new JsonLDWriteContext();
		ctx.setOptions(options);
		ctx.setFrame(getFrame());
		return ctx;
	}    

	/**
	 * Retrieves the context object for the JSON-LD. The authoritative document
	 * is online (http://lynx-project.eu/doc/jsonld/lynxdocument.json) , but we
	 * load a local version here for efficiency reasons.
	 */
	private static Object getFrame()  {
		String fileName = "frames/lynxdocument.json";
		try {
			return (Object) JsonUtils.fromInputStream(WMDocument.class.getClassLoader().getResourceAsStream(fileName));
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}

	public static String toJSON(List<WMDocument> docs, String outformat) {
		JSONArray array = new JSONArray();
		for (WMDocument qd : docs) {
			String s = toRDF(qd, outformat);
			array.put(s);
		}
		return array.toString();
	}

}
