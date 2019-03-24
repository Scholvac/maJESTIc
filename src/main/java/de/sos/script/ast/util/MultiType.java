package de.sos.script.ast.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import de.sos.script.ast.ASTFuncDecl;
import de.sos.script.ast.IType;
import de.sos.script.ast.ITypeResolver;
import de.sos.script.support.completions.ICompletion;

public class MultiType implements IType {

	private ArrayList<IType> 	mTypes = new ArrayList<>();
	
	public MultiType(Collection<IType> canidates) {
		mTypes.addAll(canidates);
	}
	
	@Override
	public String getName() {
		String str = ""; for (IType t : mTypes) str += t.getName()+"||";
		if (str.length() > 3) str = str.substring(0, str.length()-2);
		return str;
	}

	@Override
	public void insertCompletions(Set<ICompletion> completions) {
		for (IType t : mTypes) t.insertCompletions(completions);
	}

	@Override
	public ITypeResolver getTypeResolver(String name, IType... parameterTypes) {
		for (IType t : mTypes) {
			ITypeResolver tr = getTypeResolver(name, parameterTypes);
			if (tr != null)
				return tr;
		}
		return null;
	}

	@Override
	public String getFullQualifiedName() {
		String str = ""; for (IType t : mTypes) str += t.getFullQualifiedName()+"||";
		if (str.length() > 3) str = str.substring(0, str.length()-2);
		return str;
	}

	
	@Override
	public boolean inherits(IType pt) {
		for (IType t : mTypes) 
			if (t.inherits(pt))
				return true;
		return false;
	}

	private List<ASTFuncDecl> mConstructors = null;
	private List<ASTFuncDecl> mMethods = null;
	
	@Override
	public List<ASTFuncDecl> getConstructors() {
		if (mConstructors == null) {
			Set<ASTFuncDecl> set = new TreeSet<>();
			for (IType t : mTypes) {
				List<ASTFuncDecl> tc = t.getConstructors();
				if (tc != null)
					set.addAll(tc);
			}
			ArrayList<ASTFuncDecl> list = new ArrayList<>(set);
			Collections.sort(list);
			mConstructors = list;
		}
		return mConstructors;
	}

	@Override
	public List<ASTFuncDecl> getAllFunctionDeclarations() {
		if (mMethods == null) {
			Set<ASTFuncDecl> set = new TreeSet<>();
			for (IType t : mTypes) {
				List<ASTFuncDecl> tc = t.getAllFunctionDeclarations();
				if (tc != null)
					set.addAll(tc);
			}
			ArrayList<ASTFuncDecl> list = new ArrayList<>(set);
			Collections.sort(list);
			mMethods = list;
		}
		return mMethods;
	}

}
