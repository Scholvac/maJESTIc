package de.sos.script.run.lang.js.dbg;

import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;

import de.sos.script.run.dbg.DbgScriptVariable;

public class JSDbgVariable extends DbgScriptVariable {

	private Scriptable mScope;

	public JSDbgVariable(String name, Object value, Scriptable scope) {
		super(name, getJSValue(value, scope));
		mScope = scope;
		
	}

	private static Object getJSValue(Object value, Scriptable scope) {
		if (value instanceof NativeJavaObject)
			return ((NativeJavaObject)value).unwrap();
		return value;
	}
	
}
