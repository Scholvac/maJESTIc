package de.sos.script.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.sos.script.ast.util.Scope;
import de.sos.script.support.ICompletionFilter;
import de.sos.script.support.completions.ICompletion;

public class UnknownStatement extends ASTNode {

	private String mSource;
	private String mType;

	public UnknownStatement(ASTNode parent, int start, int end) {
		super(parent, start, end);
	}
	
	public void setType(String type) {
		mType = type;
	}
	public void setSource(String source) {
		mSource = source;
	}
	

	@Override
	public String toString() {
		return printInterval("UnknownStatement - " + mType);
	}

	@Override
	public List<Class<? extends ASTNode>> getAllowedChildren() {
		return new ArrayList<>();
	}
	
	
	@Override
	public void getCompletionsAndFilter(String statement, boolean hasDotAtEnd, Scope scope, Set<ICompletion> completions, Set<ICompletionFilter> filter) {
		if (getParent() != null) {
			getParent().getCompletionsAndFilter(statement, hasDotAtEnd, scope, completions, filter);
			return ;
		}
		super.getCompletionsAndFilter(statement, hasDotAtEnd, scope, completions, filter);
	}
}
