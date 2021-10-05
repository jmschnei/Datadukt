package de.dfki.cwm.data.documents.conversion.elg;

import java.util.List;
import java.util.Map;

public class NERAnnotations {
    public final Map<String, List<NamedEntity>> annotations;

    public NERAnnotations(Map<String, List<NamedEntity>> annotations) {
        this.annotations = annotations;
    }
}
