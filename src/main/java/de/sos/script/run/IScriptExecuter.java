package de.sos.script.run;

import de.sos.script.ExecutionResult;
import de.sos.script.IEntryPoint;

public interface IScriptExecuter {
	
	public ExecutionResult executeScript(final IEntryPoint entryPoint);
	
}
