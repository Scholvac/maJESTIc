package de.sos.script.support.completions;

import java.util.ArrayList;
import java.util.List;

import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.FunctionCompletion;
import org.fife.ui.autocomplete.ParameterizedCompletion.Parameter;

import de.sos.script.ast.ASTFuncDecl;
import de.sos.script.ast.ASTParamDecl;
import de.sos.script.ast.IType;

public class FunctionDeclaration implements ICompletion {

	private ASTFuncDecl mFunction;

	public FunctionDeclaration(ASTFuncDecl astFuncDecl) {
		mFunction = astFuncDecl;
	}

	@Override
	public String getFeatureString() {
		return mFunction.getSignature();
	}

	@Override
	public String getTypeAsString() {
		IType t = mFunction.getType(mFunction.getNodeScope());
		if (t != null)
			return t.getName();
		return "Unknown Type";
	}

	@Override
	public String getDefinedIn() {
		return "local";
	}

	@Override
	public String getDescription() {
		if (mFunction.getDocumentation() != null)
			return mFunction.getDocumentation();
		return "no description available"; //FIXME
	}
	
	
	@Override
	public Completion getUICompletion(CompletionProvider provider) {
		FunctionCompletion fc = new FunctionCompletion(provider, mFunction.getName(), getTypeAsString());
		List<Parameter> params = new ArrayList<>();
		List<ASTParamDecl> fparams = mFunction.getParameters();
		int pc = fparams == null ? 0 : fparams.size();
		if (pc > 0) {
			for (int i = 0; i < pc; i++) {
				ASTParamDecl p = fparams.get(i);
				final boolean last = i == pc-1;
				IType pt = p.getType(p.getNodeScope());
				String ptn = pt != null ? pt.getName() : "Any";
				Parameter param = new Parameter(ptn, p.getName(), last);
				params.add(param);
			}
			fc.setParams(params);
		}
		return fc;
//		return ICompletion.super.getUICompletion(provider);
	}

}
