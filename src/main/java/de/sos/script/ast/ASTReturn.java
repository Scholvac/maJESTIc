package de.sos.script.ast;

import java.util.Arrays;
import java.util.List;

import de.sos.script.ast.util.Scope;

public class ASTReturn extends ASTNode implements ITypeResolver{

	public ASTReturn(ASTNode parent, int start, int end) {
		super(parent, start, end);
	}
	
	
	@Override
	public String toString() {
		return printInterval("Return");
	}


//	@Override
//	public IType getType() {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public IType getType(final Scope scope) {
		ASTNode firstChild = getFirstChild();
		if (firstChild != null)
			return firstChild.getType(scope);
		return null;
	}

	@Override
	public List<Class<? extends ASTNode>> getAllowedChildren() {
		return Arrays.asList(
				ASTName.class, 
				ASTFuncCall.class,
				ASTLiteral.class);
	}

}
