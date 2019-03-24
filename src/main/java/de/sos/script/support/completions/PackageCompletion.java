package de.sos.script.support.completions;

import de.sos.script.ast.lang.java.Package;

public class PackageCompletion extends BaseCompletion implements ICompletion {

	public PackageCompletion(Package p) {
		super(p.getName(), "Java-Package", p.getFullQualifiedName(), "Java package loaded by JarManager");
	}

	@Override
	public int hashCode() {
		return getFeatureString().hashCode();
	}
	
	@Override
	public String toString() {
		return "PackageCompletion [" + getFeatureString() + "]";
	}
}
