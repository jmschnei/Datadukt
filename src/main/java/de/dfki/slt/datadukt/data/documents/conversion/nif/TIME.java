package de.dfki.slt.datadukt.data.documents.conversion.nif;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

public class TIME {

	public static final String uri = "http://www.w3.org/2006/time#";
	
	
    protected static final Resource resource(String local) {
        return ResourceFactory.createResource(uri + local);
    }

    protected static final Property property(String local) {
        return ResourceFactory.createProperty(uri, local);
    }
	
    public static final Property temporalEntity = property("TemporalEntity");
    public static final Property intervalStarts = property("intervalStarts");
    public static final Property intervalFinishes = property("intervalFinishes");
    
    
    
}
