package de.sos.script.run;

import de.sos.script.EntryPoint;
import de.sos.script.ExecutionResult;

public interface IScriptExecuter {
	
	public ExecutionResult executeScript(final EntryPoint entryPoint);
	
}
