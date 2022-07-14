package de.dfki.slt.datadukt.data.documents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import de.dfki.slt.datadukt.data.documents.conversion.WMSerialization;

/**
 * @author julianmorenoschneider
 * @project java
 * @date 01 Apr 2020
 * @company DFKI
 * @description 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "id", "type" })
public class LinkedLabelPositionAnnotation extends BaseAnnotation implements Comparator<LinkedLabelPositionAnnotation> {

    public Integer offset_ini = 0;
    
    public Integer offset_end = 0;
    
    public String anchorOf = "";
    
//  We could also use List<LynxAnnotationUnit>, but this brings some problems in occasions to be further investigated......
    public List<Label> labels;

//  We could also use List<LynxAnnotationUnit>, but this brings some problems in occasions to be further investigated......
    public List<Link> links;

    public LinkedLabelPositionAnnotation(){
		labels = new ArrayList<Label>();
    }

    /**
     * Default constructor is empty.
     */
    public LinkedLabelPositionAnnotation(String referenceContext, Integer offset_ini, Integer offset_end, String anchorOf){
        this.types = Arrays.asList(new String[]{"qont:LabelPositionAnnotation", "nif:OffsetBasedString"});
        this.offset_ini = offset_ini;
        this.offset_end = offset_end;
        this.anchorOf = anchorOf;
        this.referenceContext = referenceContext;
        this.id = generateId(referenceContext);
		labels = new ArrayList<Label>();
		links = new ArrayList<Link>();
    }

    /**
     */
	public LinkedLabelPositionAnnotation(String referenceContext, Integer _offset_ini, Integer _offset_end, String _anchorOf, List<Label> labels, List<Link> links){
        this.types = Arrays.asList(new String[]{"qont:LabelPositionAnnotation", "nif:OffsetBasedString"});
        this.referenceContext = referenceContext;
        this.offset_ini = _offset_ini;
        this.offset_end = _offset_end;
        this.anchorOf = _anchorOf;
        this.id = generateId(referenceContext);
        this.labels = labels;
        this.links = links;
    }
        
    public String generateId(String referenceContext) {
    	if(referenceContext==null || referenceContext.equalsIgnoreCase("")) {
    		String uid = UUID.randomUUID().toString().substring(0,8);
    		return "http://qurator-project.de/res/" + uid + "#offset_" + offset_ini + "_" + offset_end;
    	}
    	if(referenceContext.contains("#")) {
            return referenceContext.split("#")[0] + "#offset_" + offset_ini + "_" + offset_end;
    	}
    	else {
            return referenceContext + "#offset_" + offset_ini + "_" + offset_end;
    	}
    }

    public void addLabel(Label l) {
    	labels.add(l);
    }
    
    public Integer getOffset_ini() {
		return offset_ini;
	}

	public void setOffset_ini(Integer offset_ini) {
		this.offset_ini = offset_ini;
	}

	public Integer getOffset_end() {
		return offset_end;
	}

	public void setOffset_end(Integer offset_end) {
		this.offset_end = offset_end;
	}

	public String getAnchorOf() {
		return anchorOf;
	}

	public void setAnchorOf(String anchorOf) {
		this.anchorOf = anchorOf;
	}

	public List<Label> getLabels() {
		return labels;
	}

	public void setLabels(List<Label> labels) {
		this.labels = labels;
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
    public int compare(LinkedLabelPositionAnnotation o1, LinkedLabelPositionAnnotation o2) {
        if (o1.offset_ini==o2.offset_ini)
            return 0;
        return (o1.offset_ini>o2.offset_ini) ? 1 : - 1;
    }

    @Override
    public String toString() {
        return '{' +
                "id:'" + id + '\'' +
                ", types:" + types +
                ", source:'" + source + '\'' +
                ", referenceContext:'" + referenceContext + '\'' +
                ", offset_ini:" + offset_ini +
                ", offset_end:" + offset_end +
                ", anchorOf:'" + anchorOf + '\'' +
                ", labels:'" + labels + '\'' +
                ", links:'" + links + '\'' +
                '}';
    }
}



