package de.sos.script.ast;

import java.util.List;
import java.util.Set;

import de.sos.script.ast.ASTNode.ASTNamedNode;
import de.sos.script.ast.util.Scope;
import de.sos.script.support.completions.ICompletion;

public class ASTTypeDef extends ASTNamedNode implements ITypeResolver, INamedElement {

	public ASTTypeDef(ASTNode parent, String name, int start, int end) {
		super(parent, name, start, end);
		// TODO Auto-generated constructor stub
	}

//	@Override
//	public IType getType() {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public IType getType(final Scope scope) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void insertCompletions(Set<ICompletion> completions) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Class<? extends ASTNode>> getAllowedChildren() {
		throw new UnsupportedOperationException("Not Yet Implemented");
	}

}
