package de.dfki.cwm.data.documents;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.ObjectMapper;

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
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "type" })
//@JsonDeserialize(using = LynxPartDeserializer.class)
public class WMDocumentPart extends PositionAnnotation {

	public String title;           //title, e.g. "2.1 Introduction"
	
	public WMDocumentPart() {
	}
	
	/**
	 * Creates a simple LynxDocumentPart and calculates the new id.
	 * @param _parent Such as http://lkg.lynx-project.eu/res/doc001
	 * @param _offset_ini 0 is the beginning of the document
	 * @param _offset_end we do not verify that you give a dumb number. may crash if you lie.
	 * @param _title Fully optional
	 */
	public WMDocumentPart(String referenceContext, String title, Integer offset_ini, Integer offset_end, String anchorOf){
		super(referenceContext, offset_ini, offset_end, anchorOf);
        this.types = Arrays.asList(new String[]{"qont:QuratorDocumentPart", "nif:OffsetBasedString"});
		//this.parent = parent;
		this.title = title;
	}

	@JsonGetter("title")
	public String getTitle() {
		return title;
	}

	@JsonSetter("title")
	public void setTitle(String title) {
		this.title = title;
	}

	@JsonIgnore
	private String getHash(){
		return referenceContext.split("#")[0];
	}
	
	/**
     * Writes JSONLD.
     * The task is delegated to Jackson.
     */
    public String toJSON()
    {
        try{
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(this);
        }catch(Exception e)
        {
            e.printStackTrace();
            return "{}";
        }
    }
}
