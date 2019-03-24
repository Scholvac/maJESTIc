package de.sos.script.ui;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.thoughtworks.xstream.XStream;

import de.sos.common.param.ParameterContext;
import de.sos.script.IScript;
import de.sos.script.IScriptManager;
import de.sos.script.ScriptManager;
import de.sos.script.run.dbg.BreakPoint;
import de.sos.script.run.dbg.DebugContext;
import de.sos.script.run.dbg.DebuggerCallback;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

public class UIDebugController implements DebuggerCallback {
	
	public enum DebugEventType {
		START, FINISH, INTERRUPT_Start, INTERRUPT_End
	}
	public static class DebugEvent {
		public final DebugContext			context;
		public final BreakPoint				breakpoint;
		public final DebugEventType			type;
		
		public DebugEvent(final DebugContext ctx, final BreakPoint bp, DebugEventType t) {
			context = ctx; breakpoint = bp;
			type = t;
		}
	}

	public static final String		BREAK_POINTS 					= "BREAK_POINTS";
	
	public static final String	PARAM_BREAKPOINT_FILE				= "P_Breakpoint_File";
	
	private File										mBreakPointFile;
	private UIController 								mController;
	
	private PublishSubject<PropertyChangeEvent>			mBreakPointSubject = PublishSubject.create();
	private PublishSubject<DebugEvent>					mDebugSubject = PublishSubject.create();

	
	
	
	public UIDebugController(final ParameterContext pc, UIController controller) {
		mBreakPointFile = pc.getValue(PARAM_BREAKPOINT_FILE, new File("BreakPoints.xml"));
		mController = controller;
		loadBreakPointsFromFile();
	}
	
	public Disposable subscribeBreakPointChanges(Consumer<PropertyChangeEvent> consumer) {
		return mBreakPointSubject.subscribe(consumer);
	}
	public Disposable subscribeDebugEvents(Consumer<DebugEvent> consumer) {
		return mDebugSubject.subscribe(consumer);
	}
	
	public void toggleBreakPoint(final String id, final int line) {
		BreakPoint bp = IScriptManager.theInstance.getBreakPoint(id, line);
		PropertyChangeEvent pce = null;
		if (bp != null) {
			IScriptManager.theInstance.removeBreakPoint(bp);
			pce = new PropertyChangeEvent(this, BREAK_POINTS, bp, null);
		}
		else {
			IScriptManager.theInstance.addBreakPoint(id, line);
			pce = new PropertyChangeEvent(this, BREAK_POINTS, null, bp);
		}
		mBreakPointSubject.onNext(pce);
		//write the file after each change - this should not be that expensive, however if it becomes a bottleneck it may be outsourced into a thread
		saveBreakPointsToFile();
	}

	

	
	//--------------------------------	Debug Actions ---------------------------------//
	private Thread					mExecThread; //only used to detect if we are currently running or not
	
	public void execute() {
		if (mExecThread != null)
			return ; //only one active session
		mExecThread = new Thread() {
			@Override
			public void run() {
				try {
					IScript script = mController.getNewScriptInstance();					
					script.execute();
				}catch(Exception | Error e) {
					e.printStackTrace();
				}finally {
					mExecThread = null;
				}
			}
		};
		mExecThread.start();
	}
	


	public void debug() {
		if (mExecThread != null)
			return ; //only one active session
		mExecThread = new Thread() {
			@Override
			public void run() {
				try {
					IScript script = mController.getNewScriptInstance();
					DebugEvent evt = new DebugEvent(null, null, DebugEventType.START);					
					mDebugSubject.onNext(evt);
					
					script.debug(UIDebugController.this);
					
					evt = new DebugEvent(null, null, DebugEventType.FINISH);					
					mDebugSubject.onNext(evt);
				}catch(Exception | Error e) {
					e.printStackTrace();
				}finally {
					mExecThread = null;
				}
			}
		};
		mExecThread.start();
	}
	
	
	public void next(NextAction action) {
		try {
			if (!mNextActionQueue.isEmpty())
				return ;
			mNextActionQueue.put(action);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	//--------------------------- DebuggerCallback --------------------------------------//
	
	private BlockingQueue<NextAction>	mNextActionQueue = new ArrayBlockingQueue<>(1);
	
	@Override
	public NextAction interrupt(DebugContext context, BreakPoint bp) {
		mNextActionQueue.clear();
		
		DebugEvent evt = new DebugEvent(context, bp, DebugEventType.INTERRUPT_Start);		
		mDebugSubject.onNext(evt);
		
		//wait until we got some event
		try {
			NextAction na = mNextActionQueue.take();
			return na;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return NextAction.STOP;
		}finally {
			evt = new DebugEvent(context, bp, DebugEventType.INTERRUPT_End);		
			mDebugSubject.onNext(evt);
		}
	}
	
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	//									Utilities												//	
	//////////////////////////////////////////////////////////////////////////////////////////////
	
	private void saveBreakPointsToFile() {
		if (mBreakPointFile != null) {
			try {
				Collection<BreakPoint> all = IScriptManager.theInstance.getAllBreakPoints();
				XStream xs = new XStream();
				xs.toXML(all, new FileOutputStream(new File("Breakpoints.xml")));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void loadBreakPointsFromFile() {
		if (mBreakPointFile != null && mBreakPointFile.exists()) {
			try {
				XStream xs = new XStream();
				Collection<BreakPoint> bps = (Collection<BreakPoint>) xs.fromXML(mBreakPointFile);
				for (BreakPoint bp : bps)
					IScriptManager.theInstance.addBreakPoint(bp);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}








}
