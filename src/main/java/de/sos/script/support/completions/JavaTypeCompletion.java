package de.sos.script.support.completions;

import de.sos.script.ast.lang.java.JavaType;

public class JavaTypeCompletion extends BaseCompletion implements ICompletion {

	public JavaTypeCompletion(JavaType jt) {
		super(jt.getName(), "Java-Class", jt.getFullQualifiedName(), jt.getDescription());
		// TODO Auto-generated constructor stub
	}

	@Override
	public int hashCode() {
		return getFeatureString().hashCode();
	}
	
	@Override
	public String toString() {
		return "TypeCompletion [" + getFeatureString() + "]";
	}
}
