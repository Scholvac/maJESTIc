package de.sos.script.run.lang.py;

import java.util.HashMap;
import java.util.List;

import org.python.core.Py;
import org.python.core.PyCode;
import org.python.core.PyObject;
import org.python.core.PySystemState;
import org.python.core.ThreadState;
import org.python.util.PythonInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.sos.script.EntryPoint;
import de.sos.script.ExecutionResult;
import de.sos.script.IScript;
import de.sos.script.ScriptVariable;
import de.sos.script.ScriptVariable.VariableDirection;
import de.sos.script.run.IScriptExecuter;

public class JythonReleaseExecutor implements IScriptExecuter {

	
	public static final int JYTHON_RELEASE_OPTIMIZATION_LEVEL = 9;
		
	protected final PythonInterpreter 	mInterpreter;
	protected final IScript				mScript;
	protected final Logger				mLog;
	protected final ExecutionResult 	mResult;
	
	protected PyCode					mCode;
	protected PyObject					mEval;	//contains the whole script (which may is not used)
	
	private HashMap<String, PyObject>	mFunctions = new HashMap<>();
	
	public static IScriptExecuter createExecutor(IScript script) {
		return new JythonReleaseExecutor(script);
	}
	
	protected JythonReleaseExecutor(final IScript script) {
		mScript = script;
		mLog = LoggerFactory.getLogger(getLoggerName(script));
		mResult = new ExecutionResult(script.getIdentifier());
		mInterpreter = new PythonInterpreter(null, new PySystemState());
	}
	
	public void compile() {
		mCode = mInterpreter.compile(mScript.getContent(), mScript.getIdentifier());
		mEval = mInterpreter.eval(mCode);
	}
	
		
	protected String getLoggerName(IScript script) {
		return script.getIdentifier() + "_PyRelFunc";
	}

	private PyObject getFunction(EntryPoint entryPoint) {
		final String fn = entryPoint.getFunctionName();
		PyObject res = mFunctions.get(fn);
		if (res == null) {
			res = mInterpreter.get(fn);
			if (res == null) {
				mLog.error("Could not find function with name {} in script {}", fn, mScript.getIdentifier());
			}else
				mFunctions.put(fn, res);
		}
		return res;
	}
	
	public boolean isCompiled() { return mEval != null; }
	
			
	@Override
	public ExecutionResult executeScript(final EntryPoint entryPoint) {
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
			
			PyObject[] arguments = getArguments(entryPoint);
			PyObject function = getFunction(entryPoint);
			assert(function != null);
			ThreadState state = getThreadState();			
			PyObject res = function.__call__(state, arguments);
			mResult.setScriptResult(convertResult(res));
			
			readVariables(entryPoint, mResult);				
			mResult.tac();//just a small statistic
		}catch(Exception | Error e) {
			mLog.error("Failed to execute script with error", e);
			e.printStackTrace();
			mResult.setError(e);
			mResult.setScriptResult(null);
		}
		
		if (mLog.isTraceEnabled()) mLog.trace("...script {} execution finished with result {}", mScript.getIdentifier(), mResult);
		return mResult;
	}


	protected ThreadState getThreadState() {
		return Py.getThreadState();
	}

	public void writeVariables(final EntryPoint ep) {
		final List<ScriptVariable> list = ep.getVariables();
		if (list == null) return ;
		for (int i = 0; i < list.size(); i++) {
			//we do write each object, thereby it does not matter if we have a direction
			//as we need the variable anyhow (in case of OUT-direction)
			//the direction is only used, when reading back the values
			ScriptVariable var = list.get(i);
			mEval.__setattr__(var.getName(), Py.java2py(var.getValue()));
		}
	}

	public Object convertResult(PyObject res) {
		if (res == null)
			return null;
		return res.__tojava__(Object.class);
	}

	public void readVariables(final EntryPoint ep, ExecutionResult result) {
		final List<ScriptVariable> list = ep.getVariables();
		if (list == null) return ;
		for (int i = 0; i < list.size(); i++) {
			final ScriptVariable var = list.get(i);
			//at this point we do only read those variables, that are tagged either with 
			//OUT or INOUT
			if (var.getDirection() == VariableDirection.IN)
				continue; 
			final String varName = var.getName();
			PyObject py_value = mEval.__getattr__(varName);
			Object old = var.setValue(convertResult(py_value));
			result.add(var, old);
		}
	}

	public PyObject[] getArguments(final EntryPoint ep) {
		List<ScriptVariable> args = ep.getFunctionParameter();
		//we just create the array and have to skip the name
		if (args == null || args.isEmpty())
			return null;
		PyObject[] arguments = new PyObject[args.size()];
		for (int i = 0; i < arguments.length; i++)
			arguments[i] = Py.java2py(args.get(i).getValue());
		return arguments;
	}



}
