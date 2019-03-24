package de.sos.script.ast.util;

import java.util.List;
import java.util.Set;

import de.sos.script.ast.ASTFuncDecl;
import de.sos.script.ast.BuildInTypes;
import de.sos.script.ast.IType;
import de.sos.script.ast.ITypeResolver;
import de.sos.script.support.completions.ICompletion;

public class DelegateType implements IType {

	private IType 	mType = BuildInTypes.getType("Any");
	
	public void setType(IType t) {
		if (t == null)
			System.out.println();
		mType = t; 
	}
	public IType getDelegate() { return mType; }
	
	@Override
	public String getName() {
		return mType.getName();
	}

	@Override
	public void insertCompletions(Set<ICompletion> completions) {
		mType.insertCompletions(completions);
	}

	@Override
	public ITypeResolver getTypeResolver(String name, IType... parameterTypes) {
		return mType.getTypeResolver(name, parameterTypes);
	}

	@Override
	public String getFullQualifiedName() {
		return mType.getFullQualifiedName();
	}


	@Override
	public boolean inherits(IType pt) {
		return mType.inherits(pt);
	}
	@Override
	public List<ASTFuncDecl> getConstructors() {
		return mType.getConstructors();
	}
	@Override
	public List<ASTFuncDecl> getAllFunctionDeclarations() {
		return mType.getAllFunctionDeclarations();
	}
}
