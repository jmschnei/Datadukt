package de.dfki.slt.datadukt.data.documents.conversion.nif;

public class NIFUriHelper {

    /**
     * Transforms the NIF URI
     * <code>http://example.org/document_1#char=0,120</code> into the document
     * URI <code>http://example.org/document_1</code>.
     * 
     * @param nifUri
     *            the URI the document has inside the NIF model
     * @return the documents URI without the character position information
     */
    public static String getDocumentUriFromNifUri(String nifUri, String nifVersion) {
        if(nifVersion==null || nifVersion.equals(RDFConstants.nifVersion2_1)) {
    		return getDocumentUriFromNifUri_2_1(nifUri);
    	}
    	else if(nifVersion.equals(RDFConstants.nifVersion2_1)) {
    		return getDocumentUriFromNifUri_2_0(nifUri);
    	}
    	else {
    		try {
    			throw new Exception ("Unsupported nif version: "+nifVersion);
    		}
    		catch(Exception e) {
    			e.printStackTrace();
    		}
    		return null;
    	}
    }

    public static String getDocumentUriFromNifUri_2_0(String nifUri) {
        int pos = nifUri.lastIndexOf('#');
        if (pos > 0) {
            return nifUri.substring(0, pos);
        } else {
            return nifUri;
        }
    }

    public static String getDocumentUriFromNifUri_2_1(String nifUri) {
        int pos = nifUri.lastIndexOf("_offset");
        if (pos > 0) {
            return nifUri.substring(0, pos);
        } else {
            return nifUri;
        }
    }

	public static String getNifUri(String documentURI, int start, int end) {
		return getNifUri(documentURI, start, end, "2.1");
	}

    public static String getNifUri(String documentURI, int start, int end, String nifVersion) {
    	if(nifVersion==null || nifVersion.equals(RDFConstants.nifVersion2_1)) {
    		return getUri_Nif_2_1(documentURI, start, end);
    	}
    	else if(nifVersion.equals(RDFConstants.nifVersion2_1)) {
    		return getUri_Nif_2_0(documentURI, start, end);
    	}
    	else {
    		try {
    			throw new Exception ("Unsupported nif version: "+nifVersion);
    		}
    		catch(Exception e) {
    			e.printStackTrace();
    		}
    		return null;
    	}
    }

    public static String getUri_Nif_2_0(String documentURI, int start, int end) {
        StringBuilder uriBuilder = new StringBuilder();
        uriBuilder.append(documentURI);
        uriBuilder.append("#char=");
        uriBuilder.append(start);
        uriBuilder.append(',');
        uriBuilder.append(end);
        return uriBuilder.toString();
    }

    public static String getUri_Nif_2_1(String documentURI, int start, int end) {
        StringBuilder uriBuilder = new StringBuilder();
        uriBuilder.append(documentURI);
        uriBuilder.append("_offset_");
        uriBuilder.append(start);
        uriBuilder.append('_');
        uriBuilder.append(end);
        return uriBuilder.toString();
    }

    public static String generateDocumentUri(String text) {
    	String number = org.apache.commons.codec.digest.DigestUtils.sha256Hex(text);   
    	return number;
    }
}
