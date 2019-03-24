package de.sos.script.ast.lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.sos.script.ast.ASTNode;
import de.sos.script.ast.IScopeProvider;
import de.sos.script.ast.lang.java.JarManager;
import de.sos.script.ast.lang.java.JarManagerType;
import de.sos.script.ast.util.Scope;

public class SystemScope extends ASTNode implements IScopeProvider {

	
	
	protected JarManagerType 						mJarManagerType;
	protected List<NativeFunctionDeclaration>		mNativeFunctions = new ArrayList<>();
	private final Scope								mScope = new Scope(this) {
		@Override
		public Scope getParentScope() { return null; };
	};
	
	public SystemScope(JarManager jmgr) {
		super(null, -1, -1);
		mJarManagerType = new JarManagerType(jmgr);
	}

	@Override
	public Scope getScope() {
		return mScope;
	}

	public List<NativeFunctionDeclaration> getNativeFunctions() {
		return Collections.unmodifiableList(mNativeFunctions);
	}
	
	public boolean registerNativeFunction(NativeFunctionDeclaration nfd) {
		if (nfd.getParent() != null)
			return false;
		_addChild(nfd);
		
		mNativeFunctions.add(nfd);
		return true;
	}

	@Override
	public List<Class<? extends ASTNode>> getAllowedChildren() {
		return new ArrayList<>();
	}







	
	
}
