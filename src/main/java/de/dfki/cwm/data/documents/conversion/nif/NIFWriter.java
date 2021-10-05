package de.dfki.cwm.data.documents.conversion.nif;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

public class NIFWriter {

	/**
	 * Adds a prefix namespace to the JENA Model.
	 * @param model
	 * @param abbrev
	 * @param uri
	 * @return
	 */
	public static Model addPrefixToModel(Model model, String abbrev, String uri){
		model.setNsPrefix(abbrev, uri);
		return model;
	}

	/**
	 * Adds a generic (named-entity-like) annotation to the JENA Model.
	 * @param outModel
	 * @param documentResource
	 * @param inputText
	 * @param documentURI
	 * @param start2
	 * @param end2
	 * @param identRef
	 * @param classRef
	 */
	public static void addSpan(Model outModel, Resource documentResource, String inputText, String documentURI,
			int start2, int end2, String identRef, String classRef) {
        String spanUri = NIFUriHelper.getNifUri(documentURI, start2, end2, "2.1");
        Resource spanAsResource = outModel.createResource(spanUri);
        outModel.add(spanAsResource, RDF.type, NIF.String);
        outModel.add(spanAsResource, RDF.type, NIF.RFC5147String);
        outModel.add(spanAsResource, NIF.anchorOf,
                outModel.createTypedLiteral(inputText.substring(start2, end2), XSDDatatype.XSDstring));
        outModel.add(spanAsResource, NIF.beginIndex,
                outModel.createTypedLiteral(start2, XSDDatatype.XSDnonNegativeInteger));
        outModel.add(spanAsResource, NIF.endIndex, outModel.createTypedLiteral(end2, XSDDatatype.XSDnonNegativeInteger));
        outModel.add(spanAsResource, NIF.referenceContext, documentResource);
        outModel.add(spanAsResource, ITSRDF.taIdentRef, outModel.createResource(identRef));
        outModel.add(spanAsResource, ITSRDF.taConfidence,
        		outModel.createTypedLiteral(0, XSDDatatype.XSDdouble));
        outModel.add(spanAsResource, ITSRDF.taClassRef, outModel.createResource(classRef));
	}
	
	/**
	 * Adds a paragraph annotation to the JENA Model.
	 * @param outModel
	 * @param startIndex
	 * @param endIndex
	 * @param text
	 * @param hasContext
	 * @throws Exception
	 */
	public static void addAnnotationParagraph(Model outModel, int startIndex, int endIndex, String text, String hasContext) {
		String docURI = NIFReader.extractDocumentURI(outModel);
		String spanUri = NIFUriHelper.getNifUri(docURI, startIndex, endIndex, "2.1");
		Resource spanAsResource = outModel.createResource(spanUri);
		outModel.add(spanAsResource, RDF.type, NIF.String);
		outModel.add(spanAsResource, RDF.type, NIF.RFC5147String);
		outModel.add(spanAsResource, RDF.type, NIF.Paragraph);
		// TODO add language to String
		outModel.add(spanAsResource, NIF.anchorOf, outModel.createTypedLiteral(text, XSDDatatype.XSDstring));
		outModel.add(spanAsResource, NIF.beginIndex, outModel.createTypedLiteral(startIndex, XSDDatatype.XSDnonNegativeInteger));
		outModel.add(spanAsResource, NIF.endIndex, outModel.createTypedLiteral(endIndex, XSDDatatype.XSDnonNegativeInteger));
		outModel.add(spanAsResource, NIF.referenceContext, outModel.createResource(NIFReader.extractDocumentWholeURI(outModel)));
		if(hasContext!=null && !hasContext.equalsIgnoreCase("")) {
			outModel.add(spanAsResource, NIF.hasContext, outModel.createResource(hasContext));
		}
	}

	/**
	 * It adds a summary annotation to the context resource in the JENA Model.
	 * @param outModel The JENA Model where the annotation has to be included.
	 * @param summary The value of the summary property.
	 */
	public static void addSummary(Model outModel, String summary){
        Resource documentResource = NIFReader.extractDocumentResourceURI(outModel);
        outModel.add(documentResource, NIF.summary, outModel.createTypedLiteral(summary, XSDDatatype.XSDstring));
	}

