package de.sos.script.run.lang.js.dbg;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.debug.DebugFrame;
import org.mozilla.javascript.debug.DebuggableScript;

import de.sos.script.ScriptVariable;
import de.sos.script.run.dbg.DebugContext;
import de.sos.script.run.dbg.DebugContext.IDbgFrameCallback;

/**
 * Object to represent one stack frame.
 */
public class DbgFrame implements DebugFrame, IDbgFrameCallback {

	private final DebugContext 	mDbgContext;
	private Context 			mContext;
	private DebuggableScript 	mDebuggableScript;
	
	private Scriptable			mScope;
	private Scriptable			mThisObj;
	
	private int 				mCurrentLine;

	public DbgFrame(DebugContext ctx, Context cx, DebuggableScript fnOrScript) {
		mDbgContext = ctx;
		mContext = cx;
		mDebuggableScript = fnOrScript;
		
	}

	@Override
	public void onEnter(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
		mThisObj = thisObj;
		mScope = scope;
		mDbgContext.push(this);
		System.out.println("onEnter");
	}

	@Override
	public void onLineChange(Context cx, int lineNumber) {
		mCurrentLine = lineNumber-1;
		mDbgContext.notifyNextLine(); //JS counts from 0 we do count from 1
	}

	@Override
	public void onExceptionThrown(Context cx, Throwable ex) {
		System.out.println("onException");
	}

	@Override
	public void onExit(Context cx, boolean byThrow, Object resultOrException) {
		System.out.println("onExit");
		mDbgContext.pop();
	}

	@Override
	public void onDebuggerStatement(Context cx) {
		System.out.println("onDebuggerStatement");
	}
	
	
	@Override
	public Collection<ScriptVariable> getAccessableVariables() {
		Set<ScriptVariable> out = new HashSet<>();
		collectVariables(mScope, out);
		return out;
	}

	
	private void collectVariables(Scriptable scope, Set<ScriptVariable> out) {
		Object[] ids = scope.getIds();
		if (ids != null && ids.length > 0) {
			for (Object objID : ids) {
				String name = objID.toString();
				if (name.equals("arguments")) continue;
				Object value = scope.get(name, scope);
				if (value instanceof Function)
					continue;
				if (value instanceof Undefined)
					continue;
				
				out.add(new JSDbgVariable(name, value, scope));
			}
		}
		Scriptable parent = scope.getParentScope();
		if (parent != null && parent != scope) {
			collectVariables(parent, out);
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////
	//								IDbgFrameCallback									//
	//////////////////////////////////////////////////////////////////////////////////////
	
	
	@Override
	public String getSourceIdentifier() {
		return mDebuggableScript.getSourceName();
	}

	@Override
	public int getLine() {
		return mCurrentLine;
	}



   
}