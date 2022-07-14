package de.dfki.slt.datadukt.data.documents.conversion.nif;

import static de.dfki.slt.datadukt.data.documents.conversion.nif.RDFConstants.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;

public class NIFReader {

	/**
	 * This method extracts the first (in case there are more than one) isString, i.e., plain text, from the NIF Document.
	 * @param model Jena Model containing the NIF Documents
	 * @return The first isString object content (plain text of the document).
	 */
	public static String extractIsString(Model model){
		Resource context = model.getResource(nifPrefix+NIF_CONTEXT_TYPE);
		Property isString = model.getProperty(nifPrefix+IS_STRING);
		StmtIterator iter = model.listStatements(null, RDF.type, context);
		while (iter.hasNext()) {
			Resource contextRes = iter.nextStatement().getSubject();
			Statement isStringStm = contextRes.getProperty(isString);
			if (isStringStm != null) {
				return isStringStm.getString();
			}
		}
		return null;
	}
	
	public static String extractRDFValue(Model model){
		Resource context = model.getResource(nifPrefix+NIF_CONTEXT_TYPE);
		
		//TODO
		Property isString = model.getProperty(nifPrefix+IS_STRING);

		
		StmtIterator iter = model.listStatements(null, RDF.type, context);
		while (iter.hasNext()) {
			Resource contextRes = iter.nextStatement().getSubject();
			Statement isStringStm = contextRes.getProperty(isString);
			if (isStringStm != null) {
				return isStringStm.getString();
			}
		}
		return null;
	}
	
	public static String extractDocumentURI(Model nifModel){
		StmtIterator iter = nifModel.listStatements(null, RDF.type, nifModel.getResource(NIF.Context.getURI()));
		while(iter.hasNext()){
			Resource contextRes = iter.nextStatement().getSubject();
			//System.out.println(contextRes.getURI());
			String uri = contextRes.getURI();
			if(uri.contains("#")) {
				return uri.substring(0, uri.indexOf('#'));
			}
			else if(uri.contains("_offset")) {
				return uri.substring(0, uri.indexOf("_offset"));
			}
			else {
				return uri;
			}
		}
		return null;//		throw new BadRequestException("No context/document found.");
	}

	public static String extractDocumentWholeURI(Model nifModel){
		StmtIterator iter = nifModel.listStatements(null, RDF.type, nifModel.getResource(NIF.Context.getURI()));
		while(iter.hasNext()){
			Resource contextRes = iter.nextStatement().getSubject();
			//System.out.println(contextRes.getURI());
			String uri = contextRes.getURI();
			return uri;
		}
		return null;//		throw new BadRequestException("No context/document found.");
	}
	
	public static Resource extractDocumentResourceURI(Model nifModel){
		StmtIterator iter = nifModel.listStatements(null, RDF.type, nifModel.getResource(NIF.Context.getURI()));
		while(iter.hasNext()){
			Resource contextRes = iter.nextStatement().getSubject();
			return contextRes;
		}
		return null;
	}
	
	public static int extractEndTotalText(Model nifModel, String nifVersion){
		StmtIterator iter = nifModel.listStatements(null, RDF.type, nifModel.getResource(NIF.Context.getURI()));
		while(iter.hasNext()){
			Resource contextRes = iter.nextStatement().getSubject();
			String uri = contextRes.getURI();
			String sEnd =null;
			if(nifVersion.equals(RDFConstants.nifVersion2_0)) {
				sEnd = uri.substring(uri.lastIndexOf(',')+1);
			}
			else {
				sEnd = uri.substring(uri.lastIndexOf('_')+1);
			}
			int i = Integer.parseInt(sEnd);
			return i;
		}
		return -1;
	}

	/**
	 * This method returns the PATH of the original file.
	 * NOTE: It is only valid if the path of the origianl file will be stored in the NIF Document.
	 * @param nifModel JENA Model containing the NIF Document.
	 * @return The path of the original file.
	 */
	public static String extractDocumentPath(Model nifModel){
		StmtIterator iter = nifModel.listStatements(null, RDF.type, nifModel.getResource(NIF.Context.getURI()));
		while(iter.hasNext()){
			Resource contextRes = iter.nextStatement().getSubject();
			Statement st = contextRes.getProperty(LYNXNIF.DocumentPath);
			if(st!=null){
	//			System.out.println(contextRes.getURI());
				String uri = st.getObject().asResource().getURI();
				return uri;
			}
		}
		return null;//		throw new BadRequestException("No document path found.");
	}
	
	
	/**
	 * It extracts the summary annotation of the context resource in the JENA Model.
	 * @param outModel The JENA Model where the annotation is included.
	 */
	public static String extractSummary(Model nifModel){
		StmtIterator iter = nifModel.listStatements(null, RDF.type, nifModel.getResource(NIF.Context.getURI()));
		while(iter.hasNext()){
			Resource contextRes = iter.nextStatement().getSubject();
			Statement st = contextRes.getProperty(NIF.summary);
			if(st!=null){
				String uri = st.getObject().asLiteral().getString();
				return uri;
			}
		}
		return null;//		throw new BadRequestException("No document path found.");
	}