	/**
	 * It adds a temporary PDF file path to the context resource in the JENA Model.
	 * @param outModel The JENA Model where the annotation has to be included.
	 * @param path The value of the path property.
	 */
	public static void addTemporaryPDFPath(Model outModel, String path){
        Resource documentResource = NIFReader.extractDocumentResourceURI(outModel);
        outModel.add(documentResource, LYNXNIF.temporaryPDFPath, outModel.createTypedLiteral(path, XSDDatatype.XSDstring));
	}

	/**
	 * Adds a translation to the whole document in the Context of the JENA Model.
	 * @param outModel
	 * @param translation
	 */
	public static void addTranslation(Model outModel, String translation){
        Resource documentResource = NIFReader.extractDocumentResourceURI(outModel);
        outModel.add(documentResource, NIF.translation, outModel.createTypedLiteral(translation, XSDDatatype.XSDstring));
	}

	/**
	 * Adds a temporal annotation to the JENA Model.
	 * @param outModel
	 * @param startIndex
	 * @param endIndex
	 * @param text
	 * @param normalization
	 */
	public static void addTemporalEntity(Model outModel, int startIndex, int endIndex, String text, String normalization) throws Exception {
		String docURI = NIFReader.extractDocumentURI(outModel);
		String spanUri = NIFUriHelper.getNifUri(docURI, startIndex, endIndex, "2.1");
		Resource spanAsResource = outModel.createResource(spanUri);
		outModel.add(spanAsResource, RDF.type, NIF.String);
		outModel.add(spanAsResource, RDF.type, NIF.RFC5147String);
		outModel.add(spanAsResource, NIF.anchorOf, outModel.createTypedLiteral(text, XSDDatatype.XSDstring));
		outModel.add(spanAsResource, NIF.beginIndex, outModel.createTypedLiteral(startIndex, XSDDatatype.XSDnonNegativeInteger));
		outModel.add(spanAsResource, NIF.endIndex, outModel.createTypedLiteral(endIndex, XSDDatatype.XSDnonNegativeInteger));
        outModel.add(spanAsResource, ITSRDF.taClassRef, TIME.temporalEntity);
        outModel.add(spanAsResource, NIF.referenceContext, outModel.createResource(NIFReader.extractDocumentWholeURI(outModel)));
        outModel.add(spanAsResource, NIF.normalizedDate, outModel.createTypedLiteral(normalization, XSDDatatype.XSDstring));
}

	public static void addTemporalEntityFromTimeML(Model outModel, int startIndex, int endIndex, String text, String normalization) throws Exception{
		String docURI = NIFReader.extractDocumentURI(outModel);
		String spanUri = NIFUriHelper.getNifUri(docURI, startIndex, endIndex, "2.1");
		Resource spanAsResource = outModel.createResource(spanUri);
		outModel.add(spanAsResource, RDF.type, NIF.String);
		outModel.add(spanAsResource, RDF.type, NIF.RFC5147String);
		
		outModel.add(spanAsResource, NIF.anchorOf, outModel.createTypedLiteral(text, XSDDatatype.XSDstring));
		outModel.add(spanAsResource, NIF.beginIndex, outModel.createTypedLiteral(startIndex, XSDDatatype.XSDnonNegativeInteger));
		outModel.add(spanAsResource, NIF.endIndex, outModel.createTypedLiteral(endIndex, XSDDatatype.XSDnonNegativeInteger));
		
		// convert normalization to xsd:dateTime notation
		String[] norm = normalization.split("_");
		String intervalStart = norm[0];
		String intervalEnd = norm[1];
		outModel.add(spanAsResource, TIME.intervalStarts, outModel.createTypedLiteral(intervalStart, XSDDatatype.XSDdateTime));
		outModel.add(spanAsResource, TIME.intervalFinishes, outModel.createTypedLiteral(intervalEnd, XSDDatatype.XSDdateTime));
        outModel.add(spanAsResource, ITSRDF.taClassRef, TIME.temporalEntity);
        outModel.add(spanAsResource, NIF.referenceContext, outModel.createResource(NIFReader.extractDocumentWholeURI(outModel)));
        outModel.add(spanAsResource, NIF.normalizedDate, outModel.createTypedLiteral(normalization, XSDDatatype.XSDstring));
	}

