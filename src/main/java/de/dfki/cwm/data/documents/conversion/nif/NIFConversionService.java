package de.dfki.cwm.data.documents.conversion.nif;

import java.io.StringReader;
import java.io.StringWriter;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

public interface NIFConversionService {
	
	static String defaultPrefix = LYNXNIF.getDefaultPrefix();

	static JenaRDFConversionService jena = new JenaRDFConversionService();

	/*
	 * Convert NifDocument to String
	 */
	public static String serializeNIF(NifDocument doc, String format) throws Exception {
		String jenaIdentifier = JenaRDFConversionService.getJenaType(format);
		if (jenaIdentifier == null) {
			throw new Exception("unsupported format: " + format);
		}
		StringWriter writer = new StringWriter();
		Model model = doc.getModel();
		model.write(writer, jenaIdentifier);
		writer.close();
		return writer.toString();
	}

	public static String serializeNIFModel(Model doc, String format) throws Exception {
		String jenaIdentifier = JenaRDFConversionService.getJenaType(format);
		if (jenaIdentifier == null) {
			throw new Exception("unsupported format: " + format);
		}
		StringWriter writer = new StringWriter();
		doc.write(writer, jenaIdentifier);
		writer.close();
		return writer.toString();
	}

	/*
	 * Convert String to NifDocument
	 */
	public static NifDocument unserializeNIF(String rdf, String format) throws Exception {
		if(format.equalsIgnoreCase("text/plain")) {
			return plaintextToNIF(rdf, null, "2.1", null);
		}
		String jenaIdentifier = JenaRDFConversionService.getJenaType(format);
		if (jenaIdentifier == null) {
			throw new Exception("unsupported format: " + format);
		}
		Model model = ModelFactory.createDefaultModel();
		StringReader reader = new StringReader(rdf);
		model.read(reader, null, jenaIdentifier);
		
		NifDocument nifdoc = new NifDocument();
		nifdoc.setModel(model);
		return nifdoc;
	}
	
	/*
	 * Convert String to Jena Model
	 */
	public static NifDocument unserializeRDF(String rdf, String format, String prefix) throws Exception {
		if(format.equalsIgnoreCase("text/plain")) {
			return plaintextToNIF(rdf, null, "2.1", prefix);
		}
		String jenaIdentifier = JenaRDFConversionService.getJenaType(format);
		if (jenaIdentifier == null) {
			throw new Exception("unsupported format: " + format);
		}
		Model model = ModelFactory.createDefaultModel();
		StringReader reader = new StringReader(rdf);
		model.read(reader, null, jenaIdentifier);
		
		NifDocument nifdoc = new NifDocument();
		nifdoc.setModel(model);
		
		return nifdoc;
	}
	
	@SuppressWarnings("deprecation")
	public static NifDocument plaintextToNIF(String inputText, String language, String nifVersion, String prefix) {
		Model model = ModelFactory.createDefaultModel();
//		model.setNsPrefix("nif", nifPrefix);
//		model.setNsPrefix("xsd", xsdPrefix);
        model.setNsPrefixes(NIFTransferPrefixMapping.getInstance());

        int endTotalText = inputText.codePointCount(0, inputText.length());
        
		String uri = null;
		if(prefix==null) {
			uri = LYNXNIF.getDefaultPrefix();
		}
		else {
			uri = prefix;
		}
		
		NifDocument nifdoc = new NifDocument();
		
		//TODO
		
        String documentUri = NIFUriHelper.getNifUri(uri, 0, endTotalText, nifVersion);
        
        Resource documentResource = model.createResource(documentUri);
        model.add(documentResource, RDF.type, NIF.Context);
        model.add(documentResource, RDF.type, NIF.String);
        model.add(documentResource, RDF.type, NIF.RFC5147String);
		if (language == null) {
	        model.add(documentResource, NIF.isString, 
	        		model.createTypedLiteral(inputText, XSDDatatype.XSDstring));
		} else {
	        model.add(documentResource, NIF.isString, 
	        		model.createTypedLiteral(inputText, language));
		}
        model.add(documentResource, NIF.beginIndex,
                model.createTypedLiteral(new Integer(0), XSDDatatype.XSDnonNegativeInteger));
        model.add(documentResource, NIF.endIndex,
                model.createTypedLiteral(new Integer(endTotalText), XSDDatatype.XSDnonNegativeInteger));
		return nifdoc;
	}

}
