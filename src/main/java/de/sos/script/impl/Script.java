package de.sos.script.impl;

import de.sos.script.EntryPoint;
import de.sos.script.IEntryPoint;
import de.sos.script.IScript;
import de.sos.script.IScriptManager;
import de.sos.script.IScriptSource;
import de.sos.script.ast.CompilationUnit;
import de.sos.script.run.IScriptDebugExecutor;
import de.sos.script.run.IScriptExecuter;

public class Script implements IScript {

	private static IEntryPoint		sDefaultEntryPoint = new EntryPoint("main", null, null);
	
	private final IScriptManager	mManager;
	private final IScriptSource		mSource;
	
	
	private CompilationUnit			mCompilationUnit;
	private boolean					mInvalidCompilationUnit = true; //true if something on the script has changed but the CU has not been reparsed yet (or not successfully)
	
	private IEntryPoint				mEntryPoint = sDefaultEntryPoint;
	private IScriptExecuter			mReleaseExecutor;
	private IScriptDebugExecutor	mDebugExecutor;
	
	public Script(final IScriptManager mgr, final IScriptSource source) {
		mManager = mgr;
		mSource = source;
	}
	
	@Override
	public IScriptSource getSource() {
		return mSource;
	}
	
	
	
	@Override
	public IScriptManager getManager() {
		return mManager;
	}

	@Override
	public CompilationUnit getCompilationUnit() {
		if (mInvalidCompilationUnit || mCompilationUnit == null) {
			CompilationUnit cu = getManager().createCompilationUnit(getSource());
			if (cu != null) {
				mCompilationUnit = cu;
				mInvalidCompilationUnit = false;
			}
		}
		return mCompilationUnit;
	}

	@Override
	public void setEntryPoint(IEntryPoint entryPoint) { mEntryPoint = entryPoint;}
	@Override
	public IEntryPoint getEntryPoint() { return mEntryPoint; }

	@Override
	public IScriptExecuter getExecutor() {
		if (mReleaseExecutor == null) {
			mReleaseExecutor = getManager().createExecutor(this);
		}
		return mReleaseExecutor;
	}

	@Override
	public IScriptDebugExecutor getDebugExecutor() {
		if (mDebugExecutor == null)
			mDebugExecutor = getManager().createDebugExecutor(this);
		return mDebugExecutor;
	}

	

}