	public static void addParagraphEntity(Model outModel, int startIndex, int endIndex) {
		String docURI = NIFReader.extractDocumentURI(outModel);
		String spanUri = NIFUriHelper.getNifUri(docURI, startIndex, endIndex, "2.1");
		Resource paragraphAsResource = outModel.createResource(spanUri);
		outModel.add(paragraphAsResource, RDF.type, NIF.String);
		outModel.add(paragraphAsResource, RDF.type, NIF.RFC5147String);
		outModel.add(paragraphAsResource, NIF.beginIndex, outModel.createTypedLiteral(startIndex, XSDDatatype.XSDnonNegativeInteger));
		outModel.add(paragraphAsResource, NIF.endIndex, outModel.createTypedLiteral(endIndex, XSDDatatype.XSDnonNegativeInteger));
	}
	
	/**
	 * Adds geographical statistics to the JENA Model based on the Geolocations analized and processed.
	 * @param outModel
	 * @param inputText
	 * @param avgLatitude
	 * @param avgLongitude
	 * @param stdDevLatitude
	 * @param stdDevLongitude
	 * @param docURI
	 */
	public static void addGeoStats(Model outModel, String inputText, Double avgLatitude, Double avgLongitude, Double stdDevLatitude, Double stdDevLongitude, String docURI){
        Resource documentResource = NIFReader.extractDocumentResourceURI(outModel);
        outModel.add(documentResource, LYNXNIF.averageLatitude, outModel.createTypedLiteral(avgLatitude, XSDDatatype.XSDdouble));
        outModel.add(documentResource, LYNXNIF.averageLongitude, outModel.createTypedLiteral(avgLongitude, XSDDatatype.XSDdouble));
        outModel.add(documentResource, LYNXNIF.standardDeviationLatitude, outModel.createTypedLiteral(stdDevLatitude, XSDDatatype.XSDdouble));
        outModel.add(documentResource, LYNXNIF.standardDeviationLongitude, outModel.createTypedLiteral(stdDevLongitude, XSDDatatype.XSDdouble));
	}

	/**
	 * Adds a concrete property-value pair to an entity that is already annotated in the JENA Model.
	 * @param nifModel
	 * @param beginIndex
	 * @param endIndex
	 * @param documentURI
	 * @param info
	 * @param prop
	 * @param dataType
	 */
	public static void addEntityProperty(Model nifModel, int beginIndex, int endIndex, String documentURI, String info, Property prop, XSDDatatype dataType) {
		String entityNIFURI = NIFUriHelper.getNifUri(documentURI, beginIndex, endIndex, "2.1");
		Resource entityResource = nifModel.getResource(entityNIFURI);
		nifModel.add(entityResource, prop, nifModel.createTypedLiteral(info, dataType));
	}
	
	/**
	 * Adds the URI of an entity that is already annotated in the JENA Model.
	 * @param nifModel
	 * @param beginIndex
	 * @param endIndex
	 * @param documentURI
	 * @param entURI
	 */
	public static void addEntityURI(Model nifModel, int beginIndex, int endIndex, String documentURI, String entURI){
		String entityNIFURI = NIFUriHelper.getNifUri(documentURI, beginIndex, endIndex, "2.1");
		Resource entityResource = nifModel.getResource(entityNIFURI);
		nifModel.add(entityResource, ITSRDF.taIdentRef, nifModel.createResource(entURI));
	}
	
	/**
	 * Adds an annotation unit. 
	 * NOTE: it is still not clear how or for what that can be used.
	 * @param outModel
	 * @param uri
	 * @param documentURI
	 * @param confidence
	 */
	public static void addAnnotationUnit(Model outModel, String uri, String documentURI, double confidence){
		addPrefixToModel(outModel, "nif-ann", "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-annotation#");
		Resource resource = outModel.getResource(uri);
        Resource anon = outModel.createResource();
//        Bag bag = outModel.createBag();
        anon.addProperty(RDF.type, NIF.AnnotationUnit);
        anon.addProperty(LYNXNIF.isHyperlinkedTo, outModel.createResource(documentURI));
        anon.addLiteral(LYNXNIF.hasHyperlinkedConfidence, confidence);
        outModel.add(resource, NIF.annotationUnit, anon);
	}
	