	/**
	 * It adds a temporary PDF file path to the context resource in the JENA Model.
	 * @param outModel The JENA Model where the annotation has to be included.
	 * @param path The value of the path property.
	 */
	public static String extractTemporaryPDFPath(Model nifModel){
		StmtIterator iter = nifModel.listStatements(null, RDF.type, nifModel.getResource(NIF.Context.getURI()));
		while(iter.hasNext()){
			Resource contextRes = iter.nextStatement().getSubject();
			Statement st = contextRes.getProperty(LYNXNIF.temporaryPDFPath);
			if(st!=null){
				String path = st.getObject().asLiteral().getString();
				return path;
			}
		}
		return null;//		throw new BadRequestException("No document path found.");
	}

	/**
	 * Gets similarity annotation units. 
	 * NOTE: it is still not clear how or for what that can be used.
	 * @param outModel
	 * @param uri
	 * @param documentURI
	 * @param confidence
	 */
	public static HashMap<String, Double> getSimilarityAnnotationUnits(Model outModel){
		String uri = NIFReader.extractDocumentWholeURI(outModel);
		HashMap<String, Double> map = new HashMap<String, Double>();
		RDFNode obj = null;
		StmtIterator iterEntities2 = outModel.listStatements(outModel.createResource(uri), LYNXNIF.isSimilarTo, (String) null);
        while (iterEntities2.hasNext()) {
			Statement r = iterEntities2.next();
			obj = r.getObject();
            Resource anon = obj.asResource();
            
            String simUri = anon.getProperty(LYNXNIF.similarityURI).getObject().asResource().getURI();
            Double simScore = anon.getProperty(LYNXNIF.similarityScore).getObject().asLiteral().getDouble();
//            System.out.println(simUri + "--" + simScore);
            map.put(simUri, simScore);
        }
        return map;
	}

	/**
	 * This method returns all the annotations included in the NIF Document in the format of LynxAnnotation.
	 * @param nifModel JENA Model containing the NIF Document.
	 * @return A Hashmap containing Subjects as keys and LynxAnnotations as values.
	 */
	public static Map<String,NifAnnotation> extractAnnotations(Model nifModel){
		Map<String,NifAnnotation> list = new HashMap<String,NifAnnotation>();
				
        //ResIterator iterEntities = nifModel.listSubjectsWithProperty(NIF.entity);
		ResIterator iterEntities = nifModel.listSubjectsWithProperty(ITSRDF.taClassRef);
        while (iterEntities.hasNext()) {
        	Map<String,Object> map = new HashMap<String,Object>();
            Resource r = iterEntities.nextResource();

            String entityURI = r.getURI();
//System.out.println(entityURI);            
            StmtIterator iter2 = r.listProperties();
            while (iter2.hasNext()) {
				Statement st2 = iter2.next();
				String predicate =st2.getPredicate().getURI(); 
				String object = null;
				if(st2.getObject().isResource()){
					object = st2.getObject().asResource().getURI();
				}
				else{
					object = st2.getObject().asLiteral().getString();
				}
				map.put(predicate,object);
			}
            if(!map.isEmpty()){
            	NifAnnotation annotation = new NifAnnotation(entityURI,map);
                list.put(entityURI,annotation);
            }
        }
		ResIterator iterEntities2 = nifModel.listSubjectsWithProperty(RDF.type,LYNXNIF.MovementTrigger);
        while (iterEntities2.hasNext()) {
        	Map<String,Object> map = new HashMap<String,Object>();
            Resource r = iterEntities2.nextResource();
            String entityURI = r.getURI();
            StmtIterator iter2 = r.listProperties();
            while (iter2.hasNext()) {
				Statement st2 = iter2.next();
				String predicate =st2.getPredicate().getURI(); 
				String object = null;
				if(st2.getObject().isResource()){
					object = st2.getObject().asResource().getURI();
				}
				else{
					object = st2.getObject().asLiteral().getString();
				}
				map.put(predicate,object);
			}
            if(!map.isEmpty()){
            	NifAnnotation annotation = new NifAnnotation(entityURI,map);
                list.put(entityURI,annotation);
            }
        }
		ResIterator iterEntities3 = nifModel.listSubjectsWithProperty(LYNXNIF.travelMode);
        while (iterEntities3.hasNext()) {
        	Map<String,Object> map = new HashMap<String,Object>();
            Resource r = iterEntities3.nextResource();
            String entityURI = r.getURI();
            StmtIterator iter2 = r.listProperties();
            while (iter2.hasNext()) {
				Statement st2 = iter2.next();
				String predicate =st2.getPredicate().getURI(); 
				String object = null;
				if(st2.getObject().isResource()){
					object = st2.getObject().asResource().getURI();
				}
				else{
					object = st2.getObject().asLiteral().getString();
				}
				map.put(predicate,object);
			}
            if(!map.isEmpty()){
            	NifAnnotation annotation = new NifAnnotation(entityURI,map);
                list.put(entityURI,annotation);
            }
        }
        if(list.isEmpty()){
        	return null;
        }
		return list;
	}

