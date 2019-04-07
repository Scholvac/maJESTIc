package de.sos.script;

public interface IBreakPoint {

	public String getScriptIdentifier();
	public int getLine();
	
	public boolean isEnabled();
	public void enable(boolean enabled);	
	
}
