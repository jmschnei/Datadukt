package de.dfki.cwm.data.documents;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

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
public class PositionAnnotation extends BaseAnnotation implements Comparator<PositionAnnotation> {

    public Integer offset_ini = 0;

    public Integer offset_end = 0;

    public String anchorOf = "";
    
////  We could also use List<LynxAnnotationUnit>, but this brings some problems in occasions to be further investigated......
//    public List<Object> annotationUnit = new ArrayList<>();

    public PositionAnnotation(){
    }

    /**
     * Default constructor is empty.
     */
    public PositionAnnotation(String referenceContext, Integer offset_ini, Integer offset_end, String anchorOf){
        this.types = Arrays.asList(new String[]{"qont:Annotation", "nif:OffsetBasedString"});
        this.offset_ini = offset_ini;
        this.offset_end = offset_end;
        this.anchorOf = anchorOf;
        this.id = generateId(referenceContext);
    }

    /**
     */
	public PositionAnnotation(String referenceContext, Integer _offset_ini, Integer _offset_end, String _anchorOf, String _taClassRef, String _taIdentRef){
        this.types = Arrays.asList(new String[]{"qont:Annotation", "nif:OffsetBasedString"});
        this.referenceContext = referenceContext;
        this.offset_ini = _offset_ini;
        this.offset_end = _offset_end;
        this.anchorOf = _anchorOf;
        this.id = generateId(referenceContext);
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

    public String getId() {
        return id;
    }

    public void setId(String _id) {
        id = _id;
    }
    
    public void setTypes(List<String> types) {
            this.types = types;
    }

    public List<String> getTypes(){
        return types;
    }    
    
//    public void addAnnotationUnit(QuratorAnnotationUnit qau) {
//    	annotationUnit.add(qau);
//    }
    
    /**
     * Writes JSONLD.
     * The task is delegated to Jackson.
     */
    public String toJSON() {
    	return WMSerialization.toJSON(this);
    }

    @Override
    public int compare(PositionAnnotation o1, PositionAnnotation o2) {
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
//                ", annotationUnit:'" + annotationUnit + '\'' +
                '}';
    }
}



