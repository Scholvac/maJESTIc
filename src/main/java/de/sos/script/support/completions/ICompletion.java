package de.sos.script.support.completions;

import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;

public interface ICompletion {
	
	/** Returns the (complete) string that shall be added as completion. 
	 * @note the text does not need to consider the already entered text
	 * @return
	 */
	String getFeatureString();
	
	/** 
	 * Returns the type of the feature as String.
	 * for example double, String, ICompletion, ...	 
	 * @return
	 */
	String getTypeAsString();
	
	/** Returns the information where the feature is defined. 
	 * In most cases that the owning classifier (Class, Interface, Enum, ...)
	 * @return
	 */
	String getDefinedIn();
	
	/** Returns a human readable description of the feature.
	 * 
	 * @return
	 */
	String getDescription();

	default Completion getUICompletion(CompletionProvider provider) {
		String replacementText = getFeatureString();
		String shortDesc = "<html><b>" + getTypeAsString() + "</b> - <i>" + getDefinedIn() + "</i></html>";
		String summary = "<html><i>" + getDescription() + "</i></html>";
		BasicCompletion bc = new BasicCompletion(provider, replacementText, shortDesc, summary);
		return bc;
	}
	
	
	
}
