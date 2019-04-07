package de.sos.script.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import de.sos.script.ast.lang.java.JarManager;
import de.sos.script.ast.lang.java.JavaType;
import de.sos.script.impl.QualifiedName;

public class TypeManager {
	
	private static TypeManager theInstance;
	public static TypeManager get() {
		if (theInstance == null)
			theInstance = new TypeManager();
		return theInstance;
	}
	
	
	private static HashSet<Class> 		sPrimitiveTypes = new HashSet<>();
	
	static {
		sPrimitiveTypes.add(boolean.class);
		sPrimitiveTypes.add(byte.class);
		sPrimitiveTypes.add(char.class);
		sPrimitiveTypes.add(short.class);
		sPrimitiveTypes.add(int.class);
		sPrimitiveTypes.add(long.class);
		sPrimitiveTypes.add(float.class);
		sPrimitiveTypes.add(double.class);
	}
	
	
	private HashMap<String, IType> 		mTypes = new HashMap<>();
	private HashMap<String, IType> 		mTypesSimpleNames = new HashMap<>();
	
	
	private TypeManager() {	
	}
	
	

	public JarManager getJarManager() {
		return JarManager.get();
	}
	
	public void registerType(final IType t) {
		String fqn = t.getFullQualifiedName();
		mTypes.put(fqn, t);
		int idx = fqn.lastIndexOf('.');
		if (idx > 0) {
			String sn = fqn.substring(idx+1, fqn.length());
			mTypesSimpleNames.put(sn, t);
		}
	}
	
	public IType getType(final QualifiedName fqn) {
		return getType(fqn, new ArrayList<>());
	}
	public IType getType(final QualifiedName fqn, ArrayList<String> typeArguments){
		if (fqn.numSegments() == 1) {
			IType t = BuildInTypes.getType(fqn.firstSegment());
			if (t != null)
				return t;
		}
		String fqn_str = fqn.toString(".");
		if (typeArguments != null && typeArguments.isEmpty() == false) {
			for (String ta : typeArguments)
				fqn_str += ta;
		}
		IType t = mTypes.get(fqn_str);
		if (t != null)
			return t;
		//also try the simple types
		t = mTypesSimpleNames.get(fqn_str);
		if (t != null)
			return t;
		t = getJarManager().getType(fqn, typeArguments);
		if (t != null)
			return t;
		if (fqn.numSegments() == 1) {
			IType jt = getJarManager().searchSimpleName(fqn.firstSegment());
			if (jt != null && jt instanceof JavaType) {
				((JavaType)jt).setArguments(typeArguments);
				mTypesSimpleNames.put(fqn_str, jt);
			}
			return jt;
//			return getJarManager().searchSimpleName(fqn_str);
		}
		return null;
	}

	public IType getType(String fqn) {
		return getType(QualifiedName.createWithRegEx(fqn, "\\."), new ArrayList<>());
	}

	public IType getType(Class<?> clazz) {
		if (clazz == null) 
			return null;
		return getType(QualifiedName.createWithRegEx(clazz.getName(), "\\."), new ArrayList<>());
	}

//	public ITypeResolver getType(Class<?> c) {
//		if (c == double.class || c == Double.class || c == float.class || c == Float.class
//				|| c == int.class || c == Integer.class || c == short.class || c == Short.class 
//				|| c == byte.class || c == Byte.class || c == long.class || c == Long.class)
//			return BuildInTypes.NumberType;
//		if (c == String.class)
//			return BuildInTypes.StringType;
//		if (c == Boolean.class || c == boolean.class)
//			return BuildInTypes.BoolType;
//		
//		String fqn = c.getName();
//		IType t = getType(fqn);
//		if (t != null && t instanceof ITypeResolver)
//			return (ITypeResolver)t;
//		return null;
//	}

	public static boolean isJavaPrimitive(Class<?> clazz) {
		return sPrimitiveTypes.contains(clazz);
	}
    public static boolean inherits(Class<?> clazz, Class<?> parent) {
    	if (clazz == null || parent == null)
    		return false;
        if (clazz == parent)
            return true;
        if (clazz.getSuperclass() == parent)
            return true;

        for (Class<?> in : clazz.getInterfaces())
            if (inherits(in, parent))
                return true;
        return false;
    }



	

}
