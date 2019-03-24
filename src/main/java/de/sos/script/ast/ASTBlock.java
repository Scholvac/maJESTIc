package de.sos.script.ast;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import de.sos.script.ast.util.Scope;
import de.sos.script.support.CompletionUtils;
import de.sos.script.support.ICompletionFilter;
import de.sos.script.support.completions.ICompletion;

public class ASTBlock extends ASTNode implements IScopeProvider {

	
	private Scope				mScope = null;
	
	public ASTBlock(ASTNode parent, int start, int end) {
		super(parent, start, end);
	}

	
	@Override
	public String toString() {
		return printInterval("Block");
	}

	
	
	@Override
	public Scope getScope() {
		if (mScope == null)
			mScope = new Scope(this);
		return mScope;
	}

//	@Override
//	public IScope getParentScope() {
//		if (getParent() != null)
//			return getParent().getScope();
//		return null;
//	}
	
	@Override
	public List<Class<? extends ASTNode>> getAllowedChildren() {
		return Arrays.asList(
				ASTName.class, 
				ASTPropAccess.class, 
				ASTFuncCall.class,
				ASTFuncDecl.class,
				ASTTypeDef.class,
				ASTAssign.class);
	}
	
	
	@Override
	public void getCompletionsAndFilter(String statement, boolean hasDotAtEnd, final Scope scope, Set<ICompletion> completions, Set<ICompletionFilter> filter) {
		CompletionUtils.completionsFromNamedElement(scope.getAllNamedElements(), completions);
	}
	
//	@Override
//	public IType _saveGetType(final Set<ASTNode> set) {
//		if (set.contains(this))
//			return null;
//		return null;
//	}
}
