/**
 * 
 */
package de.dfki.cwm.data.documents;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;

import de.dfki.cwm.data.documents.conversion.WMSerialization;

/**
 * @author julianmorenoschneider
 * @project java
 * @date 22 Feb 2020
 * @date_modified 01 Apr 2020
 * @company DFKI
 * @description 
 * 
 */
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true, value={"context", "offset_end"}, allowGetters = true)
@JsonPropertyOrder({"@context", "id", "type"})
public class WMDocument {

	/// Identifier. This must be a URI.
	@JsonProperty("id")
	@JsonAlias( {"@id"} )
	private String id;

	/// Text of the document. Only one.
	@JsonProperty("text")
	private String text;

	/// Begin index of the string
	@JsonProperty("offset_ini")
	private int offsetIni = 0;

	/// End index of the string
	@JsonProperty("offset_end")
	private int offsetEnd = 0;

	/// Types of the document. http://lynx-project.eu/def/lkg/LynxDocument, but also possibly more.
	@JsonProperty("type")
	@JsonAlias({"@type"})
	private List<String> types = new ArrayList<String>();

	/// Parts of the document. 
	private List<WMDocumentPart> parts = new ArrayList<WMDocumentPart>();

	/// Metadata elements: version, author, date, etc.
	private Map<String, Object> metadata = new HashMap<String, Object>();

	/// Annotations
	@JsonProperty("annotations")
	protected List<BaseAnnotation> annotations = new ArrayList<BaseAnnotation>();

////  We could also use List<LynxAnnotationUnit>, but this brings some problems in occasions to be further investigated......
//	@JsonProperty("labelUnits")
//    public List<Label> labels;

	/// Generations: translations, summaries, new pieces of information included in the document.
	@JsonProperty("document_annotations")
	protected List<BaseAnnotation> docAnnotations = new ArrayList<BaseAnnotation>();

	public WMDocument() {
		String uid = UUID.randomUUID().toString().substring(0,8);
		setId("http://qurator-project.de/res/"+uid);
		metadata.put("language", "en");
		metadata.put("id_local", uid);
		getType();
	}

	/**
	 * Default constructor. 
	 * Two fields are mandatory if not provided, they are created randomly to grant a valid LynxDocument.
	 */
	public WMDocument(String text) {
		String uid = UUID.randomUUID().toString().substring(0,8);
		setId("http://qurator-project.de/res/"+uid);
		metadata.put("language", "en");
		metadata.put("id_local", uid);
		setText(text);
		getType();
		offsetEnd = text.length();
	}

	/**
	 * Minimal constructor.
	 * @param id Identifier. This must be a URI.
	 * @param idlocal Local  Identifier, which does not need to be a URI.
	 * @param text Text of the document.
	 * @param lan Language. ISO code, 2 letters.
	 */
	public WMDocument(String id, String idlocal, String text, String lan) {
		setText(text);
		setId(id);
		metadata.put("language", lan);
		metadata.put("id_local", idlocal);
		getType();
		offsetEnd = text.length();
	}

	/**
	 * Copy constructor.
	 */
	public WMDocument(WMDocument doc) {
		setText(doc.getText());
		setId(doc.getId());
//		setTranslations(doc.getTranslations());
		setType(doc.getType());
		setMetadata(doc.getMetadata());
		setParts(doc.getParts());
		setAnnotations(doc.getAnnotations());
		setDocumentAnnotations(doc.getDocumentAnnotations());
		offsetEnd = text.length();
	}

    @JsonProperty("@context")
    public String getContext() {
        return "http://qurator.de/doc/jsonld/quratordocument.json";
    }

	/**
	 * @return the offsetIni
	 */
	@JsonProperty("offset_ini")
	public int getOffsetIni() {
		return offsetIni;
	}

	/**
	 * @param offsetIni the offsetIni to set
	 */
	@JsonSetter
	public void setOffsetIni(int offsetIni) {
		this.offsetIni = offsetIni;
	}

	/**
	 * @param offsetEnd the offsetEnd to set
	 */
	@JsonSetter
	public void setOffsetEnd(int offsetEnd) {
		this.offsetEnd = offsetEnd;
	}

	@JsonProperty("offset_end")
	public int getOffsetEnd() {
		return text.length();
	}

	@JsonGetter
	public String getId() {
		return id;
	}

	@JsonSetter
	public void setId(String id) {
		this.id = id;
		for (WMDocumentPart part : parts) {
			part.referenceContext = this.getId();
			part.setId(part.generateId(part.referenceContext));
		}

		// TODO: Change referenceContext from all annotations and parts.
	}

	public List<String> getType() {
		if (!types.contains("nif:Context")) { types.add("nif:Context"); }
		if (!types.contains("nif:OffsetBasedString")) { types.add("nif:OffsetBasedString"); }
		if (!types.contains("qont:QuratorDocument")) { types.add("qont:QuratorDocument"); }
		return types;
	}

	public void setType(List<String> type) {
		this.types = type;
	}

