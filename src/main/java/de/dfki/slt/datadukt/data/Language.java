/**
 * 
 */
package de.dfki.slt.datadukt.data;

import java.util.Arrays;
import java.util.List;

/**
 * @author julianmorenoschneider
 * @project CurationWorkflowManager
 * @date 07.02.2020
 * @company DFKI
 * @description 
 * @modified_by
 * @version 1.0
 * 
 */
public enum Language {
	EN, 
	ES, 
	DE, 
	FR, 
	IT, 
	NL, 
	PT, 
	UNK;

	public static final String[] ENGLISH = {"EN","english","en","eng","ENG","English","inglés","ingles"};
	public static final List<String> ENGLISH2 = Arrays.asList("EN","english","en","eng","ENG","English","inglés","ingles");
	public static final String[] SPANISH = {"ES","spanish","es","spa","SPA","SP","sp","Spanish","español","Español"};
	public static final List<String> SPANISH2 = Arrays.asList("ES","spanish","es","spa","SPA","SP","sp","Spanish","español","Español");
	public static final String[] GERMAN = {"DE","german","de","deu","DEU","German","Deutsch","deutsch"};
	public static final List<String> GERMAN2 = Arrays.asList("DE","german","de","deu","DEU","German","Deutsch","deutsch");
	public static final String[] FRENCH = {"FR","french","fr","fre","FRE","French","francés","Francés"};
	public static final List<String> FRENCH2 = Arrays.asList("FR","french","fr","fre","FRE","French","francés","Francés");
	public static final String[] ITALIAN = {"IT","italian","it","ita","ITA","Italian","italiano","Italiano"};
	public static final List<String> ITALIAN2 = Arrays.asList("IT","italian","it","ita","ITA","Italian","italiano","Italiano");
	public static final String[] DUTCH = {"NL","dutch","nl","nld","NLD","Dutch","holandés","Holandés"};
	public static final List<String> DUTCH2 = Arrays.asList("NL","dutch","nl","nld","NLD","Dutch","holandés","Holandés");
	public static final String[] PORTUGUESE = {"PT","portuguese","pt","ptg","PTG","Portuguese","portugués","Portugués"};
	public static final List<String> PORTUGUESE2 = Arrays.asList("PT","portuguese","pt","ptg","PTG","Portuguese","portugués","Portugués");
	
	/**
	 * @method-description  Translate a string into a enum element
	 * @author  			julianmorenoschneider
	 * @modified_by
	 * @date  				07.02.2020
	 * @return  			Enum type of the corresponding language
	 */
	public static Language getLanguage(String slang){
		if(SPANISH2.contains(slang)) {
			return ES;
		}
		for(String sl : ENGLISH){
			if(sl.equalsIgnoreCase(slang)){
				return EN;
			}
		}
		for(String sl : GERMAN){
			if(sl.equalsIgnoreCase(slang)){
				return DE;
			}
		}
		for(String sl : FRENCH){
			if(sl.equalsIgnoreCase(slang)){
				return FR;
			}
		}
		for(String sl : ITALIAN){
			if(sl.equalsIgnoreCase(slang)){
				return IT;
			}
		}
		for(String sl : DUTCH){
			if(sl.equalsIgnoreCase(slang)){
				return NL;
			}
		}
		for(String sl : PORTUGUESE){
			if(sl.equalsIgnoreCase(slang)){
				return PT;
			}
		}
		return UNK;
	}
	
}

