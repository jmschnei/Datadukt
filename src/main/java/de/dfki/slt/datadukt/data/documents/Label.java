package de.dfki.slt.datadukt.data.documents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Label {

	@JsonProperty("type")
	@JsonAlias({"@type"})
    public List<String> types = new ArrayList<String>();

//    @JsonProperty( "id" )
//    @JsonAlias( {"@id"} )
//    public String id = "";

    @JsonIgnore
    public Map<String, String> annotationProperties = new HashMap<String, String>();
    
    public Label(){
//        types = Arrays.asList(new String[]{"nif:AnnotationUnit"});
        types = Arrays.asList(new String[]{"qont:Label"});
    }

    public Label(Map<String,String> properties){
        types = Arrays.asList(new String[]{"qont:Label"});
        annotationProperties = new HashMap<String, String>(properties);
    }

    @JsonAnySetter 
    public void add(String key, String value) {
        annotationProperties.put(key, value);
    }    

    @JsonAnyGetter
    public Map<String,String> getMap() {
        return annotationProperties;
    }    
    
    public String toJSON(){
        try{
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
            return mapper.writeValueAsString(this);
        }catch(Exception e){
            e.printStackTrace();
            return "{}";
        }
    }

    @Override
    public String toString() {
    	String s = "{" +
//                "id:'" + id + '\'' + 
    			"types:" + types ;
    	for (String key : annotationProperties.keySet()) {
        	s = s+ ", "+key+":'" + annotationProperties.get(key)+ '\'';
		}
    	s = s + "}";
    	return s;
    }

}