	/**
	 * Adds an annotation unit. 
	 * NOTE: it is still not clear how or for what that can be used.
	 * @param outModel
	 * @param uri
	 * @param documentURI
	 * @param confidence
	 */
	public static void addSimilarityAnnotationUnit(Model outModel, String documentURI, double confidence){
		addPrefixToModel(outModel, "nif-ann", "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-annotation#");
		String uri = NIFReader.extractDocumentWholeURI(outModel);
		
//		RDFNode obj = null;
//		StmtIterator iterEntities2 = outModel.listStatements(outModel.createResource(uri), LYNXNIF.isSimilarTo, (String) null);
//        while (iterEntities2.hasNext()) {
//			Statement r = iterEntities2.next();
//			obj = r.getObject();
//			System.out.println("FOUND");
//        }
//        Resource anon = null;
//        if(obj==null) {
//            anon = outModel.createResource();
//        }
//        else {
//            anon = obj.asResource();
//        }
		Resource anon = outModel.createResource();
		Resource resource = outModel.getResource(uri);
        anon.addProperty(LYNXNIF.similarityURI, outModel.createResource(documentURI));
        anon.addLiteral(LYNXNIF.similarityScore, confidence);
        outModel.add(resource, LYNXNIF.isSimilarTo, anon);
	}
	
	/**
	 * Adds an annotation unit. 
	 * NOTE: it is still not clear how or for what that can be used.
	 * @param outModel
	 * @param uri
	 * @param documentURI
	 * @param confidence
	 */
	public static void addEntityAnnotationUnit(Model outModel, int startIndex, int endIndex, String text, String taIdentRef, String nerType, double confidence, String annotationType, String serviceName){
//		RDFNode obj = null;
//		StmtIterator iterEntities2 = outModel.listStatements(outModel.createResource(uri), LYNXNIF.isSimilarTo, (String) null);
//        while (iterEntities2.hasNext()) {
//			Statement r = iterEntities2.next();
//			obj = r.getObject();
//			System.out.println("FOUND");
//        }
//        Resource anon = null;
//        if(obj==null) {
//            anon = outModel.createResource();
//        }
//        else {
//            anon = obj.asResource();
//        }
		addPrefixToModel(outModel, "nif-ann", "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-annotation#");
//		String uri = NIFReader.extractDocumentWholeURI(outModel);
		String docURI = NIFReader.extractDocumentURI(outModel);
		String spanUri = NIFUriHelper.getNifUri(docURI, startIndex, endIndex, "2.1");
		Resource spanAsResource = outModel.createResource(spanUri);
		outModel.add(spanAsResource, RDF.type, NIF.String);
		outModel.add(spanAsResource, RDF.type, NIF.RFC5147String);
		outModel.add(spanAsResource, NIF.anchorOf, outModel.createTypedLiteral(text, XSDDatatype.XSDstring));
		outModel.add(spanAsResource, NIF.beginIndex, outModel.createTypedLiteral(startIndex, XSDDatatype.XSDnonNegativeInteger));
		outModel.add(spanAsResource, NIF.endIndex, outModel.createTypedLiteral(endIndex, XSDDatatype.XSDnonNegativeInteger));
		outModel.add(spanAsResource, NIF.referenceContext, NIFReader.extractDocumentResourceURI(outModel));
		
//		if(taIdentRef!=null && !taIdentRef.equalsIgnoreCase("")) {
//			outModel.add(spanAsResource, ITSRDF.taIdentRef, outModel.createResource(taIdentRef));
//		}
//		//outModel.add(spanAsResource, NIF.entity, outModel.createResource(nerType));
//        outModel.add(spanAsResource, ITSRDF.taClassRef, outModel.createResource(nerType));
		
		Resource anon = outModel.createResource();
        anon.addProperty(ITSRDF.taClassRef, outModel.createResource(nerType));
		if(taIdentRef!=null && !taIdentRef.equalsIgnoreCase("")) {
			anon.addProperty(ITSRDF.taIdentRef, outModel.createResource(taIdentRef));
		}
		anon.addLiteral(NIF.author, serviceName);
		anon.addLiteral(NIF.AnnotationUnitType, annotationType);
        anon.addProperty(RDF.type, NIF.AnnotationUnit);
//        anon.addLiteral(LYNXNIF.similarityScore, confidence);
        outModel.add(spanAsResource, NIF.annotationUnit, anon);
	}
	

