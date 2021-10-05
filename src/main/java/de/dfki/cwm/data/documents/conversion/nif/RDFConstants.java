package de.dfki.cwm.data.documents.conversion.nif;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Jan Nehring - jan.nehring@dfki.de
 */
public class RDFConstants {

	// nif versions
	public static final String nifVersion2_0 = "2.0";
	public static final String nifVersion2_1 = "2.1";

	//There is only one prefix for nifVersion 2.0 and 2.1
	public static final String nifPrefix = "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#";

	public static final String itsrdfPrefix = "http://www.w3.org/2005/11/its/rdf#";
	public static final String xsdPrefix = "http://www.w3.org/2001/XMLSchema#";
	public static final String typePrefix = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
	public static final String dcPrefix = "http://purl.org/dc/elements/1.1/";

	public static final String lynxPrefix = "http://lynx-project.eu/";

	// used in a nif 2.1 file, i.e. nif:predLang isolang:eng
	public static final String isolangPrefix = "http://www.lexvo.org/id/iso639-3/";

	public static final String nifAnnotationPrefix = "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-annotation#";

	// Properties
	public static final String IS_STRING = "isString";
	public static final String ANCHOR_OF = "anchorOf";
	public static final String BEGIN_INDEX = "beginIndex";
	public static final String END_INDEX = "endIndex";
	public static final String IDENTIFIER = "identifier";
	public static final String PRED_LANG = "predLang";
	public static final String ANNOTATION_UNIT = "annotationUnit";
	public static final String REFERENCE_CONTEXT = "referenceContext";
	public static final String WAS_CONVERTED_FROM = "wasConvertedFrom";
	public static final String TARGET = "target";
	public static final String TA_ANNOTATORS_REF = "taAnnotatorsRef";

	public static final String NIF20_OFFSET = "#char=";
	public static final String NIF21_OFFSET = "#offset_";

	// These should not be used anymore as the nifPrefix can be one of nifPrefix_2_0 and nifPrefix
	@Deprecated
	public static final String WAS_CONVERTED_FROM_PROP = nifPrefix + WAS_CONVERTED_FROM;
	@Deprecated
	public static final String IS_STRING_PROP = nifPrefix + IS_STRING;
	@Deprecated
	public static final String ANCHOR_OF_PROP = nifPrefix + ANCHOR_OF;

	// Types
	public static final String NIF20_STRINGS_IDENTIFIER = "RFC5147String";
	public static final String NIF21_STRINGS_IDENTIFIER = "OffsetBasedString";
	public static final String NIF_STRING_TYPE = "String";
	public static final String NIF_CONTEXT_TYPE = "Context";
	public static final String NIF_PHRASE_TYPE = "Phrase";

	// Prefixes
	public static final String NIF_PREFIX = "nif";
	public static final String ISOLANG_PREFIX = "isolang";
	public static final String XSD_PREFIX = "xsd";
	public static final String ITS_RDF_PREFIX = "itsrdf";
	public static final String DC_PREFIX = "dc";

	// Serialization formats
	public static final String TURTLE = "text/turtle";
	public static final String JSON_LD = "application/ld+json";
	public static final String RDF_XML = "application/rdf+xml";
	public static final String N3 = "text/n3";
	public static final String N_TRIPLES = "application/n-triples";

	public static final Set<String> SERIALIZATION_FORMATS = new HashSet<>(Arrays.asList(new String[]{
			TURTLE, JSON_LD, RDF_XML, N3, N_TRIPLES
	}));

	/**
	 */
	public enum RDFSerialization {
		TURTLE("text/turtle"),
		JSON_LD("application/ld+json"),
		PLAINTEXT("text/plain"),
		RDF_XML("application/rdf+xml"),
		N3("text/n3"),
		N_TRIPLES("application/n-triples"),
		JSON("application/json"),
		HTML("text/html"),
		CSV("text/comma-separated-values"),
		XML("text/xml");

		private final String contentType;

		RDFSerialization(String contentType) {
			this.contentType = contentType;
		}

		public String contentType() {
			return contentType;
		}

		/**
		 * Given a textual content type, return its RDFSerialization object.
		 * @param contentType	The content type, in textual format.
		 * @return				The corresponding RDFSerialization object, or {@code null} if nothing found
		 */
		public static RDFSerialization fromValue(final String contentType) {
			String normalizedContentType = contentType.toLowerCase();

			// chop off everything beginning from ';'. An example is "text/turtle; charset=UTF-8"
			int indexOfSemicolon = normalizedContentType.indexOf(';');
			if (indexOfSemicolon >= 0) {
				normalizedContentType = normalizedContentType.substring(0, indexOfSemicolon);
			}

			// now find the matching value
			for (RDFSerialization rdfSerialization : RDFSerialization.values()) {
				if (rdfSerialization.contentType().equals(normalizedContentType)) {
					return rdfSerialization;
				}
			}
			return null;
		}
	}
}