	/**
	 * It returns all the entities annotated in the NIF Document in the form of a List of String[].
	 * @param nifModel JENA Model containing the NIF Document.
	 * @return A List containing String[] representing entities and properties.
	 */
	@Deprecated
	public static List<String[]> extractEntities(Model nifModel){
		List<String[]> list = new LinkedList<String[]>();
				
        //ResIterator iterEntities = nifModel.listSubjectsWithProperty(NIF.entity);
		ResIterator iterEntities = nifModel.listSubjectsWithProperty(ITSRDF.taClassRef);
        while (iterEntities.hasNext()) {
            Resource r = iterEntities.nextResource();
            //Statement st = r.getProperty(NIF.entity);
            Statement st = r.getProperty(ITSRDF.taClassRef);
            String stringSt = ( st!=null ) ? st.getObject().asResource().getURI() : null;
//            System.out.println("1."+st.getObject().asResource().getURI());
            Statement st2 = r.getProperty(NIF.anchorOf);
            String stringSt2 = ( st2!=null ) ? st2.getLiteral().getString() : null;
//            System.out.println("7."+st2.getLiteral().getString());
            Statement st3 = r.getProperty(ITSRDF.taIdentRef);
            String stringSt3 = ( st3!=null ) ? st3.getObject().asResource().getURI() : null;
            String[] information = {stringSt3,stringSt2,stringSt};
            list.add(information);
        }
        if(list.isEmpty()){
        	return null;
        }
		return list;
	}
	
	/***
	 * FROM HERE ON APPEAR METHOD THAT WHERE USEFULL AT SOME POINT BUT MAYBE ARE NO LONGER NEEDED.
	 */

	public static String extractITSRDFTarget(Model nifModel){
        StmtIterator iter = nifModel.listStatements(null, RDF.type, NIF.Context);
        boolean textFound = false;
        while (!textFound && iter.hasNext()) {
        	Statement st = iter.next();
            Resource contextRes = st.getSubject();
            Statement isStringStm = contextRes.getProperty(ITSRDF.target);
            if (isStringStm != null) {
                return isStringStm.getObject().asLiteral().getString();
            }
        }
        return null;
	}
	
	public static String extractITSRDFTargetLanguage(Model nifModel){
        StmtIterator iter = nifModel.listStatements(null, RDF.type, NIF.Context);
        boolean textFound = false;
        while (!textFound && iter.hasNext()) {
        	Statement st = iter.next();
            Resource contextRes = st.getSubject();
            Statement isStringStm = contextRes.getProperty(ITSRDF.target);
            if (isStringStm != null) {
            	Literal l = isStringStm.getObject().asLiteral();
            	return l.getLanguage();
            }
        }
        return null;
	}

	public static String extractMeanDateRange(Model nifModel){
		String date[] = new String[2];
        StmtIterator iter = nifModel.listStatements(null, RDF.type, NIF.Context);
        boolean textFound = false;
        while (!textFound && iter.hasNext()) {
        	Statement st = iter.next();
            Resource contextRes = st.getSubject();
            Statement isStringStm = contextRes.getProperty(LYNXNIF.meanDateStart);
            if (isStringStm != null) {
                date[0] = isStringStm.getObject().asLiteral().getString();
            }
            Statement isStringStm2 = contextRes.getProperty(LYNXNIF.meanDateEnd);
            if (isStringStm2 != null) {
                date[1] = isStringStm2.getObject().asLiteral().getString();
            }
        }
		return date[0]+"_"+date[1];
	}
		
