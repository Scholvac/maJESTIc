package de.sos.script.run.lang.js;

import java.util.HashMap;
import java.util.List;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.JavaAdapter;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.sos.script.ExecutionResult;
import de.sos.script.IEntryPoint;
import de.sos.script.IScript;
import de.sos.script.IScriptVariable;
import de.sos.script.ScriptVariable;
import de.sos.script.ScriptVariable.VariableDirection;
import de.sos.script.run.IScriptExecuter;

public class RhinoReleaseExecutor implements IScriptExecuter {

	public static final int RHINO_RELEASE_OPTIMIZATION_LEVEL = 9;
	
	public static IScriptExecuter createExecutor(IScript script) {
		return new RhinoReleaseExecutor(script);
	}

	protected final IScript				mScript;
	protected final Logger				mLog;
	protected final ExecutionResult 	mResult;
	
	private Context						mContext;
	private Thread						mContextThread;
	
	protected ScriptableObject 			mScope;
	
	private HashMap<String, Function>	mFunctions = new HashMap<>();
	
	
	
	protected RhinoReleaseExecutor(final IScript script) {
		mScript = script;
		mLog = LoggerFactory.getLogger(getLoggerName(script));
		mResult = new ExecutionResult(script.getIdentifier());
	}

	
	
	protected Context createNewContext() {
		mContextThread = Thread.currentThread();
		Context context = Context.enter();
		context.setDebugger(null, null);
		context.setGenerateObserverCount(false);
		context.setOptimizationLevel(RHINO_RELEASE_OPTIMIZATION_LEVEL);
		context.setGeneratingDebug(false);
		context.setGeneratingSource(false);
		return context;
	}
	
	
	public void compile() {
		if (mLog.isDebugEnabled()) mLog.debug("Compile script {}... ", mScript.getIdentifier());
		if (mContext == null)
			mContext = createNewContext();
		
		final ScriptableObject parentScope = RhinoGlobalSharedScope.getSystemScope(mContext, mScript.getManager().getSystemScope());
		mScope = mContext.initStandardObjects(parentScope, true);
		mContext.evaluateString(mScope, mScript.getContent(), mScript.getIdentifier(), 1, null);
		
		if (mLog.isDebugEnabled()) mLog.debug("... script {} compiled.", mScript.getIdentifier());			
	}
	



	private Function getFunction(IEntryPoint entryPoint) {
		final String fn = entryPoint.getFunctionName();
		Function res = mFunctions.get(fn);
		if (res == null) {
			res = (Function) mScope.get(fn, mScope);
			if (res == null) {
				mLog.error("Could not find function with name {} in script {}", fn, mScript.getIdentifier());
			}else
				mFunctions.put(fn, res);
		}
		return res;
	}
	
	public boolean isCompiled() {
		return mScope != null;
	}
	
	@Override
	public ExecutionResult executeScript(IEntryPoint entryPoint) {
		if (mLog.isTraceEnabled()) mLog.trace("execute script {} with entry point {}", mScript.getIdentifier(), entryPoint);

		//check the context and if invalid, rebuild it
		if (mContext != null) {
			if (mContextThread!=Thread.currentThread()) {
				mContext = createNewContext();
			}
		}
		
		if (!isCompiled()) {
			compile();
		}
	
		try {
			//clear the result. We only have one result for all executions (e.g. one instance)
			//to save memory and allocation time
			mResult.reset();
			
			mResult.tic();
			writeVariables(entryPoint);
			
			Function func = getFunction(entryPoint);
			final Object[] arguments = entryPoint.getArgumentValues(); //getArguments(entryPoint);
			Object scriptResult = func.call(mContext, mScope, func, arguments);
			
			readVariables(entryPoint, mResult);				
			mResult.tac();//just a small statistic
			mResult.setScriptResult(scriptResult);
		}catch(Exception | Error e) {
			mLog.error("Failed to execute script with error", e);
			e.printStackTrace();
			mResult.setError(e);
			mResult.setScriptResult(null);
		}
		
		if (mLog.isTraceEnabled()) mLog.trace("...script {} execution finished with result {}", mScript.getIdentifier(), mResult);
		return mResult;
	}







	protected String getLoggerName(IScript script) {
		return script.getIdentifier() + "_RhinoRelFunc";
	}	
	


	


//	public Object[] getArguments(IEntryPoint ep) {
//		List<IScriptVariable> args = ep.getFunctionParameter();
//		//we just create the array and have to skip the name
//		if (args == null || args.isEmpty())
//			return new Object[] {};
//		Object[] arguments = new Object[args.size()];
//		for (int i = 0; i < arguments.length; i++) {
//			Scriptable aw = JavaAdapter.createAdapterWrapper(mScope, args.get(i).getValue());
//			arguments[i] = args.get(i).getValue();
//		}
//		return arguments;
//	}



	public void writeVariables(IEntryPoint ep) {
		String[] names = ep.getVariableNames();
		if (names.length == 0)
			return ;
		Object[] values = ep.getVariableValues();
		if (names == null || values == null || values.length != names.length)
			throw new IllegalArgumentException("Variable lists does not match");

		for (int i = 0; i < names.length; i++) {
			//we do write each object, thereby it does not matter if we have a direction
			//as we need the variable anyhow (in case of OUT-direction)
			//the direction is only used, when reading back the values
			mScope.put(names[i], mScope, values[i]);
		}		
	}
	public void readVariables(IEntryPoint ep, ExecutionResult result) {
		String[] names = ep.getReadBackNames();
		if (names.length == 0)
			return ;
		Object[] values = new Object[names.length];
		for (int i = 0; i < names.length; i++) {
			values[i] = mScope.get(names[i], mScope);
		}
		ep.writeVariableValues(names, values);
	}


	

}
