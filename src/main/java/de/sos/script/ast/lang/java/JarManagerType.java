package de.sos.script.ast.lang.java;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import de.sos.script.ast.ASTFuncDecl;
import de.sos.script.ast.INamedElement;
import de.sos.script.ast.IType;
import de.sos.script.ast.ITypeResolver;
import de.sos.script.ast.util.Scope;
import de.sos.script.support.completions.ICompletion;
import de.sos.script.support.completions.PackageCompletion;


public class JarManagerType implements INamedElement, ITypeResolver, IType {

	private JarManager mJarManager;
	
	public JarManagerType(final JarManager jmgr) {
		mJarManager = jmgr;
	}

	@Override
	public String getName() {
		return "Packages";
	}
	@Override
	public ITypeResolver getTypeResolver(String name, IType... parameterTypes) {
		Package p = mJarManager.getPackage(name);
		if (p != null)
			return p;
		return null;
	}
	@Override
	public String getFullQualifiedName() {
		return getName();
	}
	
	
	@Override
	public void insertCompletions(Set<ICompletion> completions) {
		Collection<Package> packages = mJarManager.getAllPackages();
		for (Package p : packages) {
			completions.add(new PackageCompletion(p));
		}
	}
	@Override
	public List<ASTFuncDecl> getConstructors() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ASTFuncDecl> getAllFunctionDeclarations() {
		// TODO Auto-generated method stub
		return null;
	}	
	@Override
	public boolean inherits(IType pt) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public IType getType(final Scope scope) {
		// TODO Auto-generated method stub
		return null;
	}
}
