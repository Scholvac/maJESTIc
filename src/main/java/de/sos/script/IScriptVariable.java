package de.sos.script;

import de.sos.script.ScriptVariable.VariableDirection;

public interface IScriptVariable {

	String getName();

	Object getValue();

	VariableDirection getDirection();

	Class<?> getExpectedType();

	Object setValue(Object newValue);

	String getStringValue();

}