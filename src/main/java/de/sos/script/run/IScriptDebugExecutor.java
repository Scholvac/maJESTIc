package de.sos.script.run;

import de.sos.script.run.dbg.DebugContext;
import de.sos.script.run.dbg.DebuggerCallback;

public interface IScriptDebugExecutor extends IScriptExecuter{
	
	DebugContext getDebugContext();
	
	void setDebugCallback(final DebuggerCallback callback);
	
}
