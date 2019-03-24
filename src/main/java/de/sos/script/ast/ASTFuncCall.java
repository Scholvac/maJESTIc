package de.sos.script.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import de.sos.script.ast.util.MultiType;
import de.sos.script.ast.util.Scope;
import de.sos.script.support.CompletionUtils;
import de.sos.script.support.ICompletionFilter;
import de.sos.script.support.completions.ICompletion;

public class ASTFuncCall extends ASTPropAccess implements ITypeResolver{

	private ASTNode		 		mCallOn;
	private List<ASTNode> 		mArguments;
	
	private List<ASTFuncDecl>	mDeclarations = null;

	public ASTFuncCall(ASTNode parent, String name, int start, int end) {
		super(parent, name, start, end);
		// TODO Auto-generated constructor stub
	}

	public void setCallOn(ASTNode callOn) {
		mCallOn = callOn;
	}

	public void setArguments(List<ASTNode> arguments) {
		mArguments = arguments;		
	}
	public List<ASTNode> getArguments() {
		return mArguments;
	}

	public int getArgumentSize() {
		if (mArguments == null)
			return 0;
		return mArguments.size();
	}


	public String getSignature() {
		String sig = getName() + "(";
		if (mArguments != null && mArguments.isEmpty()) {
			for (int i = 0; i < mArguments.size(); i++)
				sig += "arg" + i + ", ";
			sig = sig.substring(0, sig.length()-2);//remove the last ', '
		}
		
		sig += ")";
		return sig;
	}
	
	@Override
	public String toString() {
		return printInterval("FunctionCall (" + getSignature() + " )");
	}
	
	@Override
	public ASTNode getSourceNode() {
		return mCallOn;
	}

	private IType mType = null;
	@Override
	public IType getType(final Scope scope) {
		if (mType == null) {
			List<ASTFuncDecl> decls = _searchDeclaration(scope);
			if (decls == null || decls.isEmpty())
				return null;
			if (decls.size() == 1) {
				mType = decls.get(0).getType(scope);
			}else {
				HashSet<IType> types = new HashSet<>();
				for (ASTFuncDecl d : decls) {
					IType t = d.getType(scope);
					if (t != null)
						types.add(t);
				}
				if (types.size() == 1) {
					mType = types.iterator().next();
				}else
					mType = new MultiType(types);
			}
//			if (decl != null) {
//				mType = decl.getType();
//			}
		}
		return mType;
	}
	
//	@Override
//	public IType getType() {
//		return getType(getScope());
//	}
//	public IType getType(final Scope scope) {	
//		if (mCallOn == null) {
//			//it has to be a function in the current scope
//			INamedElement toCall = scope.getNamedElement(getName());
//			if (toCall instanceof IType)
//				return (IType)toCall;
//			if (toCall instanceof ITypeResolver)
//				return ((ITypeResolver) toCall).getType();
//		}else {
//			IType srcType = scope.getTypeOf(mCallOn);
//			ArrayList<IType> paramTypes = new ArrayList<>();
//			if (mArguments != null) {
//				for (ASTNode pn : mArguments) {
//					paramTypes.add(scope.getTypeOf(pn));
//				}
//			}
//			if (srcType == null)
//				return null;
//			ITypeResolver tr = srcType.getTypeResolver(getName(), paramTypes.toArray(new IType[paramTypes.size()]));
//			if (tr != null)
//				return tr.getType();
//		}
//		
//		return null;
//	}

	

	private List<ASTFuncDecl> _searchDeclaration(final Scope scope) {
		//find the declarations of the called method
		Set<ASTFuncDecl> decls = new TreeSet<>();
		String methodName = getName();
		if (mCallOn != null) {
			IType sourceType = mCallOn.getType(scope); //scope.getTypeOf(mCallOn);
			if (sourceType != null) {
				decls.addAll(sourceType.getAllFunctionDeclarations());
			}
		}else {
			INamedElement ne = scope.getNamedElement(getName());
			if (ne != this && ne instanceof ASTFuncDecl) {
				return Arrays.asList((ASTFuncDecl)ne);
			}else if (ne instanceof ASTNode) {
				//this could be an constructor
				ASTNode node = (ASTNode)ne;
				IType t = node.getType(scope);
				if (t != null) {
					methodName = t.getName();
					decls.addAll(t.getConstructors());
				}
			}
		}
		//filter for name
		ArrayList<ASTFuncDecl> nameFilteredDeclarations = new ArrayList<>();
		if (decls.isEmpty() == false) {
			if (methodName == null) {
				nameFilteredDeclarations.addAll(decls); //nothing to filter
			}else {
				for (ASTFuncDecl fd : decls) {
					if (fd.getName().equals(methodName))
						nameFilteredDeclarations.add(fd);
				}
			}
		}
		//filter for parameters 
		ArrayList<ASTFuncDecl> countFilteredDeclarations = new ArrayList<>();
		if (nameFilteredDeclarations != null && nameFilteredDeclarations.isEmpty() == false) {
			for (ASTFuncDecl fd : nameFilteredDeclarations) {
				if (fd.getParameterCount() >= getArgumentSize())
					countFilteredDeclarations.add(fd);
			}
		}
		return countFilteredDeclarations;
	}

	@Override
	public void getCompletionsAndFilter(String statement, boolean hasDotAtEnd, Scope scope, Set<ICompletion> completions, Set<ICompletionFilter> filter) {
		//find the declarations of the called method
		if (hasDotAtEnd) {
			//in this case we assume that the function has been finished and we actually do an ASTPropAccess
			IType t = getType(scope);
			if (t != null)
				t.insertCompletions(completions);
//			super.getCompletionsAndFilter(statement, hasDotAtEnd, scope, completions, filter);
			return ;
		}
		List<ASTFuncDecl> decls = _searchDeclaration(scope);
		int currentParameterIdx = getChildren() != null ? getChildren().size()-2 : 0; //first one is the name
		if (currentParameterIdx < 0) currentParameterIdx = 0;
		//find the types for the parameter positions
		HashSet<IType> possibleTypes = new HashSet<>();
		for (ASTFuncDecl decl : decls) {
			List<ASTParamDecl> pl = decl.getParameters();
			if (currentParameterIdx < pl.size() && currentParameterIdx >= 0) {
				ASTParamDecl pd = pl.get(currentParameterIdx);
				IType pt = pd.getType(scope);
				if (pt != null)
					possibleTypes.add(pt);
			}
		}
		if (possibleTypes.isEmpty() == false) {
			//add type filter
			CompletionUtils.addMultipleTypeFilter(possibleTypes, filter);
		}
		//now we have the type filter but no completions
		//those are created based on the current parameter index
		ASTNode n = getChildren() != null ? getChildren().get(currentParameterIdx+1) : null;//first is the name
		if (n != null && n instanceof UnknownStatement == false)
			n.getCompletionsAndFilter(statement, hasDotAtEnd, scope, completions, filter);
		else
			CompletionUtils.completionsFromNamedElement(scope.getAllNamedElements(), completions);
//		super.getCompletionsAndFilter(statement, hasDotAtEnd, scope, completions, filter);
	}
}
