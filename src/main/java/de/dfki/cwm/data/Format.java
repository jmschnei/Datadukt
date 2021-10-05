/**
 * 
 */
package de.dfki.cwm.data;

import java.util.Arrays;
import java.util.List;

import de.dfki.cwm.exceptions.WorkflowException;

/**
 * @author julianmorenoschneider
 * @project CurationWorkflowManager
 * @date 07.02.2020
 * @company DFKI
 * @description This enum determines specific formats for information in the workflow and workflow manager. 
 * 
 */
public enum Format {
	WMD("application/workflowmanagerdocument"),
	TURTLE("text/turtle"),
	RDF("application/rdf"), 
	JSONLD("application/ld+json"), 
	TEXT("text/plain"), 
	JSON("application/json"), 
	JSONTOPIC("application/json+topic"), 
	RDFXML("application/xml+rdf"), 
	URI("uri"),
	ALEPH("aleph"),
	NIFv20("nif2.0"),
	NIFv21("nif2.1"),
	MP3("audio/mpeg3",true),
	WAV("audio/vnd.wav",true),
	MULTIPART("multipart/form-data",true),
	AUDIO("audio"),
	NONE("none"),
	UNK("unknown");
	
	String value;
	boolean isMultimedia;
	
	private Format(String value) {
		this.value=value;
		isMultimedia = false;
	}

	private Format(String value, boolean multimedia) {
		this.value=value;
		isMultimedia = multimedia;
	}

	public static final String[] turtle = {"turtle","TURTLE","TTL","ttl","text/turtle"};
	public static final List<String> turtle2 = Arrays.asList(turtle);

	public static final String[] rdf = {"rdf","RDF","application/rdf"};
	public static final List<String> rdf2 = Arrays.asList(rdf);

	public static final String[] jsonld = {"jsonld","JSONLD","json-ld","JSON-LD","application/ld+json","http://w3id.org/meta-share/omtd-share/Json"};
	public static final List<String> jsonld2 = Arrays.asList(jsonld);

	public static final String[] text = {"text","TEXT","text/plain","plaintext","PLAINTEXT"};
	public static final List<String> text2 = Arrays.asList(text);

	public static final String[] json = {"json","JSON","application/json"};
	public static final List<String> json2 = Arrays.asList(json);

	public static final String[] rdfxml = {"rdfxml","RDFXML","application/xml+rdf"};
	public static final List<String> rdfxml2 = Arrays.asList(rdfxml);

	public static final String[] uri = {"uri","URI"};
	public static final List<String> uri2 = Arrays.asList(uri);

	public static final String[] aleph = {"aleph","Aleph","ALEPH","aleph-json","Aleph-json","ALEPH-json","ajson"};
	public static final List<String> aleph2 = Arrays.asList(aleph);

	public static final String[] nif20 = {"nif2","NIF2","nif2.0","NIF2.0",};
	public static final List<String> nif202 = Arrays.asList(nif20);

	public static final String[] nif21 = {"nif21","NIF21","nif2.1","NIF2.1",};
	public static final List<String> nif212 = Arrays.asList(nif21);

	public static final String[] multi = {"multipart/form-data","form","multipart"};
	public static final List<String> multi2 = Arrays.asList(multi);

	/**
	 * Audio Formats 
	 */
	public static final String[] mp3 = {"mp3","audio/mpeg","audio/mpeg3","mpeg","mpg","http://w3id.org/meta-share/omtd-share/mp3"};
	public static final List<String> mp32 = Arrays.asList(mp3);

	public static final String[] wav = {"wav","audio/vnd.wav","http://w3id.org/meta-share/omtd-share/wav"};
	public static final List<String> wav2 = Arrays.asList(wav);

	/**
	 * Image Formats 
	 */
	// TODO 

	/**
	 * Video Formats 
	 */
	// TODO 

	/**
	 * @method-description  Translate a string into an enum element
	 * @author  			julianmorenoschneider
	 * @modified_by
	 * @date  				07.02.2020
	 * @modified_date  				16.06.2020
	 * @return  			Enum type of the corresponding format
	 */
	public static Format getFormat(String sFormat) throws Exception {
		if(turtle2.contains(sFormat)) {
			return TURTLE;
		}
		if(rdf2.contains(sFormat)) {
			return RDF;
		}
		if(jsonld2.contains(sFormat)) {
			return JSONLD;
		}
		if(text2.contains(sFormat)) {
			return TEXT;
		}
		if(json2.contains(sFormat)) {
			return JSON;
		}
		if(rdfxml2.contains(sFormat)) {
			return RDFXML;
		}
		if(uri2.contains(sFormat)) {
			return URI;
		}
		if(aleph2.contains(sFormat)) {
			return ALEPH;
		}
		if(nif202.contains(sFormat)) {
			return NIFv20;
		}
		if(nif212.contains(sFormat)) {
			return NIFv21;
		}
		if(mp32.contains(sFormat)) {
			return MP3;
		}
		if(wav2.contains(sFormat)) {
			return WAV;
		}
		if(multi2.contains(sFormat)) {
			return MULTIPART;
		}
		throw new WorkflowException("Unsupported format: "+sFormat);
	}

	@Override
	public String toString() {
		return value;
	}
	
	public boolean isMultimedia() {
		return isMultimedia;
	}
}
