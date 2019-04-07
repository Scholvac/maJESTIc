package de.sos.script.run.dbg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

import de.sos.script.IScriptManager;
import de.sos.script.ScriptManager;
import de.sos.script.ScriptVariable;
import de.sos.script.run.dbg.DebuggerCallback.NextAction;

public class DebugContext {
	
	public interface IDbgFrameCallback {
		String			getSourceIdentifier();
		int				getLine();
		
		Collection<ScriptVariable>	getAccessableVariables();
	}
	
	private final DebuggerCallback		mCallback;
	private int							mMaxStackSize = 100;
	private Stack<IDbgFrameCallback> 	mStackTrace = new Stack<>();
	
	private NextAction					mNextAction = NextAction.CONTINUE;
	private int							mLastCommandAtStackLevel = 0;
		
	public DebugContext(final DebuggerCallback callback) {
		mCallback = callback;
	}
	
	
	
	
	
	public List<ScriptVariable> getAccessableVariables() {
		Collection<ScriptVariable> collection = top().getAccessableVariables();
		ArrayList<ScriptVariable> out = new ArrayList<>(collection);
		out.sort(new Comparator<ScriptVariable>() {
			@Override
			public int compare(ScriptVariable o1, ScriptVariable o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		return out;
	}
	
	
	
	
	
	
	
	
	
	
	public void push(IDbgFrameCallback dbgFrame) {
		assert(dbgFrame != null);
		mStackTrace.push(dbgFrame);
		if (mStackTrace.size() > mMaxStackSize)
			throw new StackOverflowError("Maximum stack size exceeded");
	}
	
	public IDbgFrameCallback pop() {
		assert(mStackTrace.empty() == false);
		return mStackTrace.pop();
	}

	public IDbgFrameCallback top() {
		return mStackTrace.peek();
	}

	public void notifyNextLine() {
		if (mCallback == null)
			return ;
		
		final IDbgFrameCallback top = top();
		final String srcIdentifier = top.getSourceIdentifier();
		final int line = top.getLine();
		final BreakPoint bp = getBreakPoint(top, srcIdentifier, line);
		if (bp != null) {
			mNextAction = mCallback.interrupt(this, bp);
		}
		
		if (mNextAction == NextAction.STEP_OVER)
			mLastCommandAtStackLevel = mStackTrace.size();
	}

	private static class DbgBreakPoint extends BreakPoint {
		public final NextAction reason;
		public DbgBreakPoint(String srcId, int line, NextAction action) {
			super(srcId, line);
			reason = action;
		}
		
	}

	private BreakPoint getBreakPoint(IDbgFrameCallback top, String srcIdentifier, int line) {
		//if there is a breakpoint - stop without taking the next action into account
		BreakPoint bp = IScriptManager.theInstance.getBreakPoint(srcIdentifier, line);
		if (bp != null)
			return bp;
		switch(mNextAction) {
		case CONTINUE : return null;
		case STEP_OUT : 
		case STEP_OVER:
		{
			if (mStackTrace.size() <= mLastCommandAtStackLevel) {
				System.out.println("Create STEP OUT BreakPoint");
				bp = new DbgBreakPoint(ScriptManager.normalizeBreakPointIdentifier(srcIdentifier), line, mNextAction);
			}else
				mNextAction = NextAction.STEP_OUT;
			break;
		}
		case STEP_INTO:
			bp = new DbgBreakPoint(ScriptManager.normalizeBreakPointIdentifier(srcIdentifier), line, mNextAction);
			break;
		case STOP:
			System.out.println("Not yet implemented");
		}
		
		return bp;
	}


















}
