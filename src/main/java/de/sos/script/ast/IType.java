package de.sos.script.ast;

import java.util.List;

import de.sos.script.ast.util.Scope;

public interface IType extends ITypeResolver, INamedElement {

	
	List<ASTFuncDecl> 	getConstructors();
	List<ASTFuncDecl> 	getAllFunctionDeclarations();
	
	ITypeResolver getTypeResolver(String name, IType...parameterTypes);
	String getFullQualifiedName();

	@Override
	default IType getType(final Scope scope) {
		return this;
	}
	
	boolean inherits(IType pt);
	
	
}
