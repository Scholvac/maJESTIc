package de.sos.script.impl.lang.py;

import de.sos.script.IEntryPoint;
import de.sos.script.IScript;
import de.sos.script.IScriptManager;
import de.sos.script.ast.lang.IASTConverter;
import de.sos.script.ast.lang.SystemScope;
import de.sos.script.impl.AbstractScriptManager;
import de.sos.script.run.IScriptDebugExecutor;
import de.sos.script.run.IScriptExecuter;
import de.sos.script.run.lang.py.JythonDebugExecutor;
import de.sos.script.run.lang.py.JythonReleaseExecutor;

public class PyScriptManager extends AbstractScriptManager implements IScriptManager {

	public static final String LANG_JYPTHON = "Jython";
	
	@Override
	public String getLanguage() {
		return LANG_JYPTHON;
	}
	
	@Override
	public SystemScope getSystemScope() {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public boolean hasEditorSupport(IScript script) {
		return false;
	}

	@Override
	protected IASTConverter getASTConverter() {
		
		return null;
	}
	
	@Override
	public int getBlockStartIndex(String content, int pos) {
		
		return -1;
	}
	
	@Override
	public String createTemplate(IEntryPoint entryPoint) {
		System.out.println("Not yet supported");
		return null;
	}

	@Override
	public boolean hasDebugSupport(IScript script) {
		return true;
	}

	@Override
	public IScriptExecuter createExecutor(IScript script) {
		if (script == null)
			return null;
		//TODO: check if the script is a jython script
		return JythonReleaseExecutor.createExecutor(script);
	}

	@Override
	public IScriptDebugExecutor createDebugExecutor(IScript script) {
		if (script == null)
			return null;
		//TODO: check if the script is a rhino script
		return JythonDebugExecutor.createExecutor(script);
	}

	

	

}