	/**
	 * Adds an named-entity annotation to the JENA Model. 
	 * @param outModel
	 * @param startIndex
	 * @param endIndex
	 * @param text
	 * @param taIdentRef
	 * @param nerType
	 */
	public static void addAnnotationEntity(Model outModel, int startIndex, int endIndex, String text, String taIdentRef, String nerType){
		String docURI = NIFReader.extractDocumentURI(outModel);
		
		String spanUri = NIFUriHelper.getNifUri(docURI, startIndex, endIndex, "2.1");
		Resource spanAsResource = outModel.createResource(spanUri);
		outModel.add(spanAsResource, RDF.type, NIF.String);
		outModel.add(spanAsResource, RDF.type, NIF.RFC5147String);
		// TODO add language to String
		outModel.add(spanAsResource, NIF.anchorOf, outModel.createTypedLiteral(text, XSDDatatype.XSDstring));
		outModel.add(spanAsResource, NIF.beginIndex, outModel.createTypedLiteral(startIndex, XSDDatatype.XSDnonNegativeInteger));
		outModel.add(spanAsResource, NIF.endIndex, outModel.createTypedLiteral(endIndex, XSDDatatype.XSDnonNegativeInteger));
		if(taIdentRef!=null && !taIdentRef.equalsIgnoreCase("")) {
			outModel.add(spanAsResource, ITSRDF.taIdentRef, outModel.createResource(taIdentRef));
		}
		//outModel.add(spanAsResource, NIF.entity, outModel.createResource(nerType));
        outModel.add(spanAsResource, ITSRDF.taClassRef, outModel.createResource(nerType));
		outModel.add(spanAsResource, NIF.referenceContext, NIFReader.extractDocumentResourceURI(outModel));
	}
	
	/**
	 * 
	 * @param outModel
	 * @param startIndex
	 * @param endIndex
	 * @param text
	 * @param sub
	 * @param act
	 * @param obj
	 * @param thematicRoleSubj
	 * @param thematicRoleObj
	 */
	public static void addAnnotationRelation(Model outModel, int startIndex, int endIndex, String text, String sub, String act, String obj, String thematicRoleSubj, String thematicRoleObj){
		String docURI = NIFReader.extractDocumentURI(outModel);
		String spanUri = NIFUriHelper.getNifUri(docURI, startIndex, endIndex, "2.1");
		Resource spanAsResource = outModel.createResource(spanUri);
		outModel.add(spanAsResource, RDF.type, NIF.String);
		outModel.add(spanAsResource, RDF.type, NIF.RFC5147String);
		outModel.add(spanAsResource, NIF.anchorOf, outModel.createTypedLiteral(text, XSDDatatype.XSDstring));
		outModel.add(spanAsResource, NIF.beginIndex, outModel.createTypedLiteral(startIndex, XSDDatatype.XSDnonNegativeInteger));
		outModel.add(spanAsResource, NIF.endIndex, outModel.createTypedLiteral(endIndex, XSDDatatype.XSDnonNegativeInteger));
		
		outModel.add(spanAsResource, NIF.referenceContext, NIFReader.extractDocumentResourceURI(outModel));
		outModel.add(spanAsResource, ITSRDF.taIdentRef, outModel.createResource(LYNXNIF.relation));
		
		outModel.add(spanAsResource, NIF.relationSubject, outModel.createTypedLiteral(sub,XSDDatatype.XSDstring));
		outModel.add(spanAsResource, NIF.relationAction, outModel.createTypedLiteral(act,XSDDatatype.XSDstring));
		outModel.add(spanAsResource, NIF.relationObject, outModel.createTypedLiteral(obj,XSDDatatype.XSDstring));
		if (thematicRoleSubj != null){
			outModel.add(spanAsResource, NIF.thematicRoleSubj, outModel.createTypedLiteral(thematicRoleSubj,XSDDatatype.XSDstring));
		}
		if (thematicRoleObj != null){
			outModel.add(spanAsResource, NIF.thematicRoleObj, outModel.createTypedLiteral(thematicRoleObj,XSDDatatype.XSDstring));
		}
	}

	/**
	 * Add language annotation to the JENA Model.
	 * @param outModel
	 * @param inputText
	 * @param documentURI
	 * @param language
	 */
	public static void addLanguageAnnotation(Model outModel, String inputText, String documentURI, String language){
		int endTotalText = inputText.codePointCount(0, inputText.length());
		String documentUri = NIFUriHelper.getNifUri(documentURI, 0, endTotalText, "2.1");
        Resource documentResource = outModel.getResource(documentUri);
        outModel.add(documentResource, NIF.language, outModel.createTypedLiteral(language, XSDDatatype.XSDstring));
	}

