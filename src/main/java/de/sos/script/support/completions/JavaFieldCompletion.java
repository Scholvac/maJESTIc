package de.sos.script.support.completions;

import java.util.Optional;

import org.fife.rsta.ac.common.VariableDeclaration;
import org.fife.rsta.ac.java.Util;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;

import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.comments.Comment;

import de.sos.script.ast.lang.java.JavaType;

public class JavaFieldCompletion implements ICompletion {

	private final JavaType 				mType;
	private final FieldDeclaration		mField;
	private final VariableDeclarator	mVariable;

	public JavaFieldCompletion(VariableDeclarator v, FieldDeclaration fd, JavaType javaType) {
		mType = javaType;
		mField = fd;
		mVariable = fd.getVariable(0);
	}

	@Override
	public String getFeatureString() {
		return mVariable.getNameAsString();
	}

	@Override
	public String getTypeAsString() {
		return mVariable.getTypeAsString();
	}

	@Override
	public String getDefinedIn() {
		return mType.getName();
	}

	@Override
	public int hashCode() {
		return getFeatureString().hashCode();
	}
	
	@Override
	public String getDescription() {
		Optional<Comment> comment = mField.getComment();
		if (comment.isPresent()) {
			return comment.get().toString();
		}
		return "no documentation available";
	}
	
	@Override
	public String toString() {
		return "JavaFieldCompletion [" + getFeatureString() + "]";
	}

	
	public Completion getUICompletion(CompletionProvider provider) {
		String replacementText = getFeatureString();
		String shortDesc = "<html><b>" + getTypeAsString() + "</b> - <i>" + getDefinedIn() + "</i></html>";
		String summary = Util.docCommentToHtml(getDescription()); // "<html><i>" + getDescription() + "</i></html>";
		BasicCompletion bc = new BasicCompletion(provider, replacementText, shortDesc, summary);
		return bc;
	}
}
