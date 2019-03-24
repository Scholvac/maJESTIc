package de.sos.script.ui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import de.sos.script.run.dbg.DebuggerCallback.NextAction;
import de.sos.script.ui.UIDebugController.DebugEventType;



public class DebugActions extends UIActions {

	private UIController 				mController;
	
	private RunAction					mRunAction; 
	private DebugAction					mDebugAction;
	private StepIntoAction				mStepIntoAction;
	private StepOutAction				mStepOutAction;
	private StepOverAction				mStepOverAction;
	private StopAction					mStopAction;
		
	
	public DebugActions(UIController controller) {
		mController = controller;
	}
	
	public RunAction 		getRunAction() 		{ if (mRunAction == null) mRunAction = new RunAction(mController); return mRunAction; }
	public DebugAction 		getDebugAction() 	{ if (mDebugAction == null) mDebugAction = new DebugAction(mController); return mDebugAction; }
	public StepIntoAction 	getStepIntoAction() { if (mStepIntoAction == null) mStepIntoAction = new StepIntoAction(mController); return mStepIntoAction; }
	public StepOutAction 	getStepOutAction() 	{ if (mStepOutAction == null) mStepOutAction = new StepOutAction(mController); return mStepOutAction; }
	public StepOverAction 	getStepOverAction()	{ if (mStepOverAction == null) mStepOverAction = new StepOverAction(mController); return mStepOverAction; }
	public StopAction 		getStopAction() 	{ if (mStopAction == null) mStopAction = new StopAction(mController); return mStopAction; }
	
	
	
	public static abstract class AbstractDebugAction extends AbstractAction {
		protected UIDebugController mDbgC;

		protected AbstractDebugAction(String name, ImageIcon icon, UIController controller) {
			super(name, icon);
			mDbgC = controller.getDebugController();
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			performAction(e);
		}

		protected abstract void performAction(ActionEvent e);	
	}
	
	
	public class RunAction extends AbstractDebugAction {
		private RunAction(UIController dbg) {
			super("Run", getIcon("icons/start.png"), dbg);
			dbg.getDebugController().subscribeDebugEvents(de -> {
				setEnabled(de.type == DebugEventType.FINISH);
			});
		}

		@Override
		protected void performAction(ActionEvent e) {
			mDbgC.execute();
		}

	
	}
	public static class StopAction extends AbstractDebugAction {
		private StopAction(UIController dbg) {
			super("Stop", getIcon("icons/stop.png"), dbg);
			setEnabled(false); //for now
		}

		@Override
		protected void performAction(ActionEvent e) {
			System.out.println("Stop");
		}		
	}
	
	public static class DebugAction extends AbstractDebugAction {
		private DebugAction( UIController dbg) {
			super("Debug", getIcon("icons/debug.png"), dbg);
			dbg.getDebugController().subscribeDebugEvents(de -> {
				setEnabled(de.type == DebugEventType.FINISH);
			});
		}

		@Override
		protected void performAction(ActionEvent e) {
			mDbgC.debug();
		}		
	}
	
	public static class StepOverAction extends AbstractDebugAction {
		private StepOverAction(UIController dbg) {
			super("Step Over", getIcon("icons/debug_step_over.png"), dbg);
			setEnabled(false);
			dbg.getDebugController().subscribeDebugEvents(de -> {
				setEnabled(de.type == DebugEventType.INTERRUPT_Start);
			});
		}

		@Override
		protected void performAction(ActionEvent e) {
			mDbgC.next(NextAction.STEP_OVER);
		}		
	}
	public static class StepIntoAction extends AbstractDebugAction {
		private StepIntoAction(UIController dbg) {
			super("Step Into", getIcon("icons/debug_step_into.png"), dbg);
			setEnabled(false);
			dbg.getDebugController().subscribeDebugEvents(de -> {
				setEnabled(de.type == DebugEventType.INTERRUPT_Start);
			});
		}

		@Override
		protected void performAction(ActionEvent e) {
			mDbgC.next(NextAction.STEP_INTO);
		}		
	}
	
	public static class StepOutAction extends AbstractDebugAction {
		private StepOutAction(UIController dbg) {
			super("Step Out", getIcon("icons/debug_step_out.png"), dbg);
			setEnabled(false);
			dbg.getDebugController().subscribeDebugEvents(de -> {
				setEnabled(de.type == DebugEventType.INTERRUPT_Start);
			});
		}

		@Override
		protected void performAction(ActionEvent e) {
			mDbgC.next(NextAction.STEP_OUT);
		}		
	}
}
