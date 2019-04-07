package de.sos.script.support.completions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.fife.rsta.ac.java.Util;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.FunctionCompletion;
import org.fife.ui.autocomplete.ParameterizedCompletion.Parameter;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;

import de.sos.script.ast.lang.java.JavaType;

public class JavaMethodCompletion implements ICompletion {

	private final MethodDeclaration mMethod;
	private final JavaType 			mType;

	public JavaMethodCompletion(MethodDeclaration md, JavaType javaType) {
		mMethod = md;
		mType = javaType;
	}

	@Override
	public String getFeatureString() {
		return mMethod.getSignature().toString();//TODO: this can be changed into a short-hand completion
	}

	@Override
	public String getTypeAsString() {
		return mMethod.getTypeAsString();
	}

	@Override
	public String getDefinedIn() {
		return mType.getName();
	}

	@Override
	public String getDescription() {
		Optional<Comment> comment = mMethod.getComment();
		if (comment.isPresent())
			return comment.get().toString();
		return "<p><strong>no documentation available</strong></p>";
	}

	@Override
	public int hashCode() {
		return getFeatureString().hashCode();
	}
	@Override
	public String toString() {
		return "JavaMethodCompletion [" + getFeatureString() + "]";
	}
	
	@Override
	public Completion getUICompletion(CompletionProvider provider) {
		FunctionCompletion fc = new FunctionCompletion(provider, mMethod.getNameAsString(), getTypeAsString());
		String doc = getDescription().trim();
		doc = Util.docCommentToHtml(doc);
		int idx = doc.indexOf("</style>");
		doc = "<html>"+doc.substring(idx+8);
		fc.setShortDescription(doc);
		List<Parameter> params = new ArrayList<>();
		int pc = mMethod.getParameters().size();
		if (pc > 0) {
			for (int i = 0; i < pc; i++) {
				final com.github.javaparser.ast.body.Parameter p = mMethod.getParameter(i);
				final boolean last = i == pc-1;
				Parameter param = new Parameter(p.getTypeAsString(), p.getNameAsString(), last);
				params.add(param);
			}
			fc.setParams(params);
		}
		return fc;
//		return ICompletion.super.getUICompletion(provider);
	}
}
