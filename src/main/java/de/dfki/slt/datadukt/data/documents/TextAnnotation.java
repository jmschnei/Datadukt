package de.dfki.slt.datadukt.data.documents;

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
 * @description Class that defines text annotations in documents, for example, summaries or translations.
 *
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "id", "type" })
public class TextAnnotation extends BaseAnnotation implements Comparator<TextAnnotation> {

    @JsonProperty("generatedtext")
    public String text = "";
    
    public String language = "";
    
    public TextAnnotation(){
    }

    /**
     * Default constructor is empty.
     */
    public TextAnnotation(String text, String language){
        this.types = Arrays.asList(new String[]{"qont:TextAnnotation"});
        this.text = text;
        this.language = language;
    }

    public TextAnnotation(String text, String language, String type){
        this.types = Arrays.asList(new String[]{"qont:TextAnnotation",type});
        this.text = text;
        this.language = language;
    }

    public TextAnnotation(String text, String language, List<String> types){
        this.types = Arrays.asList(new String[]{"qont:TextAnnotation"});
    	this.types.addAll(types);
        this.text = text;
        this.language = language;
    }
    
	@JsonProperty("generatedtext")
    public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
	
	public String toJSON() {
    	return WMSerialization.toJSON(this);
    }
    
    @Override
    public int compare(TextAnnotation o1, TextAnnotation o2) {
        if (!o1.getId().equalsIgnoreCase(o2.getId()))
        	return -1;
        List<String> types1 = o1.getTypes();
        List<String> types2 = o2.getTypes();
        if(types1.size()!=types2.size())
        	return -1;
        for (int i = 0; i < types1.size(); i++) {
        	if(types1.get(i).equalsIgnoreCase(types2.get(i))) {
        		return -1;
        	}
		}
        if(!o1.getText().equalsIgnoreCase(o2.getText())) {
        	return -1;
        }
        if(!o1.getLanguage().equalsIgnoreCase(o2.getLanguage())) {
        	return -1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return '{' +
                "id:'" + id + '\'' +
                ", types:" + types +
//                ", referenceContext:'" + referenceContext + '\'' +
                ", text:'" + text + '\'' +
                ", language:'" + language + '\'' +
                '}';
    }
}



