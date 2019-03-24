package de.sos.script.ast;

import java.util.Set;

import de.sos.script.support.completions.ICompletion;

public interface INamedElement {

	String getName();

	void insertCompletions(Set<ICompletion> completions);
}