	/**
	 * Adds a new type to a QuratorDocument
	 *
	 * @param _ype Adds a type to a QuratorDocument
	 */
	public void addType(String type) {
		this.types.add(type);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
		offsetEnd = text.length();
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

	public List<BaseAnnotation> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(List<BaseAnnotation> annotations) {
		this.annotations = annotations;
	}

	/**
	 * Updates an existing annotation. If the annotation does not exist, the new
	 * one will not be added
	 *
	 * * @param QuratorAnnotation The annotation
	 * @return true if there was effectively an update.
	 */
	public boolean updateAnnotation(BaseAnnotation newAnnotation) {
		String newannid = newAnnotation.getId();
		int tam = annotations.size();
		boolean found = false;
		for (int i = 0; i < tam; i++) {
			BaseAnnotation ann = annotations.get(i);
			if (ann.getId().equals(newannid)) {
				annotations.remove(i);
				found = true;
				break;
			}
		}
		if (found) {
			annotations.add(newAnnotation);
		}
		return found;
	}

	public boolean deleteAnnotation(String annotationId) {
		int tam = annotations.size();
		for (int i = 0; i < tam; i++) {
			BaseAnnotation ann = annotations.get(i);
			if (ann.getId().equals(annotationId)) {
				annotations.remove(i);
				return true;
			}
		}
		return false;
	}

	/**
	 * Adds an annotation. 
	 * The id of the annotation will be overriden.
	 * @param ann Annotation to be added
	 */
	public void addAnnotation(BaseAnnotation ba)
	{
		ba.referenceContext = getId();
		ba.setId(ba.generateId(ba.referenceContext));
		annotations.add(ba);
	}

	@JsonIgnore
	public List<BaseAnnotation> getDocumentAnnotations() {
		return docAnnotations;
	}

	public void setDocumentAnnotations(List<BaseAnnotation> generations) {
		this.docAnnotations = generations;
	}
	
	public void addDocumentAnnotation(BaseAnnotation gen) {
//		gen.setId(this.id);
		docAnnotations.add(gen);
	}

	public List<WMDocumentPart> getParts() {
		return parts;
	}

	/**
	 */
	public void setParts(List<WMDocumentPart> parts) {
		for (WMDocumentPart part : parts) {
			part.referenceContext = this.getId();
			part.setId(part.generateId(part.referenceContext));
			this.parts.add(part);
		}
	}

	public void addPart(WMDocumentPart part) {		
		part.referenceContext = getId();
		part.setId(part.generateId(part.referenceContext));
		parts.add(part);
	}
	
	/**
	 * Comparison method
	 * TODO Probably the comparison is not complete. Some more fields have to be compared.
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
//			System.out.println("The same");
			return true;
		}
		if (!(o instanceof WMDocument)) {
//			System.out.println("Not an instance");
			return false;
		}
		WMDocument that = (WMDocument) o;

		InputStream isThat = new ByteArrayInputStream(that.toRDF("TURTLE").getBytes());
		Model modelThat = ModelFactory.createDefaultModel();
		modelThat.read(isThat, null,"TTL");

		InputStream isThis = new ByteArrayInputStream(this.toRDF("TURTLE").getBytes());
		Model modelThis = ModelFactory.createDefaultModel();
		modelThis.read(isThis, null,"TTL");

//		model1.write(System.out, "TTL");
//		model2.write(System.out, "TTL");
//		
		if(modelThis.isIsomorphicWith(modelThat)) {
			return true;
		}
		else {
			return false;
		}
//		System.out.println(model1.isIsomorphicWith(model2));

		
////		System.out.println("Instance but ...");
////		System.out.println(Objects.equals(getId(), that.getId()));
////		System.out.println(Objects.equals(getText(), that.getText()));
////		System.out.println(Objects.equals(getMetadata(), that.getMetadata()));
//		if(Objects.equals(getId(), that.getId())
//				&& Objects.equals(getText(), that.getText())) {
//
////			Set<String> keys = metadata.keySet();
////			Set<String> keys2 = that.getMetadata().keySet();
////			System.out.println("Keys1 size: " + keys.size());
////			System.out.println("Keys1 size: " + keys.size());
////			System.out.println(Objects.equals(keys, keys2));
////			Collection<Object> objs = metadata.values();
////			Collection<Object> objs2 = that.getMetadata().values();
////			System.out.println("objs1 size: " + objs.size());
////			System.out.println("objs2 size: " + objs2.size());
////			System.out.println(Objects.equals(objs, objs2));
////			for (String key : keys) {
////				System.out.println("\t" + metadata.get(key).toString());
////				System.out.println("\t" + that.getMetadata().get(key).toString());
////			}
//			return Objects.equals(getMetadata(), that.getMetadata());
//		}
//		else {
//			return false;
//		}
	}

	public String toJSON() {
		return toJSON(true);
	}
	
	public String toJSON(boolean explicitContext) {
		try {
			return WMSerialization.toJSON(this, explicitContext);
		}
		catch(Exception e) {
			e.printStackTrace();
			return "{}";
		}
	}
	
	public String toRDF(String syntax) {
		try {
			return WMSerialization.toRDF(this, syntax);
		}
		catch(Exception e) {
			e.printStackTrace();
			return "ERROR: "+e.getMessage();
		}
	}
	
    @Override
    public String toString() {
        return '{' +
                "id:'" + id + '\'' +
                ", types:" + types +
                ", text:'" + text + '\'' +
                ", offsetIni:'" + offsetIni + '\'' +
                ", offsetEnd:'" + offsetEnd + '\'' +
                ", annotations:'" + annotations.toString() + '\'' +
                ", parts:'" + parts.toString() + '\'' +
                ", textannotations:'" + docAnnotations.toString() + '\'' +
                '}';
    }

}