	public static String extractMeanPositionRange(Model nifModel){
		String position[] = new String[2];
        StmtIterator iter = nifModel.listStatements(null, RDF.type, NIF.Context);
        boolean textFound = false;
        while (!textFound && iter.hasNext()) {
        	Statement st = iter.next();
            Resource contextRes = st.getSubject();
            Statement isStringStm = contextRes.getProperty(LYNXNIF.averageLatitude);
            if (isStringStm != null) {
                position[0] = isStringStm.getObject().asLiteral().getString();
            }
            Statement isStringStm2 = contextRes.getProperty(LYNXNIF.averageLongitude);
            if (isStringStm2 != null) {
                position[1] = isStringStm2.getObject().asLiteral().getString();
            }
        }
		return position[0]+"_"+position[1];
	}
	
	public static List<String[]> extractSameAsAnnotations(Model nifModel){
		
		List<String[]> list = new LinkedList<String[]>();
		ResIterator iterEntities = nifModel.listSubjectsWithProperty(OWL.sameAs);
		while (iterEntities.hasNext()) {
			Resource r = iterEntities.nextResource();
			Statement st = r.getProperty(OWL.sameAs);
			RDFNode sameAsValue = st.getObject();
			Property referee = nifModel.getProperty(sameAsValue.toString().split("\\^\\^")[0]); // TODO: find a better way for this than this ugly string splitting. There must a beat, built in jena way to get just the referencecontest without xsd:string stuff
			Statement stRefAnchor = referee.getProperty(NIF.anchorOf);
			String stRefAnchorString = ( stRefAnchor!=null ) ? stRefAnchor.getLiteral().getString() : null;
			Statement stRefTaIdentRef = referee.getProperty(ITSRDF.taIdentRef);
			String stRefTaIdentRefString = null;
			if (stRefTaIdentRef != null){
				stRefTaIdentRefString = stRefTaIdentRef.getObject().toString();
			}
			Statement stRefClassRef = referee.getProperty(ITSRDF.taClassRef);
			String stRefClassRefString = stRefClassRef.getObject().toString();
            Statement st4 = r.getProperty(NIF.beginIndex);
            String stringSt4 = ( st4!=null ) ? st4.getLiteral().getString() : null;
            Statement st5 = r.getProperty(NIF.endIndex);
            String stringSt5 = ( st5!=null ) ? st5.getLiteral().getString() : null;
            Statement st6 = r.getProperty(OWL.sameAs);
            String entityURI = st6.getLiteral().getString();
            String[] information = {stRefTaIdentRefString,stRefAnchorString,stRefClassRefString,stringSt4,stringSt5, entityURI};
            list.add(information);
        }
        if(list.isEmpty()){
        	return null;
        }
		return list;
	}
	
	public static String extractTaIdentRefWithEntityURI(Model nifModel, String entityURI){
		
		Resource r = ResourceFactory.createResource(entityURI);
		NodeIterator nodes = nifModel.listObjectsOfProperty(r, ITSRDF.taIdentRef);
		String taIdentRef = null;
		while(nodes.hasNext()){
			RDFNode node = nodes.next();
		   taIdentRef = node.asResource().getURI();
		}
		return taIdentRef;
	}
	
	public static Map<String,Map<String,String>> extractEventsExtended(Model nifModel){
		Map<String,Map<String,String>> list = new HashMap<String,Map<String,String>>();

		//TODO Define how wvents are going to be extracted from NIF (and indeed, annotated in NIF).
		
        //ResIterator iterEntities = nifModel.listSubjectsWithProperty(NIF.entity);
		ResIterator iterEntities = nifModel.listSubjectsWithProperty(ITSRDF.taClassRef);
        while (iterEntities.hasNext()) {
    		Map<String,String> map = new HashMap<String,String>();
            Resource r = iterEntities.nextResource();

            String entityURI = r.getURI();
            
            StmtIterator iter2 = r.listProperties();
            while (iter2.hasNext()) {
				Statement st2 = iter2.next();
				String predicate =st2.getPredicate().getURI(); 
				String object = null;
				if(st2.getObject().isResource()){
					object = st2.getObject().asResource().getURI();
				}
				else{
					object = st2.getObject().asLiteral().getString();
				}
				map.put(predicate,object);
			}
            if(!map.isEmpty()){
                list.put(entityURI,map);
            }
        }
        if(list.isEmpty()){
        	return null;
        }
		return list;
	}

