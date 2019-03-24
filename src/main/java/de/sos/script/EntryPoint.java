package de.sos.script;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.sos.script.ScriptVariable.VariableDirection;

public class EntryPoint {

	public static class VariableListBuilder {
		private List<ScriptVariable> list = new ArrayList<>();
		
		public VariableListBuilder add(final String name, final Object value) {
			list.add(new ScriptVariable(name, value, VariableDirection.INOUT));
			return this;
		}
		public VariableListBuilder add(final String name, final Object value, VariableDirection dir) {
			list.add(new ScriptVariable(name, value, dir));
			return this;
		}
		public List<ScriptVariable> get() { return list; }
	}
	
	public static VariableListBuilder var() {
		return new VariableListBuilder();
	}
	
	private final String			mFunctionName;
	private List<ScriptVariable>	mFunctionParameters;
	private List<ScriptVariable>	mScriptVariables;
	
	public EntryPoint(final String functionName, List<ScriptVariable> parameters, List<ScriptVariable> scriptVariables) {
		mFunctionName = functionName;
		mFunctionParameters = parameters;
		mScriptVariables = scriptVariables;
	}
	
	public String 					getFunctionName() { return mFunctionName; }
	public List<ScriptVariable> 	getVariables() { return mScriptVariables != null ? Collections.unmodifiableList(mScriptVariables) : null;}
	public List<ScriptVariable> 	getFunctionParameter() { return mFunctionParameters != null ? Collections.unmodifiableList(mFunctionParameters) : null; }

	public ScriptVariable getVariable(final String name) {
		if (mScriptVariables == null || mScriptVariables.isEmpty())
			return null;
		for (int i = 0; i < mScriptVariables.size(); i++) {
			final ScriptVariable v = mScriptVariables.get(i);
			if (v.getName().equals(name))
				return v;
		}
		return null;
	}
	public <T> T getVariableValue(String name) {
		ScriptVariable var = getVariable(name);
		if (var != null)
			return (T)var.getValue();
		return null;
	}
	
	
	public ScriptVariable getArgument(final String name) {
		if (mFunctionParameters == null || mFunctionParameters.isEmpty())
			return null;
		for (int i = 0; i < mFunctionParameters.size(); i++) {
			final ScriptVariable v = mFunctionParameters.get(i);
			if (v.getName().equals(name))
				return v;
		}
		return null;
	}
	public <T> T getArgumentValue(String name) {
		ScriptVariable var = getArgument(name);
		if (var != null)
			return (T)var.getValue();
		return null;
	}

	
	
	
	
}
