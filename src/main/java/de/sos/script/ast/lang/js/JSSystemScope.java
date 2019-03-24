package de.sos.script.ast.lang.js;

import de.sos.script.ast.lang.NativeProperty;
import de.sos.script.ast.lang.SystemScope;
import de.sos.script.ast.lang.java.JarManager;

public class JSSystemScope extends SystemScope {


	public JSSystemScope(JarManager jmgr) {
		super(jmgr);
		_addChild(new NativeProperty("Packages", mJarManagerType));
	}

	
	
//	@Override
//	protected void collectNamedElements(Map<String, INamedElement> map) {
//		super.collectNamedElements(map);
//		map.put("Packages", mJarManagerType);
//	}


	
	

	

}
