package de.dfki.cwm.data.documents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class BaseAnnotation {

    @JsonProperty( "id" )
    @JsonAlias( {"@id"} )
    public String id = "";
    
    //Types of annotation
    @JsonProperty("type")
    @JsonAlias({"@type"})
    public List<String> types = new ArrayList<String>();
    
    //Creator of the annotation
    public String source = "";
    
    //Document where the annotation was made. Ex. http://lkg.lynx-project.eu/res/doc004
    @JsonProperty("referenceContext")
    public String referenceContext = "";
    
	/// Metadata elements: version, author, date, etc.
	private Map<String, Object> metadata = new HashMap<String, Object>();

    public BaseAnnotation(){
        this.types = Arrays.asList(new String[]{"qont:BaseAnnotation"});
    }

    public String getId() {
        return id;
    }
    
    public void setId(String _id) {
        id = _id;
    }
    
    public String generateId(String referenceContext) {
    	if(referenceContext==null || referenceContext.equalsIgnoreCase("")) {
    		String uid = UUID.randomUUID().toString().substring(0,8);
    		return "http://qurator-project.de/res/" + uid;
    	}
    	if(referenceContext.contains("#")) {
            return referenceContext.split("#")[0];
    	}
    	else {
            return referenceContext;
    	}
    }

    public void setTypes(List<String> types) {
            this.types = types;
    }

    public List<String> getTypes(){
        return types;
    }
    
    public void addType(String type) {
    	types.add(type);
    }
    
    /**
     * Writes JSONLD.
     * The task is delegated to Jackson.
     */
    public String toJSON() {
    	return WMSerialization.toJSON(this);
    }

	public Map<String, Object> getMetadata() {
		return metadata;
	}

	public void setMetadata(Map<String, Object> metadata) {
		this.metadata = metadata;
	}

	public void addMetadata(String key, Object obj) {
		metadata.put(key, obj);
	}
	
    public String getReferenceContext() {
		return referenceContext;
	}

	public void setReferenceContext(String referenceContext) {
		this.referenceContext = referenceContext;
	}

	@Override
    public String toString() {
        return '{' +
                "id:'" + id + '\'' +
                ", types:" + types +
                ", source:'" + source + '\'' +
                ", referenceContext:'" + referenceContext + '\'' +
                '}';
    }
}



