package de.sos.script.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.sos.script.IEntryPoint;
import de.sos.script.ParserProblem;
import de.sos.script.ParserProblem.Level;
import de.sos.script.ast.lang.NativeProperty;
import de.sos.script.ast.util.Scope;

public class CompilationUnit extends ASTNode implements IScopeProvider {

	private final String 				mIdentifier;
	private IScopeProvider				mSystemScope = null;
	private ArrayList<ParserProblem> 	mProblems;
	private Scope 						mScope;

	public CompilationUnit(String identifier, int start, int end) {
		super(null, start, end);
		mIdentifier = identifier;
	}

	public void setSystemScope(IScopeProvider scope) {
		mSystemScope = scope;
	}
	
	@Override
	public String toString() {
		return printInterval("CompilationUnit - " + mIdentifier);
	}

	@Override
	public Scope getScope() {
		if (mScope == null)
			mScope = new Scope(this) {
				@Override
				public Scope getParentScope() {
					return mSystemScope.getScope();
				}
			};
		return mScope;
	}
	
//	@Override
//	public IScope getParentScope() {
//		return mSystemScope;
//	}

	
	
	///////////////////////////	Parser Problems //////////////////////////////////////////////////
	
	public void addWarning(final String message, final int line) {
		addMarker(new ParserProblem(Level.WARN, message, line));
	}
	public void addError(final String message, final int line) {
		addMarker(new ParserProblem(Level.ERROR, message, line));
	}

	public void addMarker(final ParserProblem problem) {
		if (mProblems == null)
			mProblems = new ArrayList<>();
		mProblems.add(problem);
	}
	
	public List<ParserProblem> getProblems() {
		if (mProblems != null)
			return Collections.unmodifiableList(mProblems);
		return new ArrayList<>();
	}
	public List<ParserProblem> getProblems(Level level){
		ArrayList<ParserProblem> out = new ArrayList<>();
		for (int i = 0; i < mProblems.size(); i++) {
			final ParserProblem pp = mProblems.get(i);
			if (pp.getLevel() == level)
				out.add(pp);
		}
		return out;
	}
	
	public boolean hasProblem(Level l) {
		for (int i = 0; i < mProblems.size(); i++)
			if (mProblems.get(i).getLevel() == l)
				return true;
		return false;
	}
	
	public boolean hasErrors() { return hasProblem(Level.ERROR); }
	public boolean hasWarnings() { return hasProblem(Level.WARN); }

	@Override
	public List<Class<? extends ASTNode>> getAllowedChildren() {
		return Arrays.asList(
				ASTName.class, 
				ASTPropAccess.class, 
				ASTFuncCall.class,
				ASTFuncDecl.class,
				ASTTypeDef.class,
				ASTAssign.class,
				ASTBlock.class);
	}

	/** inserts information of an entry point into the compilation unit. 
	 * That is: looks for a ASTFuncDecl with the same signature as the entry - point. 
	 * If such a function is found, the parameters will be equipped with type-hints from the entry - point. 
	 * If such a function is not found, the method will append an warning.
	 * 
	 * In addition it will add the global variables of the entry point as NativeProperty
	 * @param ep
	 */
	public void insertEntryPoint(IEntryPoint ep) {
		if (ep == null)
			return ;
		if (ep.getVariableCount() > 0) {
			String[] varNames = ep.getVariableNames();
			Object[] varValues = ep.getVariableValues();
			assert(varNames.length == varValues.length);
			for (int i = 0; i < varNames.length; i++) {
				String vn = varNames[i];
				Object vv = varValues[i];
				if (vv != null) {
					NativeProperty np = new NativeProperty(vn, TypeManager.get().getType(vv.getClass()));
					_addChild(np);
				}
			}
		}
		
		//search for Functions with the same name as the entry point
		String 		epFn = ep.getFunctionName();
		if (epFn == null || epFn.isEmpty())
			return ;
		int 		numArgs = ep.getArgumentCount();
		if (numArgs != 0) {
			
			String[] 	argNames = ep.getArgumentNames();
			Class<?>[] 	argTypes = ep.getArgumentTypes();
			
			for (ASTNode child : getChildren()) {
				if (child instanceof ASTFuncDecl && ((ASTFuncDecl)child).getName().equals(epFn)) {
					ASTFuncDecl decl = (ASTFuncDecl)child;
					//check the names - they have to occure in the same order
					if (decl.getParameterCount() != numArgs)
						continue; //they can not match
					boolean match = true;
					List<ASTParamDecl> paramDecls = decl.getParameters();
					for (int i = 0; i < numArgs; i++) {
						final String paramName = paramDecls.get(i).getName();
						if (paramName.equals(argNames[i]) == false) {
							match = false;
							break;
						}
					}
					if (!match)
						continue;
					//they do match, thus we can equipp the parameters with type hints
					for (int i = 0; i < numArgs; i++) {
						final ASTParamDecl param = paramDecls.get(i);
						Class<?> argType = argTypes[i];
						if (argType != null) {
							param.setType(TypeManager.get().getType(argType));
						}
					}
					//we do not need to search for additional functions - they would either not match or result in an error (duplicated signature)
					return ;
				}
			}
		}else {
			//arg-count == 0 -> search for a function with given name and no arguments
			for (ASTNode child : getChildren()) {
				if (child instanceof ASTFuncDecl) {
					ASTFuncDecl fd = (ASTFuncDecl)child;
					if (fd.getName().equals(epFn) && fd.getParameterCount() == 0)
						return ; //no additional arguments, no warning
				}
			}
		}
		
		
		//if we reach this point, there is a valid entry point declared, but no such function was found -> add an warning
		addWarning("Could not find required entry point " + epFn, 0);
	}
	
	


}
