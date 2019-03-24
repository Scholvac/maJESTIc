package de.sos.script.ast;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import de.sos.script.support.completions.ICompletion;

public abstract class BuildInTypes implements IType {

	
	@Override
	public void insertCompletions(Set<ICompletion> completions) {
	}

	@Override
	public ITypeResolver getTypeResolver(String name, IType... parameterTypes) {
		return null;
	}

	@Override
	public String getFullQualifiedName() {
		return getName();
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
		return false;
	}

	private static HashMap<String, IType> 	sPrimitiveTypeMap = new HashMap<>();
	static {
		sPrimitiveTypeMap.put("Number", new NumberType());
		sPrimitiveTypeMap.put("Bool", new BoolType());
		sPrimitiveTypeMap.put("String", new StringType());
		sPrimitiveTypeMap.put("Any", new AnyType());
		sPrimitiveTypeMap.put("void", new VoidType());
		sPrimitiveTypeMap.put("boolean", getType("Bool"));
		sPrimitiveTypeMap.put("int", getType("Number"));
		sPrimitiveTypeMap.put("double", getType("Number"));
		sPrimitiveTypeMap.put("float", getType("Number"));
		sPrimitiveTypeMap.put("byte", getType("Number"));
		sPrimitiveTypeMap.put("char", getType("Number"));
		sPrimitiveTypeMap.put("short", getType("Number"));
		sPrimitiveTypeMap.put("long", getType("Number"));
		
	}
	public static IType getType(final String name) {
		return sPrimitiveTypeMap.get(name);
	}
	
	public static class VoidType extends BuildInTypes {
		@Override
		public String getName() {
			return "Void";
		}
	}
	public static class AnyType extends BuildInTypes {
		@Override
		public String getName() {
			return "Any";
		}
	}
	public static class NumberType extends BuildInTypes {
		@Override
		public String getName() {
			return "Number";
		}
	}

	public static class StringType extends BuildInTypes {
		@Override
		public String getName() {
			return "String";
		}		
	}
	
	public static class BoolType extends BuildInTypes {
		@Override
		public String getName() {
			return "Bool";
		}		
	}


	
}
