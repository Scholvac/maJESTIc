package de.sos.script.ast;

import java.util.List;

import de.sos.script.ast.ASTNode.ASTNamedNode;
import de.sos.script.ast.util.Scope;

public class ASTParamDecl extends ASTNamedNode implements ITypeResolver {

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
	
}
