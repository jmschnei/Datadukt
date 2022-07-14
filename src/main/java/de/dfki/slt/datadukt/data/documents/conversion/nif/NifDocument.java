package de.dfki.slt.datadukt.data.documents.conversion.nif;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;

/**
 * @author julianmorenoschneider
 * This class represents a Nif Document as a Java object. Int he first version it will be based on Jena Models, 
 *   for later avoiding using them and only contain java objects.
 */
public class NifDocument {

	private String uri;
	private int beginIndex;
	private int endIndex;

	private String isString;

	private Map<String, NifAnnotation> annotations;

	private Map<String, Object> metadata;

	private Model model;

	public NifDocument() {
		super();
	}

	public NifDocument(Model model) {
		super();
		this.model = ModelFactory.createDefaultModel();
		Map<String, String> map2 = model.getNsPrefixMap();
		for (String s : map2.keySet()) {
			this.model.setNsPrefix(s, map2.get(s));
		}
		
		StmtIterator it = model.listStatements();
		while (it.hasNext()) {
			Statement st = it.next();
			this.model.add(st);
		}
	}

	public NifDocument(String uri, int beginIndex, int endIndex, String isString, Map<String, Object> metadata, Map<String, NifAnnotation> annotations) {
		super();
		this.uri = uri;
		this.beginIndex = beginIndex;
		this.endIndex = endIndex;
		this.isString = isString;
		this.annotations = annotations;
		this.metadata = metadata;
	}

