package de.dfki.cwm.data.documents;

import java.util.Arrays;
import java.util.Comparator;

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
public class NumericAnnotation extends BaseAnnotation implements Comparator<NumericAnnotation> {

	public float value;

	public NumericAnnotation(){
	}

	/**
	 * Default constructor is empty.
	 */
	public NumericAnnotation(String referenceContext){
		this.types = Arrays.asList(new String[]{"qont:LabelAnnotation"});
		this.referenceContext = referenceContext;
		this.id = referenceContext;
		value = 0;
	}

	/**
	 */
	public NumericAnnotation(String referenceContext, float value) {
		this.types = Arrays.asList(new String[]{"qont:LabelAnnotation"});
		this.referenceContext = referenceContext;
		this.id = referenceContext;
		this.value = value;
	}

	
	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	/**
	 * Writes JSONLD.
	 * The task is delegated to Jackson.
	 */
	public String toJSON() {
		return WMSerialization.toJSON(this);
	}

	@Override
	public int compare(NumericAnnotation o1, NumericAnnotation o2) {
		if (o1.value == o2.getValue()) {
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
				", value:'" + value + '\'' +
				'}';
	}
}



