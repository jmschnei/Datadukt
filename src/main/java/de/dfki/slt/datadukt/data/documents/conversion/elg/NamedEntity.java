package de.dfki.slt.datadukt.data.documents.conversion.elg;

import java.util.Map;

public class NamedEntity {
    public final int start;
    public final int end;
    public final Map<String, String> features;

    public NamedEntity(int start, int end, Map<String, String> features) {
        this.start = start;
        this.end = end;
        this.features = features;
    }

    public String getName() {
        return features.get("name");
    }
}
