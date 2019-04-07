package de.sos.script.ast.lang;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import de.sos.script.IScriptVariable;
import de.sos.script.ScriptVariable;
import de.sos.script.ast.ASTFuncDecl;
import de.sos.script.ast.ASTParamDecl;
import de.sos.script.ast.IType;
import de.sos.script.ast.TypeManager;

public class NativeFunctionDeclaration extends ASTFuncDecl {


	private Class<?> 					mReturnClazz;
	private Function<Object[], Object> 	mCallable;

	public NativeFunctionDeclaration(final String name, final Class<?> returnType, List<ScriptVariable> parameters, Function<Object[], Object> callable, String documentation) {
		super(null, name, -1, -1);
		mCallable = callable;
		setDocumentation(documentation);
		if (parameters != null && parameters.isEmpty() == false)
			convertAndAddParameters(parameters);
		mReturnClazz = returnType != null ? returnType : Void.class;
		IType rt = TypeManager.get().getType(mReturnClazz);
		if (rt != null)
			setReturnType(rt);
		else
			System.out.println();
	}

	private void convertAndAddParameters(List<ScriptVariable> parameters) {
		ArrayList<ASTParamDecl> astParams = new ArrayList<>();		
		for (IScriptVariable sv : parameters) {
			String typeHint = sv.getExpectedType() != null ? sv.getExpectedType().getName() : "java.lang.Object";
			astParams.add(new ASTParamDecl(this, sv.getName(), typeHint, -1, -1));
		}
		this.setParameters(astParams);
	}


	public Function<Object[], Object> getCallable() {
		return mCallable;
	}


//	@Override
//	public IType getType() {
//		if (mReturnType != null)
//			return TypeManager.get().getType(mReturnType);
//		return null;
//	}

	
}
