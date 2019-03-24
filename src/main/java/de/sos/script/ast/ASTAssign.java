package de.sos.script.ast;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import de.sos.script.ast.ASTNode.ASTNamedNode;
import de.sos.script.ast.util.Scope;
import de.sos.script.support.CompletionUtils;
import de.sos.script.support.ICompletionFilter;
import de.sos.script.support.completions.BaseCompletion;
import de.sos.script.support.completions.ICompletion;

public class ASTAssign extends ASTNamedNode implements ITypeResolver, INamedElement {

	public ASTAssign(ASTNode parent, String name, int start, int end) {
		super(parent, name, start, end);
	}

	@Override
	public String toString() {
		return printInterval("Assign (" + getName() + ")");
	}
	
//	@Override
//	public IType _saveGetType(final Set<ASTNode> set) {
//		if (set.contains(this))
//			return null;
//		assert(getChildren() != null && getChildren().isEmpty() == false);
//		ASTNode child = getChildren().get(0);
//		if (child instanceof ITypeResolver)
//			return ((ITypeResolver) child).getType();
//		return null;
//	}
	
	@Override
	public IType getType(final Scope scope) {
		ASTNode child = getFirstChild();
		if (child != null)
			return child.getType(scope);
		return null;
	}
	
	@Override
	public void insertCompletions(Set<ICompletion> completions) {
		IType t = getType(getNodeScope());
		String typeStr = "Unknown";
		if (t != null)
			typeStr = t.getName();
		completions.add(new BaseCompletion(getName(), typeStr, "local", "no description available"));
	}

	@Override
	public List<Class<? extends ASTNode>> getAllowedChildren() {
		return Arrays.asList(
				ASTName.class, 
				ASTPropAccess.class,
				ASTFuncCall.class
				);
	}
	
	@Override
	public void getCompletionsAndFilter(String statement, boolean hasDotAtEnd, Scope scope, Set<ICompletion> completions, Set<ICompletionFilter> filter) {
		//TODO: we need to search for TypeDefs - but those are not that easy to detect for JS
		CompletionUtils.completionsFromNamedElement(scope.getAllNamedElements(), completions);
	}


}
