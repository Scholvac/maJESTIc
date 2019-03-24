package de.sos.script.run.dbg;

public class BreakPoint {
	
	public final String		sourceIdentifier;
	public final int		lineNumber;
	
	public BreakPoint(final String srcId, final int line) {
		sourceIdentifier = srcId;
		lineNumber = line;
	}
	
	
	@Override
	public String toString() {
		return "BreakPoint [" + sourceIdentifier + ":" + lineNumber + "]";
	}
}
