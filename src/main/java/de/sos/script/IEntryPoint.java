package de.sos.script;

import java.util.List;

public interface IEntryPoint {
	
	public interface IMutableEntryPoint extends IEntryPoint {
		public void setArgumentValue(final String name, Object newValue);
		public void setVariableValue(final String name, Object newValue);
	}

	String getFunctionName();

	
	int getArgumentCount();
	String[] getArgumentNames();
	Object[] getArgumentValues();
	
	int getVariableCount();
	String[] getVariableNames();
	Object[] getVariableValues();
	
	/** Returns a list of variable names, that shall be read back, after the script has been executed.
	 * (e.g ScriptVariables with Direction OUT or INOUT). 
	 * This list may differ from <code>getVariableNames()</code> in terms that it is a subset. 
	 * In general there is no need to read back ObjectVariables, as they are handled as "call by reference" and their internal value
	 * is changed directly. However if the script does reasign a variable it may be usefull to get the value back.
	 * @return
	 */
	String[] getReadBackNames();
	void writeVariableValues(final String[] names, final Object[] values);

	/**
	 * Returns the types of the arguments (in the same order as getArgumentValues() and getArgumentNames()). 
	 * This method is mainly used for support reasons (e.g. knowing the provided arguments for the entry point, if the language
	 * itself does not provide any type-hints).
	 * @return
	 */
	default Class<?>[] getArgumentTypes(){
		Object[] args = getArgumentValues();
		if (args.length == 0)
			return new Class<?>[] {};
		Class<?>[] out = new Class<?>[args.length];
		for (int i = 0; i < out.length; i++) {
			out[i] = args[i] == null ? Object.class : args[i].getClass();
		}
		return out;
	}
	
}