	/***
	 * FROM HERE ON APPEAR METHOD THAT WHERE USEFULL AT SOME POINT BUT MAYBE ARE NO LONGER NEEDED.
	 */
	
	/** Method to add NIF annotations for source segments in esmt/xlingual
	 * Note these source segments are of type nif:Phrase
	 * @param outModel the current NIF model
	 * @param startIndex beginning of the source phrase
	 * @param endIndex end of the source phrase
	 * @param text surface representation of the phrase
	 * @param documentResource is the original context
	 * @param annIndex index number of the annotation unit
	 */
	public static void addAnnotationMTSource(Model outModel, int startIndex, int endIndex, String text, Resource documentResource, String annIndex){
				//System.out.println("Hello I am inside here\n");
				String docURI = NIFReader.extractDocumentURI(outModel);
				String spanUri = new StringBuilder().append(docURI).append("#char=").append(startIndex).append(',').append(endIndex).toString();

				Resource spanAsResource = outModel.createResource(spanUri);
				outModel.add(spanAsResource, RDF.type, NIF.Phrase);
				//outModel.add(spanAsResource, RDF.type, NIF.RFC5147String);
				outModel.add(spanAsResource, RDF.type, NIF.OffsetString);
				
				outModel.add(spanAsResource, NIF.anchorOf, outModel.createTypedLiteral(text, XSDDatatype.XSDstring));
				outModel.add(spanAsResource, NIF.beginIndex, outModel.createTypedLiteral(startIndex, XSDDatatype.XSDnonNegativeInteger));
				outModel.add(spanAsResource, NIF.endIndex, outModel.createTypedLiteral(endIndex, XSDDatatype.XSDnonNegativeInteger));
				outModel.add(spanAsResource, NIF.referenceContext, documentResource);
				outModel.add(spanAsResource, NIF.annotationUnit, outModel.createResource(annIndex));
				//outModel.add(spanAsResource, ITSRDF.taIdentRef, outModel.createResource(taIdentRef));
				//outModel.add(spanAsResource, NIF.entity, outModel.createResource(nerType));
		        //outModel.add(spanAsResource, ITSRDF.taClassRef, nerType);
		        
	}
	
	/** Method to add NIF annotations for target segments in esmt/xlingual
	 * Note these target segments are annotation units (blank nodes represented by AnonId)
	 * @param outModel the current NIF model
	 * @param text surface representation of the phrase
	 * @param targetLang language of the text
	 * @param documentResource is the original context
	 * @param annIndex index number of the annotation unit
	 */
	public static void addAnnotationMTTarget(Model outModel, String text, String targetLang, Resource documentResource, String annIndex){
				//System.out.println("Hello I am inside here\n");
				//AnonId bnode = new AnonId(annIndex);
				Resource spanAsResource = outModel.createResource(annIndex);
				
				if (!outModel.getNsPrefixMap().containsValue(RDFConstants.itsrdfPrefix)) {
	                outModel.setNsPrefix("itsrdf", RDFConstants.itsrdfPrefix);
	            }

	            Literal literal = outModel.createLiteral(text, targetLang);
	            spanAsResource.addLiteral(outModel.getProperty(RDFConstants.itsrdfPrefix + "target"), literal);
		        
	}

	public static void addAnnotation(Model outModel, Resource documentResource, String documentURI, int annotationId, String annotation) {
        StringBuilder uriBuilder = new StringBuilder();
        uriBuilder.append(documentURI);
        uriBuilder.append(annotation);
        uriBuilder.append(annotationId);

        Resource annotationAsResource = outModel.createResource(uriBuilder.toString());
        outModel.add(annotationAsResource, RDF.type, NIF.Annotation);
        outModel.add(documentResource, NIF.topic, annotationAsResource);
        outModel.add(annotationAsResource, ITSRDF.taIdentRef, outModel.createResource("http://example.dkt.de/meainingUri1"));

        outModel.add(annotationAsResource, NIF.confidence,Double.toString(0), XSDDatatype.XSDstring);
	}
	
