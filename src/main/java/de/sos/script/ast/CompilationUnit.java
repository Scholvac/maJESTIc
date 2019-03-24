package de.sos.script.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.sos.script.ParserProblem;
import de.sos.script.ParserProblem.Level;
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
		return Collections.unmodifiableList(mProblems);
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
	
	


}
