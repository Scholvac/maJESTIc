package de.sos.script.support;

import java.util.List;

import de.sos.script.support.completions.ICompletion;

public interface ICompletionFilter {

	List<ICompletion> filter(final List<ICompletion> completions);
	
}