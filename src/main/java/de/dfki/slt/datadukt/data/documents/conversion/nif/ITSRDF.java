package de.dfki.slt.datadukt.data.documents.conversion.nif;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

/**
 * The Internationalization Tag Set (ITS) Version 2.0 vocabulary. <b>Note</b>
 * that this class contains only the classes and properties used by GERBIL.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 * 
 */
public class ITSRDF {

    protected static final String uri = "http://www.w3.org/2005/11/its/rdf#";

    /**
     * returns the URI for this schema
     * 
     * @return the URI for this schema
     */
    public static String getURI() {
        return uri;
    }

    protected static final Resource resource(String local) {
        return ResourceFactory.createResource(uri + local);
    }

    protected static final Property property(String local) {
        return ResourceFactory.createProperty(uri, local);
    }

    public static final Property taClassRef = property("taClassRef");
    public static final Property taConfidence = property("taConfidence");
    public static final Property taIdentRef = property("taIdentRef");
    public static final Property taSource = property("taSource");

    public static final Property target = property("target");
}