	public static Map<String,Map<String,String>> extractEntitiesExtended(Model nifModel){
		Map<String,Map<String,String>> list = new HashMap<String,Map<String,String>>();
				
        //ResIterator iterEntities = nifModel.listSubjectsWithProperty(NIF.entity);
		ResIterator iterEntities = nifModel.listSubjectsWithProperty(ITSRDF.taClassRef);
        while (iterEntities.hasNext()) {
    		Map<String,String> map = new HashMap<String,String>();
            Resource r = iterEntities.nextResource();

            String entityURI = r.getURI();
            
            StmtIterator iter2 = r.listProperties();
            while (iter2.hasNext()) {
				Statement st2 = iter2.next();
				String predicate =st2.getPredicate().getURI(); 
				String object = null;
				if(st2.getObject().isResource()){
					object = st2.getObject().asResource().getURI();
				}
				else{
					object = st2.getObject().asLiteral().getString();
				}
				map.put(predicate,object);
			}
            if(!map.isEmpty()){
                list.put(entityURI,map);
            }
        }
        if(list.isEmpty()){
        	return null;
        }
		return list;
	}

	public static Map<String,Map<String,String>> extractTemporalEntitiesExtended(Model nifModel){
		Map<String,Map<String,String>> list = new HashMap<String,Map<String,String>>();
		ResIterator iterEntities = nifModel.listSubjectsWithProperty(ITSRDF.taClassRef);
        while (iterEntities.hasNext()) {
    		Map<String,String> map = new HashMap<String,String>();
            Resource r = iterEntities.nextResource();

            String entityURI = r.getURI();
            
            StmtIterator iter2 = r.listProperties();
            boolean isTemporalExpression=false;
            while (iter2.hasNext()) {
				Statement st2 = iter2.next();
				String predicate =st2.getPredicate().getURI(); 
				String object = null;
				if(st2.getObject().isResource()){
					object = st2.getObject().asResource().getURI();
				}
				else{
					object = st2.getObject().asLiteral().getString();
				}
				if(predicate.equalsIgnoreCase(ITSRDF.taClassRef.getURI()) && object.equalsIgnoreCase(TIME.temporalEntity.getURI())){
					isTemporalExpression=true;
				}
				map.put(predicate,object);
			}
            if(!map.isEmpty() && isTemporalExpression){
                list.put(entityURI,map);
            }
        }
        if(list.isEmpty()){
        	return null;
        }
		return list;
	}

	public static Map<String,Map<String,String>> extractNonTemporalEntitiesExtended(Model nifModel){
		Map<String,Map<String,String>> list = new HashMap<String,Map<String,String>>();
		ResIterator iterEntities = nifModel.listSubjectsWithProperty(ITSRDF.taClassRef);
        while (iterEntities.hasNext()) {
    		Map<String,String> map = new HashMap<String,String>();
            Resource r = iterEntities.nextResource();

            String entityURI = r.getURI();
            
            StmtIterator iter2 = r.listProperties();
            boolean isTemporalExpression=false;
            while (iter2.hasNext()) {
				Statement st2 = iter2.next();
				String predicate =st2.getPredicate().getURI(); 
				String object = null;
				if(st2.getObject().isResource()){
					object = st2.getObject().asResource().getURI();
				}
				else{
					object = st2.getObject().asLiteral().getString();
				}
				if(predicate.equalsIgnoreCase(ITSRDF.taClassRef.getURI()) && object.equalsIgnoreCase(TIME.temporalEntity.getURI())){
					isTemporalExpression=true;
				}
				map.put(predicate,object);
			}
            if(!map.isEmpty() && !isTemporalExpression){
                list.put(entityURI,map);
            }
        }
        if(list.isEmpty()){
        	return null;
        }
		return list;
	}

