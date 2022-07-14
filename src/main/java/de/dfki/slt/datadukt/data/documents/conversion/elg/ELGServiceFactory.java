package de.dfki.slt.datadukt.data.documents.conversion.elg;

import com.squareup.moshi.Moshi;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class ELGServiceFactory {

    private ELGServiceFactory() {}

    private final static String BASE_URL = "https://live.european-language-grid.eu";

    public static ELGService createELGService() {
        Moshi moshi = new Moshi.Builder().build();
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build();
        return retrofit.create(ELGService.class);
    }

}
