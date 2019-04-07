package de.sos.script;

public interface IScriptSource {

	/** Returns the content of the script as "compilable"/"interpretable" String. 
	 * 
	 * @return the source to be compiled
	 */
	String getContentAsString();

	/**
	 * Returns the name of the script language (usually defined in one of the script manager)
	 * @return
	 */
	String getLanguage();

	/** returns an unique identifier for this script */
	String getIdentifier();

}