	public static List<String[]> extractTemporalExpressions(Model nifModel){
		List<String[]> list = new LinkedList<String[]>();
				
        //ResIterator iterEntities = nifModel.listSubjectsWithProperty(NIF.entity);
		ResIterator iterEntities = nifModel.listSubjectsWithProperty(TIME.intervalStarts);
        while (iterEntities.hasNext()) {
            Resource r = iterEntities.nextResource();
            //Statement st = r.getProperty(NIF.entity);
            Statement st = r.getProperty(ITSRDF.taClassRef);
            String stringSt = ( st!=null ) ? st.getObject().asResource().getURI() : null;
//            System.out.println("1."+st.getObject().asResource().getURI());
            Statement st2 = r.getProperty(NIF.anchorOf);
            String stringSt2 = ( st2!=null ) ? st2.getLiteral().getString() : null;
//            System.out.println("7."+st2.getLiteral().getString());
            Statement st3 = r.getProperty(TIME.intervalStarts);
            String stringSt3 = ( st2!=null ) ? st3.getLiteral().getString() : null;
            Statement st4 = r.getProperty(TIME.intervalFinishes);
            String stringSt4 = ( st2!=null ) ? st4.getLiteral().getString() : null;
            
            String[] information = {stringSt, stringSt2, stringSt3, stringSt4};
            list.add(information);
        }
        if(list.isEmpty()){
        	return null;
        }
		return list;
	}
	
	public static List<String[]> extractEntityIndices(Model nifModel){
//		try {
//			System.out.println(NIFConverter.serializeRDF(nifModel, "text/turtle"));
//		}
//		catch(Exception e) {
//			
//		}
		List<String[]> list = new LinkedList<String[]>();
		//ResIterator iterEntities = nifModel.listSubjectsWithProperty(NIF.entity);
		ResIterator iterEntities = nifModel.listSubjectsWithProperty(ITSRDF.taClassRef);
		while (iterEntities.hasNext()) {
			Resource r = iterEntities.nextResource();
            //Statement st = r.getProperty(NIF.entity);
            Statement st = r.getProperty(ITSRDF.taClassRef);
            String stringSt = ( st!=null ) ? st.getObject().asResource().getURI() : null;
            Statement st2 = r.getProperty(NIF.anchorOf);
            String stringSt2 = ( st2!=null ) ? st2.getLiteral().getString() : null;
            Statement st3 = r.getProperty(ITSRDF.taIdentRef);
            String stringSt3 = ( st3!=null ) ? st3.getObject().asResource().getURI() : null;
            Statement st4 = r.getProperty(NIF.beginIndex);
            String stringSt4 = ( st4!=null ) ? st4.getLiteral().getString() : null;
            Statement st5 = r.getProperty(NIF.endIndex);
            String stringSt5 = ( st5!=null ) ? st5.getLiteral().getString() : null;
            String entityURI = r.getURI();
            String[] information = {stringSt3,stringSt2,stringSt,stringSt4,stringSt5, entityURI};
            list.add(information);
        }
        if(list.isEmpty()){
        	return null;
        }
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		String s = "@prefix nif-ann: <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-annotation#> .\r\n" + 
				"@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\r\n" + 
				"@prefix lynxnif: <http://lynx-project.eu/ontologies/nif#> .\r\n" + 
				"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\r\n" + 
				"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\r\n" + 
				"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\r\n" + 
				"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\r\n" + 
				"\r\n" + 
				"<http://lynx-project.eu/res/5b1ed630b10623400cc9c91599a4b00b3c401b67761ae2dfbb4365c876f18ead#offset_11_17>\r\n" + 
				"        a                       nif:String , nif:RFC5147String ;\r\n" + 
				"        nif-ann:annotationUnit  [ nif-ann:annotationUnitType  \"Named Entity\" ;\r\n" + 
				"                                  nif:author                  \"Lynx Geolocation Service\" ;\r\n" + 
				"                                  itsrdf:taClassRef           <http://dbpedia.org/ontology/Location>\r\n" + 
				"                                ] ;\r\n" + 
				"        nif:anchorOf            \"Berlin\" ;\r\n" + 
				"        nif:beginIndex          \"11\"^^xsd:nonNegativeInteger ;\r\n" + 
				"        nif:endIndex            \"17\"^^xsd:nonNegativeInteger ;\r\n" + 
				"        nif:referenceContext    <http://lynx-project.eu/res/5b1ed630b10623400cc9c91599a4b00b3c401b67761ae2dfbb4365c876f18ead#offset_0_27> .\r\n" + 
//				"        itsrdf:taClassRef       <http://dbpedia.org/ontology/Location> .\r\n" + 
				"\r\n" + 
				"<http://lynx-project.eu/res/5b1ed630b10623400cc9c91599a4b00b3c401b67761ae2dfbb4365c876f18ead#offset_0_27>\r\n" + 
				"        a               nif:RFC5147String , nif:String , nif:Context ;\r\n" + 
				"        <http://lkg.lynx-project.eu/def/metadata>\r\n" + 
				"                [ <http://data.europa.eu/eli/ontology#jurisdiction>\r\n" + 
				"                          \"ES\" ;\r\n" + 
				"                  <http://purl.org/dc/terms/author>\r\n" + 
				"                          \"jmschnei\"\r\n" + 
				"                ] ;\r\n" + 
				"        nif:beginIndex  \"0\"^^xsd:nonNegativeInteger ;\r\n" + 
				"        nif:endIndex    \"27\"^^xsd:nonNegativeInteger ;\r\n" + 
				"        nif:isString    \"Welcome to Berlin in 2016.\\n\" .\r\n" + 
				"";
		
		Model model = NIFConversionService.unserializeNIF(s, "text/turtle").getModel();
		
		Map<String,Map<String,Object>> map1 = NIFReader.extractAnnotationUnitEntitiesExtended(model);
		Set<String> set1 = map1.keySet();
		for (String key1: set1) {
			System.out.println(key1+":");
			Map<String,Object> map2 = map1.get(key1);
			Set<String> set2 = map2.keySet();
			for (String key2: set2) {
				System.out.print("\t"+key2+":");
				Object obj = map2.get(key2);
				if(obj instanceof String) {
					System.out.println(""+obj);
				}
				else if(obj instanceof HashMap<?,?>) {
					System.out.println();
					Map<String,String> map3 = (HashMap<String,String>)obj;
					Set<String> set3 = map3.keySet();
					for (String key3: set3) {
						System.out.println("\t\t"+key3+":"+map3.get(key3));
					}
				}				
			}
		}
	}
	
