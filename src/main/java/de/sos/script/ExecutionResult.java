package de.sos.script;

import java.util.ArrayList;
import java.util.List;

import org.mozilla.javascript.NativeJavaObject;

public class ExecutionResult {


	private final String	mIdentifier;
	
	
	private Throwable 		mError;
	private Object			mScriptResult;
	private long			mStart;
	private long			mEnd;
	

	public ExecutionResult(final String identifier) {
		mIdentifier = identifier;
	}

	public void setError(final Throwable e) {
		mError = e;
	}

	public void setScriptResult(final Object scriptResult) {
		if (scriptResult != null && scriptResult instanceof NativeJavaObject)
			mScriptResult = ((NativeJavaObject)scriptResult).unwrap();
		else
			mScriptResult = scriptResult;
	}

	public void tic() {
		mStart = System.currentTimeMillis();
	}
	public void tac() {
		mEnd = System.currentTimeMillis();
	}


	public String getIdentifier() { return mIdentifier; }
	public Object getResult() { return mScriptResult; }
	public Throwable getError() { return mError; }
	public boolean hasError() { return mError != null; }
	public long getDuration() { return mEnd - mStart; }

	public void reset() {
		mStart = mEnd = -1;
		mError = null;
		mScriptResult = null;
	}
	
	
	
}
