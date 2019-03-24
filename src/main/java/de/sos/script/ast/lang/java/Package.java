package de.sos.script.ast.lang.java;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import de.sos.script.ast.ASTFuncDecl;
import de.sos.script.ast.IType;
import de.sos.script.ast.ITypeResolver;
import de.sos.script.impl.QualifiedName;
import de.sos.script.support.completions.BaseCompletion;
import de.sos.script.support.completions.ICompletion;
import de.sos.script.support.completions.JavaTypeCompletion;
import de.sos.script.support.completions.PackageCompletion;



public class Package implements IType {

	
	
	private Map<String, Package> 			mSubPackages = new HashMap<String, Package>();
	private Map<String, URL>				mProxyTypes = new HashMap<>();
	private Map<String, JavaType>			mJavaTypes = new HashMap<>();
	
	private String mName;
	private Package mParent;

//	private Map<String, IType> 			mTypes = new HashMap<>();
	
	public Package(final String n, final Package parent) {
		mName = n;
		mParent = parent;
	}
	
	@Override
	public String getName() {
		return mName;
	}
	
	public Package getOrCreateSubPackage(String n) {
		Package p = mSubPackages.get(n);
		if (p == null) {
			mSubPackages.put(n, p = new Package(n, this));
		}
		return p;
	}

	public void addProxyType(String name, URL sourceURL) {
		mProxyTypes.put(name, sourceURL);
	}
	
	public JavaType getType(String name) {
		if (mJavaTypes.containsKey(name))
			return mJavaTypes.get(name);
		if (mProxyTypes.containsKey(name)) {
			return createAndStoreCompilationUnit(name, mProxyTypes.remove(name));
		}
		return null;
	}

	private JavaType createAndStoreCompilationUnit(String name, URL sourceURL) {
		CompilationUnit cu = null;
		if (sourceURL == null)
			cu = createFromClass(getFullQualifiedName() + "." + name);
		else
			try {
				cu = StaticJavaParser.parse(sourceURL.openStream());
			}catch(Exception e) {
				e.printStackTrace();
			}
		
		if (cu != null) {
			JavaType jt = new JavaType(name, cu);
			mJavaTypes.put(name, jt); //add even if cu == null as it has been removed from proxy list
			return jt;
		}
		mJavaTypes.put(name, null);
		return null;
	}

	private CompilationUnit createFromClass(String className) {
		CompilationUnit cu = new CompilationUnit();
		String pn = getFullQualifiedName();
		cu.setPackageDeclaration(pn);
		
		try {
			Class<?> cl = Class.forName(className);
			if (cl != null && cl.isEnum() == false) {
				String cn = QualifiedName.createWithRegEx(className, "\\.").lastSegment();
				ClassOrInterfaceDeclaration classDecl = null;
				if (cl.isInterface())
					classDecl = cu.addInterface(cn, getModifiers(cl.getModifiers()));
				else
					classDecl = cu.addClass(cn, getModifiers(cl.getModifiers()));
				
				if (cl.getSuperclass() != null)
					classDecl.addExtendedType(cl.getSuperclass());
				if (cl.getInterfaces() != null && cl.getInterfaces().length != 0) {
					for (int i = 0; i < cl.getInterfaces().length; i++)
						classDecl.addImplementedType(cl.getInterfaces()[i]);
				}
				
				
				for (Field f: cl.getDeclaredFields()) {
					Keyword[] modifiers = getModifiers(f.getModifiers());
					classDecl.addField(f.getType(), f.getName(), modifiers);
				}
				for (Method m : cl.getDeclaredMethods()) {
					Keyword[] modifiers = getModifiers(m.getModifiers());
					MethodDeclaration mdecl = classDecl.addMethod(m.getName(), modifiers);
					mdecl.setType(m.getReturnType());
					Class<?>[] paramTypes = m.getParameterTypes();
					if (!(paramTypes == null || paramTypes.length == 0)) {
						for (int i = 0; i < paramTypes.length; i++) {
							mdecl.addParameter(paramTypes[i], "arg_" + i);
						}
					}
				}
				for (Constructor c : cl.getConstructors()) {
					Keyword[] modifiers = getModifiers(c.getModifiers());
					ConstructorDeclaration cd = classDecl.addConstructor(modifiers);
					Class<?>[] paramTypes = c.getParameterTypes();
					if (!(paramTypes == null || paramTypes.length == 0)) {
						for (int i = 0; i < paramTypes.length; i++) {
							cd.addParameter(paramTypes[i], "arg_" + i);
						}
					}
				}
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cu;
	}

	private Keyword[] getModifiers(int mod) {
		ArrayList<Keyword> l = new ArrayList<>();
		if (Modifier.isPublic(mod))
			l.add(Keyword.PUBLIC);
		else if (Modifier.isPrivate(mod))
			l.add(Keyword.PRIVATE);
		else if (Modifier.isProtected(mod))
			l.add(Keyword.PROTECTED);
		else 
			l.add(Keyword.DEFAULT);
		
		if (Modifier.isStatic(mod))
			l.add(Keyword.STATIC);
		if (Modifier.isAbstract(mod))
			l.add(Keyword.ABSTRACT);
		if (Modifier.isNative(mod))
			l.add(Keyword.NATIVE);
		if (Modifier.isFinal(mod))
			l.add(Keyword.FINAL);
		if (Modifier.isSynchronized(mod))
			l.add(Keyword.SYNCHRONIZED);
		//TODO: do we need the others as well?
		if (l.isEmpty() == false)
			return l.toArray(new Keyword[l.size()]);
		return null;
	}

	@Override
	public String getFullQualifiedName() {
		if (mParent != null)
			return mParent.getFullQualifiedName() + "." + mName;
		return mName;
	}


	@Override
	public ITypeResolver getTypeResolver(String name, IType... parameterTypes) {
		if (name != null) {
			if (mSubPackages.containsKey(name))
				return mSubPackages.get(name);
		}
		return getType(name);
	}

	@Override
	public void insertCompletions(Set<ICompletion> completions) {
		final String parent_name = getFullQualifiedName();
		for (Package p : mSubPackages.values()) {
			completions.add(new PackageCompletion(p));
		}
		for (JavaType jt : mJavaTypes.values()) {
			completions.add(new JavaTypeCompletion(jt));
		}
		for (String n : mProxyTypes.keySet()) {
			URL url = mProxyTypes.get(n);
			completions.add(new BaseCompletion(n, "Java-Class (Proxy)", getFullQualifiedName(), "not yet loaded java class \n" + url));
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

	public IType searchSimpleType(String className) {
		IType t = getType(className);
		if (t != null)
			return t;
		for (Package sp : mSubPackages.values()) {
			t = sp.searchSimpleType(className);
			if (t != null)
				return t;
		}
		return null;
	}

	@Override
	public boolean inherits(IType pt) {
		// TODO Auto-generated method stub
		return false;
	}

	
}
