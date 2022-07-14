package de.dfki.slt.datadukt.data.documents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import de.dfki.slt.datadukt.data.documents.conversion.WMSerialization;

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
public class LabelAnnotation extends BaseAnnotation implements Comparator<LabelAnnotation> {

	@JsonProperty("labelUnits")
	public List<Label> labels;

	public LabelAnnotation(){
		this.types = Arrays.asList(new String[]{"qont:LabelAnnotation"});
		labels = new ArrayList<Label>();
	}

	/**
	 * Default constructor is empty.
	 */
	public LabelAnnotation(String referenceContext){
		this.types = Arrays.asList(new String[]{"qont:LabelAnnotation"});
		this.referenceContext = referenceContext;
		this.id = referenceContext;
		labels = new ArrayList<Label>();
	}

	/**
	 */
	public LabelAnnotation(String referenceContext, List<Label> labels) {
		this.types = Arrays.asList(new String[]{"qont:LabelAnnotation"});
		this.referenceContext = referenceContext;
		this.id = referenceContext;
		this.labels = labels;
	}

	public void addLabel(Label l) {
		labels.add(l);
	}

	public List<Label> getLabels() {
		return labels;
	}

	public void setLabels(List<Label> labels) {
		this.labels = labels;
	}

	/**
	 * Writes JSONLD.
	 * The task is delegated to Jackson.
	 */
	public String toJSON() {
		return WMSerialization.toJSON(this);
	}

	@Override
	public int compare(LabelAnnotation o1, LabelAnnotation o2) {
		if (o1.labels.equals(o2.getLabels())) {
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
				", labels:'" + labels + '\'' +
				'}';
	}
}



