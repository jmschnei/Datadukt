package de.dfki.cwm.data.documents.conversion.nif;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

public class GEO {

	public static final String uri = "http://www.w3.org/2003/01/geo/wgs84_pos/";
	
    protected static final Resource resource(String local) {
        return ResourceFactory.createResource(uri + local);
    }

    protected static final Property property(String local) {
        return ResourceFactory.createProperty(uri, local);
    }
	
    public static final Property latitude = property("lat");
    public static final Property longitude = property("long");
	
}

