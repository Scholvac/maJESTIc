package de.sos.script.ast;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import de.sos.script.ast.ASTNode.ASTNamedNode;
import de.sos.script.ast.util.Scope;
import de.sos.script.support.CompletionUtils;
import de.sos.script.support.ICompletionFilter;
import de.sos.script.support.completions.ICompletion;

public class ASTPropAccess extends ASTNamedNode implements ITypeResolver{

	
	public ASTPropAccess(ASTNode parent, String name, int start, int end) {
		super(parent, name, start, end);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public String toString() {
		return printInterval("PropertyAccess (" + getName() + ")");
	}
	
	
	protected ASTNode getSourceNode() {
		assert(getChildren() != null && getChildren().isEmpty() == false);
		ASTNode child = getChildren().get(0);
		return child;
	}
	
	public IType getSourceType() {
//		ASTNode child = getSourceNode();
//		
//		IType type = null;
//		if (child instanceof IType)
//			type = (IType)child;
//		else if (child instanceof ITypeResolver)
//			type = ((ITypeResolver)child).getType();
//		else if (child instanceof ASTNamedNode) {
//			String n = ((ASTNamedNode) child).getName();
//			INamedElement ne = getScope().getNamedElement(n);
//			if (ne instanceof IType)
//				type = (IType)ne;
//			else if (ne instanceof ITypeResolver)
//				type = ((ITypeResolver)ne).getType();
//		}
//		return type;
		return null;
	}
	
//	@Override
//	public IType getType() {
//		IType srcType = getSourceType();
//		if (srcType != null){
//			ITypeResolver propType = srcType.getTypeResolver(getName(), null);
//			if (propType != null)
//				return propType.getType();
//		}
//		return null;
//	}

	private IType mType = null;
	
	@Override
	public IType getType(final Scope scope) {
		if (mType == null)
			mType = searchType(scope);
		return mType;
	}
		
	private IType searchType(final Scope scope) {
		ASTNode callOn = getFirstChild();
		if (callOn == null) {
			throw new UnsupportedOperationException("Not Yet Implemented");
		}else {
			IType callOnType = callOn.getType(scope);
			if (callOnType != null) {
				ITypeResolver tr = callOnType.getTypeResolver(getName(), null);
				if (tr != null)
					return tr.getType(scope);
			}
		}
		return null;
//		throw new NullPointerException("Could not find the type of: " + debugPrint() );
	}
	

	@Override
	public List<Class<? extends ASTNode>> getAllowedChildren() {
		return Arrays.asList(
				ASTPropAccess.class, 
				ASTName.class);
	}
	
	
	
	@Override
	public void getCompletionsAndFilter(String statement, boolean hasDotAtEnd, Scope scope, Set<ICompletion> completions, Set<ICompletionFilter> filter) {
		if (hasDotAtEnd) {
			IType t = getType(scope);
			if (t != null) {
				t.insertCompletions(completions);
				return ;
			}
		}else {
			final ASTNode sourceNode = getSourceNode();
			if (sourceNode != null) {
				IType sourceType = sourceNode.getType(scope); //scope.getTypeOf(sourceNode);
				if (sourceType != null) {
					sourceType.insertCompletions(completions);
					final String thisName = getName();
					if (thisName != null && thisName.isEmpty() == false) {
						CompletionUtils.addNameFilter(thisName, filter);
					}
					return ;
				}
			}
		}
		super.getCompletionsAndFilter(statement, hasDotAtEnd, scope, completions, filter);
	}

}
