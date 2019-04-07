package de.sos.script;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.sos.script.IEntryPoint.IMutableEntryPoint;
import de.sos.script.ScriptVariable.VariableDirection;


public class EntryPoint implements IMutableEntryPoint {

	public static class VariableListBuilder {
		private List<IScriptVariable> list = new ArrayList<>();
		
		public VariableListBuilder add(final String name, final Object value) {
			list.add(new ScriptVariable(name, value, VariableDirection.INOUT));
			return this;
		}
		public VariableListBuilder add(final String name, final Object value, VariableDirection dir) {
			list.add(new ScriptVariable(name, value, dir));
			return this;
		}
		public List<IScriptVariable> get() { return list; }
	}
	
	public static VariableListBuilder var() {
		return new VariableListBuilder();
	}
	
	private final String			mFunctionName;
	
	private final String[]			mArgumentNames;
	private final Object[]			mArgumentValues;
	
	private final String[]			mVariableNames;
	private final Object[]			mVariableValues; //its only the array that is final, not the content
	private final String[] 			mReadBackNames;
	
	
	public static String[] extractNames(final List<IScriptVariable> vars) {
		if (vars == null)
			return new String[]{};
		String[] names = new String[vars.size()];
		for (int i = 0; i < names.length; i++) names[i] = vars.get(i).getName();
		return names;
	}
	public static String[] extractReadBackNames(final List<IScriptVariable> vars) {
		if (vars == null)
			return new String[]{};
		List<String> tmp = new ArrayList<>();
		for (IScriptVariable var : vars) {
			if (var.getDirection() != VariableDirection.IN)
				tmp.add(var.getName());
		}
		return tmp.toArray(new String[tmp.size()]);
	}
	public static Object[] extractValues(final List<IScriptVariable> vars) {
		if (vars == null) 
			return new Object[] {};
		Object[] values = new Object[vars.size()];
		for (int i = 0; i < values.length; i++) values[i] = vars.get(i).getValue();
		return values;
	}
	public EntryPoint(final String functionName, List<IScriptVariable> arguments, List<IScriptVariable> variables) {
		this(functionName, extractNames(arguments), extractValues(arguments), extractNames(variables), extractValues(variables), extractReadBackNames(variables));
	}
	
	
	public EntryPoint(String functionName, String[] argumentNames, Object[] argumentValues, String[] variableNames, Object[] variableValues, final String[] readBackNames) {
		mFunctionName = functionName;
		mArgumentNames = argumentNames;
		mArgumentValues = argumentValues;
		mVariableNames = variableNames;
		mVariableValues = variableValues;
		mReadBackNames = readBackNames;
		if (mVariableNames == null || mVariableValues == null)
			throw new IllegalArgumentException("Variable values and names may not be null - use empty arrays");
		if (mVariableNames.length != mVariableValues.length)
			throw new IllegalArgumentException("Name and Value arrays of variables do not have the same size");
		
		if (mArgumentNames == null || mArgumentValues == null)
			throw new IllegalArgumentException("Arguments values and names may not be null - use empty arrays");
		if (mArgumentNames.length != mArgumentValues.length)
			throw new IllegalArgumentException("Name and Value arrays of arguments do not have the same size");
	}
	@Override
	public String 					getFunctionName() { return mFunctionName; }


	@Override
	public int getArgumentCount() {
		return mArgumentNames.length;
	}


	@Override
	public String[] getArgumentNames() {
		return mArgumentNames;
	}

	@Override
	public Object[] getArgumentValues() {
		return mArgumentValues;
	}


	@Override
	public int getVariableCount() {
		return mVariableNames.length;
	}


	@Override
	public String[] getVariableNames() {
		return mVariableNames;
	}

	@Override
	public Object[] getVariableValues() {
		return mVariableValues;
	}
	@Override
	public String[] getReadBackNames() {
		return mReadBackNames;
	}

	@Override
	public void writeVariableValues(String[] names, Object[] values) {
		for (int i = 0; i < names.length; i++) {
			final String n = names[i];
			int vIdx = Arrays.binarySearch(mVariableNames, n);
			if (vIdx < 0)
				throw new NullPointerException("Variable: " + n + " does not exists");
			mVariableValues[vIdx] = values[i];
		}
	}
	
	public <T> T getArgumentValue(final String name) {		
		int vIdx = Arrays.binarySearch(mArgumentNames, name);
		if (vIdx < 0)
			throw new NullPointerException("Argument: " + name + " does not exists");
		return (T)mArgumentValues[vIdx];
	}
	public <T> T getVariableValue(final String name) {		
		int vIdx = Arrays.binarySearch(mVariableNames, name);
		if (vIdx < 0)
			throw new NullPointerException("Variable: " + name + " does not exists");
		return (T)mVariableValues[vIdx];
	}
	
	
	/////////////////	IMutableEntryPoint /////////////////////////////////
	@Override
	public void setVariableValue(String name, Object value) {
		int vIdx = Arrays.binarySearch(mVariableNames, name);
		if (vIdx < 0)
			throw new NullPointerException("Variable: " + name + " does not exists");
		mVariableValues[vIdx] = value;
	}
	@Override
	public void setArgumentValue(String name, Object newValue) {
		int vIdx = Arrays.binarySearch(mArgumentNames, name);
		if (vIdx < 0)
			throw new NullPointerException("Argument: " + name + " does not exists");
		mArgumentValues[vIdx] = newValue;
	}
	
	
//
//	public IScriptVariable getVariable(final String name) {
//		if (getVariables() == null || getVariables().isEmpty())
//			return null;
//		for (int i = 0; i < getVariables().size(); i++) {
//			final IScriptVariable v = getVariables().get(i);
//			if (v.getName().equals(name))
//				return v;
//		}
//		return null;
//	}
//	public <T> T getVariableValue(String name) {
//		IScriptVariable var = getVariable(name);
//		if (var != null)
//			return (T)var.getValue();
//		return null;
//	}
//	
//	
//	public IScriptVariable getArgument(final String name) {
//		if (getFunctionParameter() == null || getFunctionParameter().isEmpty())
//			return null;
//		for (int i = 0; i < getFunctionParameter().size(); i++) {
//			final IScriptVariable v = getFunctionParameter().get(i);
//			if (v.getName().equals(name))
//				return v;
//		}
//		return null;
//	}
//	public <T> T getArgumentValue(String name) {
//		IScriptVariable var = getArgument(name);
//		if (var != null)
//			return (T)var.getValue();
//		return null;
//	}

	
	
	
	
}
