package de.sos.script.ui;


import java.io.File;

import de.sos.common.param.ParameterContext;
import de.sos.script.IEntryPoint;
import de.sos.script.IScript;
import de.sos.script.IScriptManager;
import de.sos.script.IScriptSource;
import de.sos.script.ScriptSource.WriteableScriptSource;

public class UIController {
	
	
	public interface IContentEditor {
		String 	getScriptContent();
		boolean changeSource(IScriptSource source, IEntryPoint ep);
	}

	private ParameterContext 	mParameterContext;
	
	private UIDebugController	mDebugController;
	
	private IContentEditor		mContentEditor = null;
	private IScriptSource		mSource;	

	
	public UIController(ParameterContext pc) {
		mParameterContext = pc;
		mDebugController = new UIDebugController(pc, this);
	}
	
	public boolean setSource(IScriptSource source, IEntryPoint ep) { 
		mSource = source;
		if (mContentEditor != null) {
			return mContentEditor.changeSource(source, ep);
		}
		return false;
	}
	
	//----------------------- Setter / Getter ---------------------------//
	
	public IScriptSource getSource() { return mSource; }
	
	public void setContentProvider(IContentEditor cp) { mContentEditor = cp; }
	public IContentEditor getContentProvider() { return mContentEditor; }

	public String getIdentifier() { return mSource != null ? mSource.getIdentifier() : null; }
	
	public UIDebugController getDebugController() { return mDebugController; }
	public ParameterContext getParameterContext() { return mParameterContext; }
	
	
	
	//------------------ Delegate - Setter / Getter ----------------------//
	
	
	public IScript getNewScriptInstance() {
		return IScriptManager.theInstance.loadScript(mSource);
	}

	public boolean canWrite() {
		final IScriptSource src = getSource();
		if (src != null && src instanceof WriteableScriptSource) {
			final String content = getContentProvider() != null ? getContentProvider().getScriptContent() : null;
			if (content != null)
				return ((WriteableScriptSource)src).canWrite(content);
		}
		return false;
	}
	
	public boolean write() {
		final IScriptSource src = getSource();
		if (src != null && src instanceof WriteableScriptSource) {
			final String content = getContentProvider() != null ? getContentProvider().getScriptContent() : null;
			if (content != null)
				return ((WriteableScriptSource)src).writeContent(content);
		}
		return false;
	}
	

	


	

}
