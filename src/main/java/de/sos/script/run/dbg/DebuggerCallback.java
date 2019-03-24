package de.sos.script.run.dbg;

public interface DebuggerCallback {
	
	public enum NextAction {
		STEP_INTO, 
		STEP_OUT,
		STEP_OVER,
		CONTINUE,
		STOP
	}
	
	NextAction interrupt(final DebugContext context, final BreakPoint bp);
}