	/*
	 * Convert String to NifDocument (through Jena Model)
	 */
	@SuppressWarnings("deprecation")
	public NifDocument(String rdf, String format, String prefix, String language) throws Exception {
		if(format.equalsIgnoreCase("text/plain")) {
			Model model = ModelFactory.createDefaultModel();
//			model.setNsPrefix("nif", nifPrefix);
//			model.setNsPrefix("xsd", xsdPrefix);
	        model.setNsPrefixes(NIFTransferPrefixMapping.getInstance());

	        int endTotalText = rdf.codePointCount(0, rdf.length());
			String uri = null;
			if(prefix==null) {
				uri = LYNXNIF.getDefaultPrefix();
			}
			else {
				uri = prefix;
			}
			String documentIdentifier = NIFUriHelper.generateDocumentUri(rdf);
			uri = uri + documentIdentifier;
	        String documentUri = NIFUriHelper.getNifUri(uri, 0, endTotalText, "2.1");
	        Resource documentResource = model.createResource(documentUri);
	        model.add(documentResource, RDF.type, NIF.Context);
	        model.add(documentResource, RDF.type, NIF.String);
	        model.add(documentResource, RDF.type, NIF.RFC5147String);
			if (language == null) {
		        model.add(documentResource, NIF.isString, 
		        		model.createTypedLiteral(rdf, XSDDatatype.XSDstring));
			} else {
		        model.add(documentResource, NIF.isString, 
		        		model.createTypedLiteral(rdf, language));
			}
	        model.add(documentResource, NIF.beginIndex,
	                model.createTypedLiteral(new Integer(0), XSDDatatype.XSDnonNegativeInteger));
	        model.add(documentResource, NIF.endIndex,
	                model.createTypedLiteral(new Integer(endTotalText), XSDDatatype.XSDnonNegativeInteger));
			this.model = model;
		}
		else {
			String jenaIdentifier = JenaRDFConversionService.getJenaType(format);
			if (jenaIdentifier == null) {
				throw new Exception("unsupported format: " + format);
			}
			Model model = ModelFactory.createDefaultModel();
			StringReader reader = new StringReader(rdf);
			model.read(reader, null, jenaIdentifier);
			this.model = model;
		}
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
		isString = NIFReader.extractIsString(model);
		endIndex = NIFReader.extractEndTotalText(model, "2.0");
		beginIndex = 0;
		annotations = NIFReader.extractAnnotations(model);
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public int getBeginIndex() {
		return beginIndex;
	}

	public void setBeginIndex(int beginIndex) {
		this.beginIndex = beginIndex;
	}

	public int getEndIndex() {
		return endIndex;
	}

	public void setEndIndex(int endIndex) {
		this.endIndex = endIndex;
	}

	public String getIsString() {
		return isString;
	}

	public void setIsString(String isString) {
		this.isString = isString;
	}

	public Map<String, NifAnnotation> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(Map<String, NifAnnotation> annotations) {
		this.annotations = annotations;
	}

	
	public Map<String, Object> getMetadata() {
		return metadata;
	}

	public void setMetadata(Map<String, Object> metadata) {
		this.metadata = metadata;
	}

	/**
	 * Adds a generic (named-entity-like) annotation to the NIF Document.
	 * @param outModel
	 * @param documentResource
	 * @param inputText
	 * @param documentURI
	 * @param start2
	 * @param end2
	 * @param identRef
	 * @param classRef
	 */
	public void addAnnotation(String anchorOf,
								int start, 
								int end, 
								String identRef, 
								String classRef) {
		String documentWholeURI = NIFReader.extractDocumentWholeURI(model);
		Resource documentResource = model.getResource(documentWholeURI);
		String documentURI = NIFReader.extractDocumentURI(model);
		String spanUri = NIFUriHelper.getNifUri(documentURI, start, end, "2.1");
        Resource spanAsResource = model.createResource(spanUri);
        model.add(spanAsResource, RDF.type, NIF.String);
        model.add(spanAsResource, RDF.type, NIF.RFC5147String);
        model.add(spanAsResource, NIF.anchorOf,
                  model.createTypedLiteral(anchorOf, XSDDatatype.XSDstring));
        model.add(spanAsResource, NIF.beginIndex,
                model.createTypedLiteral(start, XSDDatatype.XSDnonNegativeInteger));
        model.add(spanAsResource, NIF.endIndex, model.createTypedLiteral(end, XSDDatatype.XSDnonNegativeInteger));
        model.add(spanAsResource, NIF.referenceContext, documentResource);
        if(identRef!=null) {
            model.add(spanAsResource, ITSRDF.taIdentRef, model.createResource(identRef));
        }
        if(classRef!=null) {
        	model.add(spanAsResource, ITSRDF.taClassRef, model.createResource(classRef));
        }
        
	}

	/**
	 * Adds a generic (named-entity-like) annotation to the NIF Document.
	 * @param outModel
	 * @param documentResource
	 * @param inputText
	 * @param documentURI
	 * @param start2
	 * @param end2
	 * @param identRef
	 * @param classRef
	 */
	public void addAnnotationUnit(String anchorOf,
								int start, 
								int end, 
								String identRef, 
								String classRef,
								Float confidence) {
		String documentWholeURI = NIFReader.extractDocumentWholeURI(model);
		Resource documentResource = model.getResource(documentWholeURI);
		String documentURI = NIFReader.extractDocumentURI(model);
		String spanUri = NIFUriHelper.getNifUri(documentURI, start, end, "2.1");

		Resource anon = model.createResource();
		Resource spanAsResource = model.createResource(spanUri);
        model.add(spanAsResource, RDF.type, NIF.String);
        model.add(spanAsResource, RDF.type, NIF.RFC5147String);
        model.add(spanAsResource, NIF.anchorOf,
        		model.createTypedLiteral(anchorOf, XSDDatatype.XSDstring));
        model.add(spanAsResource, NIF.beginIndex,
                model.createTypedLiteral(start, XSDDatatype.XSDnonNegativeInteger));
        model.add(spanAsResource, NIF.endIndex, model.createTypedLiteral(end, XSDDatatype.XSDnonNegativeInteger));
        model.add(spanAsResource, NIF.referenceContext, documentResource);
        if(identRef!=null && !identRef.equalsIgnoreCase("")) {
            //model.add(spanAsResource, ITSRDF.taIdentRef, model.createResource(identRef));
            anon.addProperty(ITSRDF.taIdentRef, model.createResource(identRef));
        }
        if(classRef!=null && !classRef.equalsIgnoreCase("")) {
        	//model.add(spanAsResource, ITSRDF.taClassRef, model.createResource(classRef));
            anon.addProperty(ITSRDF.taClassRef, model.createResource(classRef));
        }
        if(confidence!=null) {
            anon.addLiteral(LYNXNIF.confidence, confidence.floatValue());
        }
        model.add(spanAsResource, LYNXNIF.entity, anon);
	}


	/**
	 * Adds an annotation unit. 
	 * NOTE: it is still not clear how or for what that can be used.
	 * @param outModel
	 * @param uri
	 * @param documentURI
	 * @param confidence
	 */
	public void addSimilarityAnnotationUnit(String documentURI, double confidence){
		NIFWriter.addSimilarityAnnotationUnit(this.model, documentURI, confidence);
	}

	/**
	 * Gets all the Similarity Annotation Units. 
	 */
	public HashMap<String, Double> getSimilarityAnnotationUnit(){
		return NIFReader.getSimilarityAnnotationUnits(this.model);
	}

	/**
	 * This method allows the combination of the NIF documents, with the requirement that they are based on the same source document.
	 * @param doc2
	 * @throws Exception
	 */
	public void combineDocument(NifDocument doc2) throws Exception {
		if(!isString.equals(doc2.getIsString())) {
			throw new Exception("The content of both NIF Documents (in isString) is different. They can not be combined.");
		}
		boolean sameURI = false;
		if(uri.equals(doc2.getUri())) {
//			String content = NIFConverter.serializeRDF(inputModel2, "text/turtle");
//			content = content.replaceAll(documentURI2, documentURI1);
//			inputModel2 = NIFConverter.unserializeRDF(content, "text/turtle");
			sameURI = true;
		}
		if(sameURI) {
			
		}
		/**
		 * Combine the two documents
		 */
		
		/**
		 * The next part combines the metadata of the document annotation
		 */
		//TODO
		/**
		 * The next part combines the annotations
		 */
		Map<String,NifAnnotation> annotations2 = doc2.getAnnotations();
		Set<String> keys_doc2 = annotations2.keySet();
		for (String key_doc2 : keys_doc2) {
			if(annotations.containsKey(key_doc2)) {
				
				
				
			}
			else {
				annotations.put(key_doc2, annotations2.get(key_doc2));
			}
		}
		
	}

	public void substractDocument(NifDocument nd2) {
		model.remove(nd2.getModel());
	}

	static String defaultPrefix = LYNXNIF.getDefaultPrefix();

	static JenaRDFConversionService jena = new JenaRDFConversionService();

	/*
	 * Convert NIF Document (through Jena Model) to String
	 */
	public String serialize(String format) throws Exception {
		String jenaIdentifier = JenaRDFConversionService.getJenaType(format);
		if (jenaIdentifier == null) {
			throw new Exception("unsupported format: " + format);
		}
		StringWriter writer = new StringWriter();
		model.write(writer, jenaIdentifier);
		writer.close();
		return writer.toString();
	}

	/*
	 * Convert String to NIFDocument (through Jena Model)
	 */
	public NifDocument unserializeRDF(String rdf, String format) throws Exception {
		if(format.equalsIgnoreCase("text/plain")) {
			return plaintextToNIF(rdf, null, "2.1", null);
		}
		String jenaIdentifier = JenaRDFConversionService.getJenaType(format);
		if (jenaIdentifier == null) {
			throw new Exception("unsupported format: " + format);
		}
		Model model = ModelFactory.createDefaultModel();
		StringReader reader = new StringReader(rdf);
		model.read(reader, null, jenaIdentifier);
		NifDocument doc = new NifDocument(model);
		return doc;
	}
	
	/*
	 * Convert String to NifDocument (through Jena Model)
	 */
	public NifDocument unserializeRDF(String rdf, String format, String prefix) throws Exception {
		if(format.equalsIgnoreCase("text/plain")) {
			return plaintextToNIF(rdf, null, "2.1", prefix);
		}
		String jenaIdentifier = JenaRDFConversionService.getJenaType(format);
		if (jenaIdentifier == null) {
			throw new Exception("unsupported format: " + format);
		}
		Model model = ModelFactory.createDefaultModel();
		StringReader reader = new StringReader(rdf);
		model.read(reader, null, jenaIdentifier);
		
		NifDocument doc = new NifDocument(model);
		return doc;
	}
	
	@SuppressWarnings("deprecation")
	public NifDocument plaintextToNIF(String inputText, String language, String nifVersion, String prefix) {
		Model model = ModelFactory.createDefaultModel();
//		model.setNsPrefix("nif", nifPrefix);
//		model.setNsPrefix("xsd", xsdPrefix);
        model.setNsPrefixes(NIFTransferPrefixMapping.getInstance());

        int endTotalText = inputText.codePointCount(0, inputText.length());
		String uri = null;
		if(prefix==null) {
			uri = LYNXNIF.getDefaultPrefix();
		}
		else {
			uri = prefix;
		}
		String documentIdentifier = NIFUriHelper.generateDocumentUri(inputText);
		uri = uri + documentIdentifier;
        
//		String documentUri = NIFUriHelper.getNifUri(uri, 0, endTotalText, nifVersion);
		String documentUri = uri;

        Resource documentResource = model.createResource(documentUri);
        model.add(documentResource, RDF.type, NIF.Context);
        model.add(documentResource, RDF.type, NIF.String);
        model.add(documentResource, RDF.type, NIF.RFC5147String);
		if (language == null) {
	        model.add(documentResource, NIF.isString, 
	        		model.createTypedLiteral(inputText, XSDDatatype.XSDstring));
		} else {
	        model.add(documentResource, NIF.isString, 
	        		model.createTypedLiteral(inputText, XSDDatatype.XSDstring));
//	        		model.createLiteral(inputText, language));
		}
        model.add(documentResource, NIF.beginIndex,
                model.createTypedLiteral(new Integer(0), XSDDatatype.XSDnonNegativeInteger));
        model.add(documentResource, NIF.endIndex,
                model.createTypedLiteral(new Integer(endTotalText), XSDDatatype.XSDnonNegativeInteger));
                
        //Adding metadata: 
        /**
         * lkg:metadata  [ eli:first_date_entry_in_force  "19890803" ;
                        eli:jurisdiction               "es" ;
                        eli:type_document              "Real Decreto" ;
                        eli:version                    "con" ;
                        eli:version_date               "19890802" ;
                        lkg:hasAuthority               "Ministerio para las Administraciones Públicas" ;
                        dct:language                   "es" ;
                        dct:subject                    "Comunidades de Usuarios de Aguas" , "Abastecimiento de aguas" , "Aguas" , "Ministerio de Obras Públicas y Urbanismo" , "Confederaciones Hidrográficas" , "Comunidades de Regantes" , "Comías e Aguas" ;
                        dct:title                      "Real Decreto 984/1989, de 28 de julio, por el que se determina la estructura orgánica dependiente de la Presidencia de las Confederaciones Hidrográficas." ;
                        dct:uri                        [ a                 lkg:EliURI ;
                                                         lkg:externalLink  "http://lkg.lynx-project.eu/link/es/rd/1989/18569/con/es"
                                                       ] ;
                        dct:uri                        [ a                 lkg:LocalURI ;
                                                         lkg:externalLink  "http://lkg.lynx-project.eu/link/BOE-A-1989-18569"
                                                       ]
                      ] .
         */
		Resource anon = model.createResource();
        //anon.addLiteral(ResourceFactory.createProperty("http://data.europa.eu/eli/ontology#", "jurisdiction"), "ES");
        anon.addLiteral(ResourceFactory.createProperty("http://purl.org/dc/terms/", "author"), "DFKI NIF Generation");
        model.add(documentResource, ResourceFactory.createProperty("http://lkg.lynx-project.eu/def/", "metadata"), anon);
        
        NifDocument doc = new NifDocument(model);
		return doc;
	}


}
