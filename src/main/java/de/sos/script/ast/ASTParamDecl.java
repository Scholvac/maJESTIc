package de.sos.script.ast;

import java.util.List;
import java.util.Set;

import de.sos.script.ast.ASTNode.ASTNamedNode;
import de.sos.script.ast.util.Scope;
import de.sos.script.support.completions.BaseCompletion;
import de.sos.script.support.completions.ICompletion;

public class ASTParamDecl extends ASTNamedNode implements ITypeResolver, INamedElement {

	private String 	mTypeHint;
	private IType 	mType = null;

	public ASTParamDecl(ASTNode parent, String name, String typeHint, int start, int end) {
		super(parent, name, start, end);
		mTypeHint = typeHint;
	}
	

	public ASTParamDecl(ASTNode parent, String name, IType type, int start, int end) {
		super(parent, name, start, end);
		setType(type);
	}


	public String asSignaturePart() {
		if (mTypeHint != null && mTypeHint.isEmpty() == false)
			return mTypeHint + " " + getName();
		return getName();
	}


//	@Override
//	public IType getType() {
//		if (mTypeHint != null)
//			return TypeManager.get().getType(mTypeHint);
//		return null;
//	}


	@Override
	public List<Class<? extends ASTNode>> getAllowedChildren() {
		throw new UnsupportedOperationException("Not Yet Implemented");
	}
	
	public void setType(final IType type) {
		mType = type;
	}
	@Override
	public IType getType(final Scope scope) {
		if (mType == null) {
			if (mTypeHint != null && mTypeHint.isEmpty() == false)
				mType = TypeManager.get().getType(mTypeHint);
		}
		return mType;
	}


	@Override
	public void insertCompletions(Set<ICompletion> completions) {
		IType type = getType(getNodeScope());
		if (type != null) {
			type.insertCompletions(completions);
		}
		String resolvedType = type != null ? type.getName() : "Any";
		if (getParent() instanceof ASTFuncDecl) {
			completions.add(new BaseCompletion(getName(), resolvedType, ((ASTFuncDecl)getParent()).getName(), "function argument"));
		}
	}
	
}
