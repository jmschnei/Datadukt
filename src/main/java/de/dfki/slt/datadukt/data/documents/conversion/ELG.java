package de.dfki.slt.datadukt.data.documents.conversion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.dfki.slt.datadukt.data.documents.Label;
import de.dfki.slt.datadukt.data.documents.LabelPositionAnnotation;
import de.dfki.slt.datadukt.data.documents.WMDocument;
import de.dfki.slt.datadukt.data.documents.conversion.elg.AnnotationsResponse;
import de.dfki.slt.datadukt.data.documents.conversion.elg.ELGService;
import de.dfki.slt.datadukt.data.documents.conversion.elg.ELGServiceFactory;
import de.dfki.slt.datadukt.data.documents.conversion.elg.NERAnnotations;
import de.dfki.slt.datadukt.data.documents.conversion.elg.NamedEntity;
import retrofit2.Call;
import retrofit2.Response;

public class ELG {

	public static Map<String, String> dbpediaRefs = new HashMap<String, String>();

	// todo: actual mapping between output NER labels and dbpedia refs
	static {
		dbpediaRefs.put("Organization", "http://dbpedia.org/ontology/Organisation");
//		dbpediaRefs.put("Person", "");
		dbpediaRefs.put("People", "http://dbpedia.org/ontology/Person");
		dbpediaRefs.put("Date", "http://dbpedia.org/ontology/date");
//		dbpediaRefs.put("Location", "");
		dbpediaRefs.put("Place", "http://dbpedia.org/ontology/Place");
	}

	public static void main(String[] args) throws Exception {
		ELGService elgService = ELGServiceFactory.createELGService();
		String sampleText = "Bill Clinton is a member of The Eagles. Google organized a scientific conference in New-York on Wednesday, 31th of October.";
		String lang = "en";
		Call<AnnotationsResponse> testCall = elgService.getAnnotations(sampleText);
		Response<AnnotationsResponse> testResponse = testCall.execute();
		AnnotationsResponse response = testResponse.body();
		if (response != null) {
			NERAnnotations nerAnnotations = response.response;
			WMDocument qd = fromNERAnnotations(nerAnnotations, sampleText, lang);
			System.out.println(qd.toString());
		}
		else{
			System.out.println(testResponse.code());
		}
	}

	public static WMDocument fromNERAnnotations(NERAnnotations nerAnnotations, String text, String lang) {
		WMDocument qd = new WMDocument("http://speaker-project.de/res/56b56027","56b56027", text, lang);

		Map<String, List<NamedEntity>> annotations = nerAnnotations.annotations;
		for (String key: annotations.keySet()) {

			for (NamedEntity ne: annotations.get(key)) {
				LabelPositionAnnotation lpa = new LabelPositionAnnotation("http://document1.org/res/", ne.start, ne.end, text.substring(ne.start, ne.end));

				Label label = new Label();
				label.add("taClassRef", dbpediaRefs.get(key));
				//l.add("taIdentRef", "http://dbpedia.org/resource/Berlin");
				for (Map.Entry<String, String> entry : ne.features.entrySet()) {
					label.add(entry.getKey(), entry.getValue());
				}

				lpa.addLabel(label);
				qd.addAnnotation(lpa);
			}
		}
    	return qd;
	}
}