	public static Map<String,Map<String,Object>> extractAnnotationUnitEntitiesExtended(Model nifModel){
		Map<String,Map<String,Object>> list = new HashMap<String,Map<String,Object>>();
				
        //ResIterator iterEntities = nifModel.listSubjectsWithProperty(NIF.entity);
//		ResIterator iterEntities = nifModel.listSubjectsWithProperty(ITSRDF.taClassRef);
		ResIterator iterEntities = nifModel.listSubjectsWithProperty(NIF.annotationUnit);
        while (iterEntities.hasNext()) {
    		Map<String,Object> map = new HashMap<String,Object>();
            Resource r = iterEntities.nextResource();

            String entityURI = r.getURI();
            
            StmtIterator iter2 = r.listProperties();
            while (iter2.hasNext()) {
				Statement st2 = iter2.next();
				String predicate =st2.getPredicate().getURI(); 
				// Check if it is an annotation unit.
				if(predicate.equalsIgnoreCase(NIF.annotationUnit.toString())) {
					Map<String,String> nifAnnMap = new HashMap<String, String>();
					
					NodeIterator it1 = nifModel.listObjectsOfProperty(r, st2.getPredicate());
					
					while(it1.hasNext()) {
						RDFNode node = it1.next();
			            StmtIterator iter3 = node.asResource().listProperties();
			            while (iter3.hasNext()) {
							Statement st3 = iter3.next();
							String predicate3 =st3.getPredicate().getURI();
							String object = null;
							if(st3.getObject().isResource()){
								object = st3.getObject().asResource().getURI();
							}
							else{
								object = st3.getObject().asLiteral().getString();
							}
//							System.out.println(predicate3 + "  --  "+ object);
							nifAnnMap.put(predicate3,object);
			            }
					}					
					map.put(predicate,nifAnnMap);
				}
				else {
					String object = null;
					if(st2.getObject().isResource()){
						object = st2.getObject().asResource().getURI();
					}
					else{
						object = st2.getObject().asLiteral().getString();
					}
					map.put(predicate,object);
				}
			}
            if(!map.isEmpty()){
                list.put(entityURI,map);
            }
        }
        if(list.isEmpty()){
        	return null;
        }
		return list;
	}

