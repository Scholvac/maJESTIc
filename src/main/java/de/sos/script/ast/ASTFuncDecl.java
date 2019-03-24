package de.sos.script.ast;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import de.sos.script.ast.ASTNode.ASTNamedNode;
import de.sos.script.ast.util.DelegateType;
import de.sos.script.ast.util.MultiType;
import de.sos.script.ast.util.Scope;
import de.sos.script.support.completions.FunctionDeclaration;
import de.sos.script.support.completions.ICompletion;

public class ASTFuncDecl extends ASTNamedNode implements IScopeProvider, ITypeResolver, INamedElement, Comparable<ASTFuncDecl> {


	private List<ASTParamDecl> 	mParameterDeclarations;
	private Scope 				mScope;
	private IType				mReturnType;
	
	public ASTFuncDecl(ASTNode parent, String name, int start, int end) {
		super(parent, name, start, end);
	}

	@Override
	public String toString() {
		return printInterval("FunctionDeclaration");
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
	
	public void setReturnType(IType t) {
		mReturnType = t;
	}
	
	@Override
	public IType getType(final Scope scope) {
		if (mReturnType == null) {
			mReturnType = new DelegateType(); //avoid recursive calls
			IType realType = _searchReturnType(scope);
			if (realType == null)
				((DelegateType)mReturnType).setType(TypeManager.get().getType("Void"));
			else
				((DelegateType)mReturnType).setType(realType);
		}
		return mReturnType;
	}

	private IType _searchReturnType(final Scope scope) {
		if (getChildren() == null)
			return null;
		
		//we do not delegate to the block but do search in all children for an ASTReturnNode
		Stack<ASTNode> open = new Stack<>();
		open.addAll(getChildren());
		Set<IType> possibleTypes = new HashSet<>();
		while(open.isEmpty() == false) {
			ASTNode child = open.pop();
			if (child instanceof ASTReturn) {
				IType canidate = child.getType(scope);
				if (canidate != null)
					possibleTypes.add(canidate);
			}else {
				if (child.getChildren() != null)
					open.addAll(child.getChildren());
			}
		}
		//remove recursive added types
		possibleTypes.remove(mReturnType);//that is always a delegate
		
		if (possibleTypes.isEmpty())
			return null;
		if (possibleTypes.size() == 1)
			return possibleTypes.iterator().next();
		return new MultiType(possibleTypes);
	}

	@Override
	public void insertCompletions(Set<ICompletion> completions) {
		completions.add(new FunctionDeclaration(this));		
	}

//	@Override
//	public IType _saveGetType(final Set<ASTNode> set) {
//		if (set.contains(this))
//			return null;
//		return null;
//	}

	public String getSignature() {
		String sig = getName() + "(";
		if (mParameterDeclarations != null && mParameterDeclarations.isEmpty() == false) {
			for (ASTParamDecl pd : mParameterDeclarations) {
				sig += pd.asSignaturePart() + ", ";
			}
			sig = sig.substring(0, sig.length()-2); //remove last ', '
		}		
		return sig + ")";
	}

	public void setParameters(List<ASTParamDecl> paramDecls) {
		mParameterDeclarations = paramDecls;		
	}
	public List<ASTParamDecl> getParameters(){ return mParameterDeclarations; }

	@Override
	public List<Class<? extends ASTNode>> getAllowedChildren() {
		return Arrays.asList(
				ASTParamDecl.class);
	}

	public int getParameterCount() {
		if (mParameterDeclarations == null || mParameterDeclarations.isEmpty())
			return 0;
		return mParameterDeclarations.size();
	}

	@Override
	public int compareTo(ASTFuncDecl o) {
		return getSignature().compareToIgnoreCase(o.getSignature());
	}

}
