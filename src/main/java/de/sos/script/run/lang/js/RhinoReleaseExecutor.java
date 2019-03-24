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

import de.sos.script.EntryPoint;
import de.sos.script.ExecutionResult;
import de.sos.script.IScript;
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
	protected ScriptableObject 			mScope;
	
	private HashMap<String, Function>	mFunctions = new HashMap<>();
	
	
	
	protected RhinoReleaseExecutor(final IScript script) {
		mScript = script;
		mLog = LoggerFactory.getLogger(getLoggerName(script));
		mResult = new ExecutionResult(script.getIdentifier());
	}

	
	
	protected Context createNewContext() {
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
	



	private Function getFunction(EntryPoint entryPoint) {
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
	public ExecutionResult executeScript(EntryPoint entryPoint) {
		if (mLog.isTraceEnabled()) mLog.trace("execute script {} with entry point {}", mScript.getIdentifier(), entryPoint);
		//TODO: check the method name
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
			Object[] arguments = getArguments(entryPoint);
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
	


	


	public Object[] getArguments(EntryPoint ep) {
		List<ScriptVariable> args = ep.getFunctionParameter();
		//we just create the array and have to skip the name
		if (args == null || args.isEmpty())
			return new Object[] {};
		Object[] arguments = new Object[args.size()];
		for (int i = 0; i < arguments.length; i++) {
			Scriptable aw = JavaAdapter.createAdapterWrapper(mScope, args.get(i).getValue());
			arguments[i] = args.get(i).getValue();
		}
		return arguments;
	}



	public void writeVariables(EntryPoint ep) {
		final List<ScriptVariable> list = ep.getVariables();
		if (list == null) return ;
		for (int i = 0; i < list.size(); i++) {
			//we do write each object, thereby it does not matter if we have a direction
			//as we need the variable anyhow (in case of OUT-direction)
			//the direction is only used, when reading back the values
			ScriptVariable var = list.get(i);
			mScope.put(var.getName(), mScope, var.getValue());
		}		
	}
	public void readVariables(EntryPoint ep, ExecutionResult result) {
		final List<ScriptVariable> list = ep.getVariables();
		if (list == null) return ;
		for (int i = 0; i < list.size(); i++) {
			final ScriptVariable var = list.get(i);
			//at this point we do only read those variables, that are tagged either with 
			//OUT or INOUT
			if (var.getDirection() == VariableDirection.IN)
				continue; 
			final String varName = var.getName();
			Object value = mScope.get(varName, mScope);
			Object old = var.setValue(value);
			result.add(var, old);
		}
	}


	

}
