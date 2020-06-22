/**
 * 
 */
package de.dfki.cwm.data;

import java.util.Arrays;
import java.util.List;

import de.dfki.cwm.exceptions.WorkflowException;

/**
 * @author julianmorenoschneider
 * @project CurationWorkflowManager
 * @date 07.02.2020
 * @company DFKI
 * @description This enum determines specific formats for information in the workflow and workflow manager. 
 * 
 */
public enum Format {
	TURTLE,
	RDF, 
	JSONLD, 
	TEXT, 
	JSON, 
	RDFXML, 
	URI,
	ALEPH,
	NIFv20,
	NIFv21,
	UNK;
	
	public static final String[] turtle = {"turtle","TURTLE","TTL","ttl","text/turtle"};
	public static final List<String> turtle2 = Arrays.asList(turtle);

	public static final String[] rdf = {"rdf","RDF","application/rdf"};
	public static final List<String> rdf2 = Arrays.asList(rdf);

	public static final String[] jsonld = {"jsonld","JSONLD","json-ld","JSON-LD","application/ld+json"};
	public static final List<String> jsonld2 = Arrays.asList(jsonld);

	public static final String[] text = {"text","TEXT","text/plain","plaintext","PLAINTEXT"};
	public static final List<String> text2 = Arrays.asList(text);

	public static final String[] json = {"json","JSON","application/json"};
	public static final List<String> json2 = Arrays.asList(json);

	public static final String[] rdfxml = {"rdfxml","RDFXML","application/xml+rdf"};
	public static final List<String> rdfxml2 = Arrays.asList(rdfxml);

	public static final String[] uri = {"uri","URI"};
	public static final List<String> uri2 = Arrays.asList(uri);

	public static final String[] aleph = {"aleph","Aleph","ALEPH"};
	public static final List<String> aleph2 = Arrays.asList(uri);

	public static final String[] nif20 = {"nif2","NIF2","nif2.0","NIF2.0",};
	public static final List<String> nif202 = Arrays.asList(uri);

	public static final String[] nif21 = {"nif21","NIF21","nif2.1","NIF2.1",};
	public static final List<String> nif212 = Arrays.asList(uri);

	/**
	 * @method-description  Translate a string into an enum element
	 * @author  			julianmorenoschneider
	 * @modified_by
	 * @date  				07.02.2020
	 * @modified_date  				16.06.2020
	 * @return  			Enum type of the corresponding format
	 */
	public static Format getFormat(String sFormat) throws Exception {
		if(turtle2.contains(sFormat)) {
			return TURTLE;
		}
		if(rdf2.contains(sFormat)) {
			return RDF;
		}
		if(jsonld2.contains(sFormat)) {
			return JSONLD;
		}
		if(text2.contains(sFormat)) {
			return TEXT;
		}
		if(json2.contains(sFormat)) {
			return JSON;
		}
		if(rdfxml2.contains(sFormat)) {
			return RDFXML;
		}
		if(uri2.contains(sFormat)) {
			return URI;
		}
		if(aleph2.contains(sFormat)) {
			return ALEPH;
		}
		if(nif202.contains(sFormat)) {
			return NIFv20;
		}
		if(nif212.contains(sFormat)) {
			return NIFv21;
		}
		throw new WorkflowException("Unsupported format: "+sFormat);
	}


}
