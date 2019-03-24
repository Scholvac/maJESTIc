package de.sos.script.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.sos.script.ast.ASTNode.ASTNamedNode;
import de.sos.script.ast.util.Scope;
import de.sos.script.support.CompletionUtils;
import de.sos.script.support.ICompletionFilter;
import de.sos.script.support.completions.BaseCompletion;
import de.sos.script.support.completions.ICompletion;

public class ASTName extends ASTNamedNode {

	public ASTName(ASTNode parent, String name, int start, int end) {
		super(parent, name, start, end);
	}
	
	@Override
	public String toString() {
		return printInterval("Name (" + getName() + ")");
	}
	
	@Override
	public List<Class<? extends ASTNode>> getAllowedChildren() {
		return new ArrayList<>();
	}
	
	@Override
	public IType getType(final Scope ns) {
		if (ns == null) 
			return null;
		INamedElement ne = ns.getNamedElement(getName());
		if (ne == null || ne == this)
			return null;
		if (ne instanceof ASTNode)
			return ((ASTNode)ne).getType(ns);
		return null;
	}

	@Override
	public void getCompletionsAndFilter(final String statement, final boolean hasDotAtEnd, final Scope scope, final Set<ICompletion> completions, final Set<ICompletionFilter> filter) {
		final String thisName = getName();
		if (thisName == null || thisName.isEmpty()) {
			CompletionUtils.completionsFromNamedElement(scope.getAllNamedElements(), completions);
			return ;
		}else {
			INamedElement scope_ne = scope.getNamedElement(thisName);
			if (scope_ne != null && scope_ne instanceof ASTNode) {
				if (hasDotAtEnd == false) {
					completions.add(new BaseCompletion(".", null, null, null) );
				}else {
					//the element exists
					ASTNode ne = (ASTNode)scope_ne;
					IType type = scope.getTypeOf(ne);
					if (type == null) {
						CompletionUtils.completionsFromNamedElement(scope.getAllNamedElements(), completions);
					}else {
						type.insertCompletions(completions);
					}
				}
			}else {
				//the element does not exists, thus we take all named elements but add a name filter
				CompletionUtils.completionsFromNamedElement(scope.getAllNamedElements(), completions);
				CompletionUtils.addNameFilter(thisName, filter);
			}
		}
	}

}
