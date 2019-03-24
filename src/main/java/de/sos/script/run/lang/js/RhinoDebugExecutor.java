package de.sos.script.run.lang.js;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.debug.DebugFrame;
import org.mozilla.javascript.debug.DebuggableScript;
import org.mozilla.javascript.debug.Debugger;
import org.mozilla.javascript.tools.debugger.Dim;

import de.sos.script.IScript;
import de.sos.script.run.IScriptDebugExecutor;
import de.sos.script.run.dbg.DebugContext;
import de.sos.script.run.dbg.DebuggerCallback;
import de.sos.script.run.lang.js.dbg.DbgFrame;

public class RhinoDebugExecutor extends RhinoReleaseExecutor implements IScriptDebugExecutor, Debugger {

	public static IScriptDebugExecutor createExecutor(IScript script) {
		return new RhinoDebugExecutor(script);
	}


	protected RhinoDebugExecutor(IScript script) {
		super(script);
	}
	
	
	
	@Override
	protected Context createNewContext() {
		Context context = Context.enter();
		context.setGenerateObserverCount(false);
		context.setOptimizationLevel(-1); //interactive mode
		context.setGeneratingDebug(true);
		context.setGeneratingSource(true);
		context.setDebugger(this, new Dim.ContextData());
		return context;
	}
	
	
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////
	//									Debug
	/////////////////////////////////////////////////////////////////////////////////////////////
	
	private DebugContext			mDebugContext = null;
	private DebuggerCallback		mCallback = null;
	
	@Override
	public DebugContext getDebugContext() {
		if (mDebugContext == null)
			mDebugContext = new DebugContext(mCallback);
		return mDebugContext;
	}
	
	@Override
	public void handleCompilationDone(Context cx, DebuggableScript fnOrScript, String source) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public DebugFrame getFrame(Context cx, DebuggableScript fnOrScript) {
		return new DbgFrame(getDebugContext(), cx, fnOrScript);
	}


	@Override
	public void setDebugCallback(DebuggerCallback callback) {
		if (callback != mCallback)
			mDebugContext = null;
		mCallback = callback;
	}	
}