	public static void addAnnotation(Model outModel, Resource documentResource, String documentURI, int annotationId) {
		addAnnotation(outModel, documentResource, documentURI, annotationId, "#annotation");
	}
	
	public static void addBabelnetAnnotation(Model outModel, String documentURI, String sense, String language){
		Resource resource = outModel.getResource(documentURI);
        outModel.add(resource, LYNXNIF.babelnetSense, sense+"@"+language);
	}

	public static void addSextupleMAEAnnotation(Model outModel, String documentURI, String person, String origin,
			String destination, Date dTime, Date aTime, String travelMode, int startIndex, int endIndex, String text, float score){
		
		String spanUri = new StringBuilder().append(documentURI).append("#char=").append(startIndex).append(',').append(endIndex).toString();

		Resource resource = outModel.getResource(spanUri);
		outModel.add(resource, RDF.type, LYNXNIF.MovementActionEvent);
		outModel.add(resource, NIF.beginIndex, outModel.createTypedLiteral(startIndex, XSDDatatype.XSDnonNegativeInteger));
		outModel.add(resource, NIF.endIndex, outModel.createTypedLiteral(endIndex, XSDDatatype.XSDnonNegativeInteger));
		if(person!=null){
			outModel.add(resource, LYNXNIF.maePerson, outModel.createTypedLiteral(person, XSDDatatype.XSDstring));
		}
		if(origin!=null){
			outModel.add(resource, LYNXNIF.maeOrigin, outModel.createTypedLiteral(origin, XSDDatatype.XSDstring));
		}
		if(destination!=null){
			outModel.add(resource, LYNXNIF.maeDestination, outModel.createTypedLiteral(destination, XSDDatatype.XSDstring));
		}
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		if(dTime!=null){
			outModel.add(resource, LYNXNIF.maeDepartureTime, outModel.createTypedLiteral(df.format(dTime), XSDDatatype.XSDdateTime));
		}
		if(aTime!=null){
			outModel.add(resource, LYNXNIF.maeArrivalTime, outModel.createTypedLiteral(df.format(aTime), XSDDatatype.XSDdateTime));
		}
		if(travelMode!=null){
			outModel.add(resource, LYNXNIF.maeTravelMode, outModel.createTypedLiteral(travelMode, XSDDatatype.XSDstring));
		}
		if(text!=null){
			outModel.add(resource, NIF.anchorOf, outModel.createTypedLiteral(text, XSDDatatype.XSDstring));
		}
		outModel.add(resource, LYNXNIF.maeScore, outModel.createTypedLiteral(score, XSDDatatype.XSDfloat));
	}

	public static void addTopicModelling(Model outModel, String inputText, String documentURI, String label){
		int endTotalText = inputText.codePointCount(0, inputText.length());
		String documentUri = new StringBuilder().append(documentURI).append("#char=").append("0").append(',').append(endTotalText).toString();
        Resource documentResource = outModel.getResource(documentUri);
        outModel.add(documentResource, NIF.topicModelling, outModel.createTypedLiteral(label, XSDDatatype.XSDstring));
	}
	public static void addDocumentClassification(Model outModel, String inputText, String documentURI, String label){
		int endTotalText = inputText.codePointCount(0, inputText.length());
		String documentUri = new StringBuilder().append(documentURI).append("#char=").append("0").append(',').append(endTotalText).toString();
        Resource documentResource = outModel.getResource(documentUri);
        outModel.add(documentResource, NIF.documentClassification, outModel.createTypedLiteral(label, XSDDatatype.XSDstring));
	}

	public static void addMAETransportationMode(Model outModel, String documentURI, String travelMode, int startIndex, int endIndex){
		
		String spanUri = new StringBuilder().append(documentURI).append("#char=").append(startIndex).append(',').append(endIndex).toString();
		Resource resource = outModel.getResource(spanUri);
		outModel.add(resource, NIF.beginIndex, outModel.createTypedLiteral(startIndex, XSDDatatype.XSDnonNegativeInteger));
		outModel.add(resource, NIF.endIndex, outModel.createTypedLiteral(endIndex, XSDDatatype.XSDnonNegativeInteger));
        outModel.add(resource, LYNXNIF.travelMode, outModel.createTypedLiteral(travelMode, XSDDatatype.XSDstring));
	}

