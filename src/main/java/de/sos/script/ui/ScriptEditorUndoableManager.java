package de.sos.script.ui;

import java.util.Timer;

import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.CompoundEdit;

import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.RUndoManager;

import de.sos.script.ui.UIDebugController.DebugEvent;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

public class ScriptEditorUndoableManager extends RUndoManager {

	
	private PublishSubject<CompoundEdit>	mEditSubject = PublishSubject.create();
	
	//the undoable manager may be disabled during loading of an "setText(..)" operation
	private boolean 		mEnabled = true;
	private long 			mIdleTime = 300;

	private Watchdog mWatchdog = null;
	
	public ScriptEditorUndoableManager(RTextArea textArea, int idleTime) {
		super(textArea);
		mIdleTime = idleTime;
		
	}

	
	public boolean isEnabled() { return mEnabled; }
	public void enable(boolean enable) { mEnabled = enable;}
	
	public Disposable subscribeEdit(final Consumer<CompoundEdit> consumer) {
		return mEditSubject.subscribe(consumer);
	}
	
	@Override
	public void undo() {
		endInternalAtomicEdit();
		super.undo();
	}
	
	@Override
	public void redo() {
		endInternalAtomicEdit();
		super.redo();
	}
	
	@Override
	public void undoableEditHappened(UndoableEditEvent e) {
		if (mEnabled) {
			if (mWatchdog == null) {
				beginInternalAtomicEdit();
				mWatchdog = new Watchdog(mIdleTime, new Watchdog.WatchdogObserver() {
					@Override
					public void timeoutOccured(Watchdog w) {
						endInternalAtomicEdit();
						mWatchdog = null;
						mEditSubject.onNext(ScriptEditorUndoableManager.this);
					}				
				});
				mWatchdog.start();
			}else
				mWatchdog.refresh();
			super.undoableEditHappened(e);
		}
	}
}
