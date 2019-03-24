package de.sos.script.ui;


import java.io.File;

import de.sos.common.param.ParameterContext;
import de.sos.script.IScript;
import de.sos.script.IScriptManager;
import de.sos.script.ScriptSource;

public class UIController {
	
	
	public interface IContentEditor {
		String 	getScriptContent();
		boolean changeSource(ScriptSource source);
	}

	private ParameterContext 	mParameterContext;
	
	private UIDebugController	mDebugController;
	
	private IContentEditor		mContentEditor = null;
	private ScriptSource		mSource;	

	
	public UIController(ParameterContext pc) {
		mParameterContext = pc;
		mDebugController = new UIDebugController(pc, this);
	}
	
	public boolean setSource(ScriptSource source) { 
		mSource = source;
		if (mContentEditor != null) {
			return mContentEditor.changeSource(source);
		}
		return false;
	}
	
	//----------------------- Setter / Getter ---------------------------//
	
	public ScriptSource getSource() { return mSource; }
	
	public void setContentProvider(IContentEditor cp) { mContentEditor = cp; }
	public IContentEditor getContentProvider() { return mContentEditor; }

	public String getIdentifier() { return mSource != null ? mSource.getIdentifier() : null; }
	
	public UIDebugController getDebugController() { return mDebugController; }
	public ParameterContext getParameterContext() { return mParameterContext; }
	
	
	
	//------------------ Delegate - Setter / Getter ----------------------//
	
	
	public IScript getNewScriptInstance() {
		return IScriptManager.theInstance.loadScript(mSource);
	}
	

	


	

}
