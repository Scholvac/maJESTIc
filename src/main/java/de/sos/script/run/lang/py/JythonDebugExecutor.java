package de.sos.script.run.lang.py;

import org.python.core.ThreadState;

import de.sos.script.IScript;
import de.sos.script.run.IScriptDebugExecutor;
import de.sos.script.run.dbg.DebugContext;
import de.sos.script.run.dbg.DebuggerCallback;
import de.sos.script.run.lang.py.dbg.JythonTraceFunc;

public class JythonDebugExecutor extends JythonReleaseExecutor implements IScriptDebugExecutor {

	protected DebugContext 	mDebugContext;
	private DebuggerCallback mCallback;
	
	public static IScriptDebugExecutor createExecutor(IScript script) {
		return new JythonDebugExecutor(script);
	}
	
	protected JythonDebugExecutor(final IScript script) {
		super(script);
	}
	
	
	@Override
	protected ThreadState getThreadState() {
		ThreadState ts = super.getThreadState();
		ts.tracefunc = new JythonTraceFunc(getDebugContext());
		ts.tracing = true;
		return ts;
	}

	/////////////////////////////////////////////////////////////////////////////////////////////
	//									Debug
	/////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public DebugContext getDebugContext() {
		if (mDebugContext == null)
			mDebugContext = new DebugContext(mCallback);
		return mDebugContext;
	}

	@Override
	public void setDebugCallback(DebuggerCallback callback) {
		if (callback != mCallback)
			mDebugContext = null;
		mCallback = callback;
		
	}
}
