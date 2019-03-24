package de.sos.script.ast;

import java.util.ArrayList;
import java.util.List;

import de.sos.script.ast.util.Scope;

public class ASTLiteral extends ASTNode implements ITypeResolver {

	private String mValue;
	private String mType;


	public ASTLiteral(ASTNode parent, String type, String value, int start, int end) {
		super(parent, start, end);
		mValue = value;
		mType = type;
	}
	
	
	@Override
	public String toString() {
		return printInterval(mType + " Literal = " + mValue);
	}

	
	@Override
	public IType getType(final Scope scope) {
		return TypeManager.get().getType(mType);
	}


	@Override
	public List<Class<? extends ASTNode>> getAllowedChildren() {
		return new ArrayList<>();
	}
}
