package de.dfki.slt.datadukt.data.documents.conversion;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.JsonLDWriteContext;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.RDFWriter;
import org.json.JSONObject;

import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.utils.JsonUtils;

import de.dfki.slt.datadukt.data.documents.WMDocument;

public class JSONLDTest {

	
	public static void main(String[] args) throws Exception {
		String syntax = "TURTLE";
		String rdf = "@prefix lkg-res: <http://lkg.lynx-project.eu/res/> .\n" + 
				"@prefix eli:   <http://data.europa.eu/eli/ontology#> .\n" + 
				"@prefix owl:   <http://www.w3.org/2002/07/owl#> .\n" + 
				"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n" + 
				"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n" + 
				"@prefix lkg:   <http://lkg.lynx-project.eu/def/> .\n" + 
				"@prefix skos:  <http://www.w3.org/2004/02/skos/core#> .\n" + 
				"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n" + 
				"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n" + 
				"@prefix dbo:   <http://dbpedia.org/ontology/> .\n" + 
				"@prefix qont:  <http://qurator-projekt.de/ontology/> .\n" + 
				"@prefix nif-ann: <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-annotation#> .\n" + 
				"@prefix dct:   <http://purl.org/dc/terms/> .\n" + 
				"@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" + 
				"@prefix dbr:   <http://dbpedia.org/resource/> .\n" + 
				"@prefix foaf:  <http://xmlns.com/foaf/0.1/> .\n" + 
				"\n" + 
				"<http://qurator-project.de/res/56b56027>\n" + 
				"        a                     nif:Context , qont:QuratorDocument , qont:LabelAnnotation , nif:OffsetBasedString ;\n" + 
				"        qont:has_part          <http://qurator-project.de/res/56b56027#offset_0_22> ;\n" + 
				"        qont:metadata          [ eli:id_local  \"56b56027\" ;\n" + 
				"                                dct:language  \"en\"\n" + 
				"                              ] ;\n" + 
				"        nif:beginIndex        \"0\"^^xsd:nonNegativeInteger ;\n" + 
				"        nif:endIndex          \"47\"^^xsd:nonNegativeInteger ;\n" + 
				"        nif:isString          \"The capital of Germany in 2020 is still Berlin.\" ;\n" + 
//				"        nif:referenceContext  <http://qurator-project.de/res/56b56027> ;\n" + 
//				"        qont:LabelUnit        [ a           qont:Label ;\n" + 
//				"                                itsrdf:taClassRef  \"Society\"\n" + 
//				"                              ] ;\n" + 
//				"        qont:TextAnnotationUnit   [ a                   qont:TextAnnotation ;\n" + 
//				"                                dct:language        \"en\" ;\n" + 
//				"                                qont:generatedText  \"This is a summary of the sentence\"\n" + 
//				"                              ] ;\n" + 
				"        qont:DocumentAnnotations [ a           qont:LabelAnnotation, qont:BaseAnnotation  ;\n" + 
				"        							qont:LabelUnit      [ a           qont:Label, qont:TopicAnnotation ;\n" + 
				"                                							qont:Topic   \"Society\"\n" + 
				"                              							] \n" + 
				"                              ] , \n" + 
				"        					   [ a                   qont:TextAnnotation   ;\n" + 
				"                                dct:language        \"en\" ;\n" + 
				"                                qont:generatedText  \"This is a summary of the sentence\"\n" + 
				"                              ] .\n" + 
				"\n" + 
				"<http://qurator-project.de/res/56b56027#offset_40_46>\n" + 
				"        a                     qont:LabelPositionAnnotation , nif:OffsetBasedString ;\n" + 
				"        nif:anchorOf          \"Berlin\" ;\n" + 
				"        nif:beginIndex        \"40\"^^xsd:nonNegativeInteger ;\n" + 
				"        nif:endIndex          \"46\"^^xsd:nonNegativeInteger ;\n" + 
				"        nif:referenceContext  <http://qurator-project.de/res/56b56027> ;\n" + 
				"        qont:LabelUnit        [ a                  qont:Label ;\n" + 
				"                                itsrdf:taClassRef  dbo:Location ;\n" + 
				"                                itsrdf:taIdentRef  dbr:Berlin\n" + 
				"                              ] .\n" + 
				"\n" + 
				"<http://qurator-project.de/res/56b56027#offset_0_22>\n" + 
				"        a                     qont:QuratorDocumentPart , nif:OffsetBasedString ;\n" + 
				"        qont:metadata          []  ;\n" + 
				"        nif:anchorOf          \"The capital of Germany\" ;\n" + 
				"        nif:beginIndex        \"0\"^^xsd:nonNegativeInteger ;\n" + 
				"        nif:endIndex          \"22\"^^xsd:nonNegativeInteger ;\n" + 
				"        nif:referenceContext  <http://qurator-project.de/res/56b56027> ;\n" + 
				"        dct:source            \"\" ;\n" + 
				"        dct:title             \"title\" .\n" + 
				"";
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		StringReader reader = new StringReader(rdf);
		Model model = ModelFactory.createDefaultModel();
		model.read(reader, null, syntax);
		
//		String fileName = "frames/qdoc.json";
		String fileName = "src/main/resources/frames/qdocfr.json";
		Object o = null;
		try {
			File f = new File(fileName);
			o = (Object) JsonUtils.fromInputStream(new FileInputStream(f));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}

		JsonLdOptions options = new JsonLdOptions();
		options.setProcessingMode("json-ld-1.1");
		options.setOmitGraph(true);
		options.setFrameExpansion(false);
		options.setEmbed(true);
		options.setCompactArrays(true);
		options.setBase("http://qurator-projekt.de/res/");
		JsonLDWriteContext ctx = new JsonLDWriteContext();
		ctx.setOptions(options);
		ctx.setFrame(o);

//		model.write(System.out, "TTL");
		RDFWriter w = RDFWriter.create()
				.format(RDFFormat.JSONLD_FRAME_PRETTY)
//				.format(RDFFormat.JSONLD_FLATTEN_PRETTY)
				.context(ctx)
				.source(model)
				.build();
		w.output(bos);
		String jsonld = new String(bos.toByteArray());
		System.out.println(jsonld);
		WMDocument doc = WMDeserialization.workflowmanagerDocumentFromJSON(jsonld);
//		if (!doc.getId().startsWith("http"))
//			doc.setId("http://qurator-project.de/res/"+doc.getId());
		doc.setAnnotations(WMDeserialization.parseBaseAnnotations(rdf, syntax));
		System.out.println(doc.toJSON(false));
		JSONObject obj2 = new JSONObject(doc.toJSON(false));
		System.out.println(obj2.toString(2));
		
		WMDocument doc3 = WMDeserialization.fromRDF(rdf, syntax);
		JSONObject obj3 = new JSONObject(doc3.toJSON(false));
		System.out.println(obj3.toString(2));
	}
}
