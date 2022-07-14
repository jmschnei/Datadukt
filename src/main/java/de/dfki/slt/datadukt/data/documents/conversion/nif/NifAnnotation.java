package de.dfki.slt.datadukt.data.documents.conversion.nif;

import java.util.Map;

/**
 * Class that represents each annotation on a NIF Document.
 * @author julianmorenoschneider
 *
 */
public class NifAnnotation {

	private String uri;
	
	private int beginIndex;
	private int endIndex;

	private String anchor;

	private Map<String, Object> properties;
	
	public NifAnnotation() {
		super();
	}

	public NifAnnotation(String uri, Map<String, Object> properties) {
		super();
		this.uri = uri;
		
		//TODO Extract the generic properties from the Map and include them as variables.

//		for (String key : properties.keySet()) {
//			System.out.println(key+"--"+properties.get(key));
//		}
		beginIndex = Integer.parseInt(properties.get("http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#beginIndex").toString());
		endIndex = Integer.parseInt(properties.get("http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#endIndex").toString());
		anchor = properties.get("http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#anchorOf").toString();
		properties.remove("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
		properties.remove("http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#beginIndex");
		properties.remove("http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#endIndex");
		properties.remove("http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#anchorOf");
		this.properties = properties;
	}
	
	public NifAnnotation(String uri, int beginIndex, int endIndex, String anchor, Map<String, Object> properties) {
		super();
		this.uri = uri;
		this.beginIndex = beginIndex;
		this.endIndex = endIndex;
		this.anchor = anchor;
		this.properties = properties;
	}

	public String getUri() {
		return uri;
	}
	
	public void setUri(String uri) {
		this.uri = uri;
	}
	
	public Map<String, Object> getProperties() {
		return properties;
	}
	
	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
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

	public String getAnchor() {
		return anchor;
	}

	public void setAnchor(String anchor) {
		this.anchor = anchor;
	}
	
	
}