	public static void addMAEMovementVerb(Model outModel, String documentURI, String verb, int startIndex, int endIndex){
		String spanUri = new StringBuilder().append(documentURI).append("#char=").append(startIndex).append(',').append(endIndex).toString();
		Resource resource = outModel.getResource(spanUri);
		outModel.add(resource, RDF.type, LYNXNIF.MovementTrigger);
		outModel.add(resource, NIF.beginIndex, outModel.createTypedLiteral(startIndex, XSDDatatype.XSDnonNegativeInteger));
		outModel.add(resource, NIF.endIndex, outModel.createTypedLiteral(endIndex, XSDDatatype.XSDnonNegativeInteger));
        outModel.add(resource, LYNXNIF.movementVerb, outModel.createTypedLiteral(verb, XSDDatatype.XSDstring));
	}
	
	public static void addLuceneDocumentInformation(Model outModel, String string) {
		String documentUri = NIFReader.extractDocumentWholeURI(outModel);
        Resource documentResource = outModel.getResource(documentUri);
        outModel.add(documentResource, NIF.indexDocumentId, outModel.createTypedLiteral(string, XSDDatatype.XSDstring));
	}

	public static void addMetaDataInformation(Model outModel, String documentURI, String author, String date, String location){
		if(author!=null){
			String spanUriAuthor = new StringBuilder().append(documentURI).append("#char=author").toString();
			Resource resourceAuthor = outModel.getResource(spanUriAuthor);
			outModel.add(resourceAuthor, RDF.type, NIF.String);
			outModel.add(resourceAuthor, RDF.type, NIF.RFC5147String);
			outModel.add(resourceAuthor, NIF.beginIndex, outModel.createTypedLiteral(0, XSDDatatype.XSDnonNegativeInteger));
			outModel.add(resourceAuthor, NIF.endIndex, outModel.createTypedLiteral(0, XSDDatatype.XSDnonNegativeInteger));
	        outModel.add(resourceAuthor, NIF.anchorOf, outModel.createTypedLiteral(author, XSDDatatype.XSDstring));
	        outModel.add(resourceAuthor, ITSRDF.taClassRef, DBO.person);
			outModel.add(resourceAuthor, NIF.referenceContext, outModel.createResource(NIFReader.extractDocumentWholeURI(outModel)));
		}
		if(date!=null){
			String spanUriDate = new StringBuilder().append(documentURI).append("#char=date").toString();
			Resource resourceDate = outModel.getResource(spanUriDate);
			outModel.add(resourceDate, RDF.type, NIF.String);
			outModel.add(resourceDate, RDF.type, NIF.RFC5147String);
			outModel.add(resourceDate, NIF.beginIndex, outModel.createTypedLiteral(0, XSDDatatype.XSDnonNegativeInteger));
			outModel.add(resourceDate, NIF.endIndex, outModel.createTypedLiteral(0, XSDDatatype.XSDnonNegativeInteger));
	        outModel.add(resourceDate, NIF.anchorOf, outModel.createTypedLiteral(date, XSDDatatype.XSDstring));
	        outModel.add(resourceDate, ITSRDF.taClassRef, TIME.temporalEntity);
			outModel.add(resourceDate, NIF.referenceContext, outModel.createResource(NIFReader.extractDocumentWholeURI(outModel)));
		}
		if(location!=null){
			String spanUriLocation = new StringBuilder().append(documentURI).append("#char=location").toString();
			Resource resourceLocation = outModel.getResource(spanUriLocation);
			outModel.add(resourceLocation, RDF.type, NIF.String);
			outModel.add(resourceLocation, RDF.type, NIF.RFC5147String);
			outModel.add(resourceLocation, NIF.beginIndex, outModel.createTypedLiteral(0, XSDDatatype.XSDnonNegativeInteger));
			outModel.add(resourceLocation, NIF.endIndex, outModel.createTypedLiteral(0, XSDDatatype.XSDnonNegativeInteger));
	        outModel.add(resourceLocation, NIF.anchorOf, outModel.createTypedLiteral(location, XSDDatatype.XSDstring));
	        outModel.add(resourceLocation, ITSRDF.taClassRef, DBO.location);
			outModel.add(resourceLocation, NIF.referenceContext, outModel.createResource(NIFReader.extractDocumentWholeURI(outModel)));
		}
	
	}

}
