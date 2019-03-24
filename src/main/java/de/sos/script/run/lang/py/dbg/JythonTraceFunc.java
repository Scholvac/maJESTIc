package de.sos.script.run.lang.py.dbg;

import org.python.core.PyException;
import org.python.core.PyFrame;
import org.python.core.PyObject;
import org.python.core.TraceFunction;

import de.sos.script.run.dbg.DebugContext;

public class JythonTraceFunc extends TraceFunction {

	private DebugContext mDebugContext;

	public JythonTraceFunc(DebugContext dbgContext) {
		mDebugContext = dbgContext;
	}

	@Override
	public TraceFunction traceCall(PyFrame frame) {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public TraceFunction traceReturn(PyFrame frame, PyObject ret) {
		System.out.println("return " + frame.f_lineno);
		return this;
	}

	@Override
	public TraceFunction traceLine(PyFrame frame, int line) {
		mDebugContext.notifyNextLine();
		return this;
	}

	@Override
	public TraceFunction traceException(PyFrame frame, PyException exc) {
		// TODO Auto-generated method stub
		return this;
	}

}
