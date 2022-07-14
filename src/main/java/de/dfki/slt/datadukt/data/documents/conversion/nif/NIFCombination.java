package de.dfki.slt.datadukt.data.documents.conversion.nif;

import org.apache.jena.rdf.model.Model;

public class NIFCombination {

	public static Model combineNIFModels(NifDocument doc1,NifDocument doc2){
		try {
//			String isString1 = doc1.getIsString();
//			String isString2 = doc2.getIsString();
//			if(!doc1.getIsString().equals(doc2.getIsString())) {
//				throw new Exception("The content of both NIF Documents (in isString) is different. They can not be combined.");
//			}
//			
//			if(!doc1.getUri().equals(doc2.getUri())) {
//				String content = doc2.NIFConverter.serializeRDF(inputModel2, "text/turtle");
//				content = content.replaceAll(documentURI2, documentURI1);
//				inputModel2 = NIFConverter.unserializeRDF(content, "text/turtle");
//			}
//			doc1.combineDocument(doc2);
//			return doc1;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

//	public static void main(String[] args) throws Exception {
//		String content1 = "Welcome in Berlin in 2018";
//		String prefix1 = "http://lynx-project.eu/ontologies/documents/doc1";
//		Model model1 = NIFConverter.plaintextToNIF(content1, null, "2.1", prefix1);
//		NIFWriter.addAnnotationEntity(model1, 1, 10, content1.substring(1, 10), null, DBO.person.getURI());
////		System.out.println(NIFConverter.serializeRDF(model1, "text/turtle"));
////		System.out.println(".-----------------");
//		
//		String content2 = "Welcome in Berlin in 2018";
//		String prefix2 = "http://lynx-project.eu/ontologies/documents/doc2";
//		Model model2 = NIFConverter.plaintextToNIF(content2, null, "2.1", prefix2);
//		int start2 = 4;
//		int end2 = 14;
//		NIFWriter.addAnnotationEntity(model2, start2, end2, content2.substring(start2, end2), null, DBO.person.getURI());
//		
////		System.out.println(NIFConverter.serializeRDF(model2, "text/turtle"));
////		System.out.println(".-----------------");
//		Model output = NIFCombination.combineNIFModels(model1, model2);
////		System.out.println(NIFConverter.serializeRDF(output, "text/turtle"));
//	}

}
