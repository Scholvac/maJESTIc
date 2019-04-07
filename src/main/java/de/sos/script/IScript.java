package de.sos.script;

import de.sos.script.ast.CompilationUnit;
import de.sos.script.ast.util.Scope;
import de.sos.script.run.IScriptDebugExecutor;
import de.sos.script.run.IScriptExecuter;
import de.sos.script.run.dbg.DebuggerCallback;

public interface IScript {

	IScriptSource getSource();

	CompilationUnit getCompilationUnit();
	IScriptManager getManager();

	void setEntryPoint(final IEntryPoint entryPoint);
	IEntryPoint getEntryPoint();
	
	IScriptExecuter getExecutor();
	IScriptDebugExecutor getDebugExecutor();
	

	default String getIdentifier() {
		return getSource().getIdentifier();
	}
	default String getContent() {
		return getSource().getContentAsString();
	}
	/** Returns the language string of this script. 
	 * The language string can be used to identify the correct script manager. 
	 * @return the language string, given by the script source.
	 */
	default String getLanguage() {
//		return getSource().getLanguage();
		return getManager().getLanguage();
	}
	/** 
	 * Returns the length of the script content
	 * @return length of script content
	 */
	default int getLength() {
		final String content = getSource().getContentAsString();
		if (content != null)
			return content.length();
		return 0;
	}
	default Scope getScopeForPosition(int pos) {
		CompilationUnit cu = getCompilationUnit();
		if (cu == null) {
			return null;
		}
		IEntryPoint ep = getEntryPoint();
		if (ep != null) {
			cu.insertEntryPoint(ep);
		}
		return cu.getScopeForIndex(pos);
	}

	default ExecutionResult execute() { return execute(getEntryPoint()); }
	default ExecutionResult execute(final IEntryPoint ep) {
		IScriptExecuter exec = getExecutor();
		if (exec != null)
			return exec.executeScript(ep);
		return null;
	}
	
	default ExecutionResult debug(DebuggerCallback callback) { return debug(getEntryPoint(), callback); }
	default ExecutionResult debug(final IEntryPoint ep, DebuggerCallback callback) {
		IScriptDebugExecutor exec = getDebugExecutor();
		if (exec != null) {
			exec.setDebugCallback(callback);
			return exec.executeScript(ep);
		}
		return null;
	}

	default boolean isDebugSupported() {
		return getManager().hasDebugSupport(this);
	}

	
	
	
	
}
