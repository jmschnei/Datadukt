package de.dfki.cwm.data.documents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import de.dfki.cwm.data.documents.conversion.WMSerialization;

/**
 * @author Julian Moreno Schneider julian.moreno_schneider@dfki.de
 * @modified_by 
 * @project java
 * @date 10.06.2020
 * @date_modified 
 * @company DFKI
 * @description 
 *
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "id", "type" })
public class LinkAnnotation extends BaseAnnotation implements Comparator<LinkAnnotation> {

	public List<Link> links;

	public LinkAnnotation(){
		links = new ArrayList<Link>();
	}

	/**
	 * Default constructor is empty.
	 */
	public LinkAnnotation(String referenceContext){
		this.types = Arrays.asList(new String[]{"qont:LinkAnnotation"});
		this.referenceContext = referenceContext;
		this.id = referenceContext;
		links = new ArrayList<Link>();
	}

	/**
	 */
	public LinkAnnotation(String referenceContext, List<Link> links) {
		this.types = Arrays.asList(new String[]{"qont:LinkAnnotation"});
		this.referenceContext = referenceContext;
		this.id = referenceContext;
		this.links = links;
	}

	public void addLink(Link l) {
		links.add(l);
	}

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	/**
	 * Writes JSONLD.
	 * The task is delegated to Jackson.
	 */
	public String toJSON() {
		return WMSerialization.toJSON(this);
	}

	@Override
	public int compare(LinkAnnotation o1, LinkAnnotation o2) {
		if (o1.links.equals(o2.getLinks())) {
			return 0;
		}
		else {
			return -1;
		}
	}

	@Override
	public String toString() {
		return '{' +
				"id:'" + id + '\'' +
				", types:" + types +
				", source:'" + source + '\'' +
				", referenceContext:'" + referenceContext + '\'' +
				", labels:'" + links + '\'' +
				'}';
	}
}



