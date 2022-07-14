package de.dfki.slt.datadukt.data.documents.conversion.nif;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

public class LYNXNIF {

    protected static final String uri = "http://lynx-project.eu/ontologies/nif#";
    protected static final String defaultPrefix = "http://lynx-project.eu/res/";

    /**
     * returns the URI for this schema
     * 
     * @return the URI for this schema
     */
    public static String getURI() {
        return uri;
    }
    public static String getDefaultPrefix() {
        return defaultPrefix;
    }

    public static final Resource resource(String local) {
        return ResourceFactory.createResource(uri + local);
    }

    public static final Property property(String local) {
        return ResourceFactory.createProperty(uri, local);
    }

	public static final Property temporaryPDFPath = property("TemporaryPDFPath");

	public static final Property DocumentPath = property("DocumentPath");
    public static final Property DocumentNIFPath = property("DocumentNIFPath");

    public static final Property DocumentName = property("DocumentName");

    public static final Property isSimilarTo = property("isSimilarTo");
    public static final Property similarityScore = property("similarityScore");
    public static final Property similarityURI = property("similarityURI");

    public static final Property isHyperlinkedTo = property("isHyperlinkedTo");
    public static final Property hasHyperlinkedConfidence = property("hasHyperlinkedConfidence");

    public static final Property babelnetSense = property("babelnetSense");
    
    public static final Resource anchorOf = resource("anchorOf");
    public static final Property beginIndex = property("beginIndex");
    public static final Property confidence = property("confidence");
    public static final Property isString = property("isString");
    public static final Property endIndex = property("endIndex");
    public static final Property entity = property("entity");
    public static final Property keyword = property("keyword");
    
    public static final Property averageLatitude = property("averageLatitude");
    public static final Property averageLongitude = property("averageLongitude");
    public static final Property standardDeviationLatitude = property("standardDeviationLatitude");
    public static final Property standardDeviationLongitude = property("standardDeviationLongitude");
    
    public static final Property meanDateStart = property("meanDateStart");
    public static final Property meanDateEnd = property("meanDateEnd");
        
    public static final Property sentimentValue = property("sentimentValue"); //TODO: this is a dummy placeholder. Check with Felix for a proper name from a proper ontology for this!    
    
    public static final Resource Event = resource("Event");
    public static final Property eventSubject = property("eventSubject");
    public static final Property eventPredicate = property("eventPredicate");
    public static final Property eventObject = property("eventObject");
    public static final Property eventTimestamp = property("eventTimestamp");
    public static final Property eventRelevance = property("eventRelevance");
    
    public static final Resource MovementActionEvent = resource("MovementActionEvent");
    public static final Property maePerson = property("maePerson");
    public static final Property maeOrigin = property("maeOrigin");
    public static final Property maeDestination = property("maeDestination");
    public static final Property maeDepartureTime = property("maeDepartureTime");
    public static final Property maeArrivalTime = property("maeArrivalTime");
    public static final Property maeTravelMode = property("maeTravelMode");
    public static final Property maeScore = property("maeScore");

    public static final Resource MovementTrigger = resource("MovementTrigger");

    public static final Property travelMode = property("travelMode");
    public static final Property movementVerb = property("movementVerb");
	public static final Resource relation = resource("relation");

    
    public static String createDocumentURI(){
    	return defaultPrefix+"doc"+((int)(Math.random()*9000)+1000);
    }

}