	public static List<String[]> extractAnnotationUnitEntityIndices(Model nifModel){
//		try {
//			System.out.println(NIFConverter.serializeRDF(nifModel, "text/turtle"));
//		}
//		catch(Exception e) {
//			
//		}
		List<String[]> list = new LinkedList<String[]>();
		//ResIterator iterEntities = nifModel.listSubjectsWithProperty(NIF.entity);
		ResIterator iterEntities = nifModel.listSubjectsWithProperty(NIF.annotationUnit);
		while (iterEntities.hasNext()) {
			Resource r = iterEntities.nextResource();
            //Statement st = r.getProperty(NIF.entity);
            Statement st = r.getProperty(ITSRDF.taClassRef);
            String stringSt = ( st!=null ) ? st.getObject().asResource().getURI() : null;
            Statement st2 = r.getProperty(NIF.anchorOf);
            String stringSt2 = ( st2!=null ) ? st2.getLiteral().getString() : null;
            Statement st3 = r.getProperty(ITSRDF.taIdentRef);
            String stringSt3 = ( st3!=null ) ? st3.getObject().asResource().getURI() : null;
            Statement st4 = r.getProperty(NIF.beginIndex);
            String stringSt4 = ( st4!=null ) ? st4.getLiteral().getString() : null;
            Statement st5 = r.getProperty(NIF.endIndex);
            String stringSt5 = ( st5!=null ) ? st5.getLiteral().getString() : null;
            String entityURI = r.getURI();
            String[] information = {stringSt3,stringSt2,stringSt,stringSt4,stringSt5, entityURI};
            list.add(information);
        }
        if(list.isEmpty()){
        	return null;
        }
		return list;
	}
	
	public static String extractIndexNIFPath(Model nifModel){
		String documentUri = NIFReader.extractDocumentWholeURI(nifModel);
        Resource documentResource = nifModel.getResource(documentUri);
        NodeIterator it = nifModel.listObjectsOfProperty(documentResource, NIF.indexPath);
        String result = "";
        while(it.hasNext()){
        	result += ";"+it.next().asLiteral().getString();
        }
        return (result!=null && result.length()>0)?result.substring(1):"";
	}
	
	public static String extractDocumentNIFPath(Model nifModel){
		StmtIterator iter = nifModel.listStatements(null, RDF.type, nifModel.getResource(NIF.Context.getURI()));
      
		while(iter.hasNext()){
			Resource contextRes = iter.nextStatement().getSubject();
			Statement st = contextRes.getProperty(LYNXNIF.DocumentPath);
			if(st!=null){
	//			System.out.println(contextRes.getURI());
				String uri = st.getObject().asResource().getURI();
				return uri;
			}
		}
		return null;//"No document path found.");
	}
	
	public static List<String> extractTaIdentRefsFromModel(Model nifModel){
		List<String> refs = new LinkedList<String>();
		//ResIterator iterEntities = nifModel.listSubjectsWithProperty(NIF.entity);
		ResIterator iterEntities = nifModel.listSubjectsWithProperty(ITSRDF.taIdentRef);
        while (iterEntities.hasNext()) {
            Resource r = iterEntities.nextResource();
            Statement st3 = r.getProperty(ITSRDF.taIdentRef);
            String ref = ( st3!=null ) ? st3.getObject().asResource().getURI() : null;
            if(ref!=null){
            	refs.add(ref);
            }
        }
        return refs;
	}
	
	public static List<String> extractTaClassRefsFromModel(Model nifModel){
		List<String> refs = new LinkedList<String>();
		//ResIterator iterEntities = nifModel.listSubjectsWithProperty(NIF.entity);
		ResIterator iterEntities = nifModel.listSubjectsWithProperty(ITSRDF.taClassRef);
        while (iterEntities.hasNext()) {
            Resource r = iterEntities.nextResource();
            Statement st3 = r.getProperty(ITSRDF.taClassRef);
            String ref = ( st3!=null ) ? st3.getObject().asResource().getURI() : null;
            if(ref!=null){
            	refs.add(ref);
            }
        }
        return refs;
	}
	
	public static Map<String,String> extractDocumentInformation(Model nifModel){
		Map<String,String> map = new HashMap<String,String>();
		
        ResIterator iterEntities = nifModel.listSubjectsWithProperty(RDF.type,"http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#Context");
        while (iterEntities.hasNext()) {
            Resource r = iterEntities.nextResource();
            StmtIterator iter2 = r.listProperties();
            while (iter2.hasNext()) {
				Statement st2 = iter2.next();
				String predicate =st2.getPredicate().getURI(); 
				String object = null;
				if(st2.getObject().isResource()){
					object = st2.getObject().asResource().getURI();
				}
				else{
					object = st2.getObject().asLiteral().getString();
				}
				map.put(predicate,object);
			}
        }
        if(map.isEmpty()){
        	return null;
        }
		return map;
	}

}
