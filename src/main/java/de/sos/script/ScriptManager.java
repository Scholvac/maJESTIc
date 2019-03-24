package de.sos.script;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.sos.script.ast.CompilationUnit;
import de.sos.script.ast.lang.SystemScope;
import de.sos.script.ast.lang.java.JarManager;
import de.sos.script.impl.lang.js.JSScriptManager;
import de.sos.script.impl.lang.py.PyScriptManager;
import de.sos.script.run.IScriptDebugExecutor;
import de.sos.script.run.IScriptExecuter;
import de.sos.script.run.dbg.BreakPoint;


public class ScriptManager implements IScriptManager {

	
	

	public static ScriptManager init() {
		return init("ScriptManager");
	}
	public static ScriptManager init(final String id){
		ScriptManager smgr = new ScriptManager(id);
	
		//we can register the already known managers
		smgr.registerScriptManager(JSScriptManager.LANG_JAVASCRIPT, new JSScriptManager());
		smgr.registerScriptManager(PyScriptManager.LANG_JYPTHON, new PyScriptManager());
		return smgr;
	}
	
//	public static ScriptManager getService() {
//		return ServiceManager.getByID(SERVICE_SCRIPT_MANAGER);
//	}
//	public static ScriptManager getExtensionPoint() {
//		return (ScriptManager)ExtensionPointManager.getExtensionPoint(EP_SCRIPT_MANAGER);
//	}
	
	
	
	
	private Map<String, IScriptManager>			mManagerMap = new HashMap<>();
	private Logger								LOG;
	
	private ScriptManager(final String identifier) {
		LOG = LoggerFactory.getLogger(identifier);	
	}
	
	
	public IScriptManager registerScriptManager(final String ext, final IScriptManager mgr) {
		if (ext != null && mgr != null) {
			LOG.debug("Register ScriptManager {} for extension {}", mgr, ext);
			return mManagerMap.put(ext, mgr);
		}
		return null;			
	}
	
	
	
	
	public JarManager getJarManager() {
		return JarManager.get();
	}

	
	
	
	public IScriptManager getManagerForExtension(final String lang) {
		if (lang == null || lang.isEmpty())
			return null;
		return mManagerMap.get(lang);		
	}
	
	@Override
	public IScript loadScript(final ScriptSource source) {
		if (source == null)
			return null;
		IScriptManager delegate = getManagerForExtension(source.getLanguage());
		if (delegate != null)
			return delegate.loadScript(source);
		return null;
	}
	

	@Override
	public CompilationUnit createCompilationUnit(final ScriptSource source) {
		if (source == null)
			return null;
		IScriptManager delegate = getManagerForExtension(source.getLanguage());
		if (delegate != null)
			return delegate.createCompilationUnit(source);
		return null;
	}
	

	@Override
	public int getBlockStartIndex(String content, int pos) {
		throw new UnsupportedOperationException("This method is only available in language specific ScriptManager implementations");
	}
	@Override
	public SystemScope getSystemScope() {
		throw new UnsupportedOperationException("This method is only available in language specific ScriptManager implementations");
	}
	@Override
	public String getLanguage() {
		throw new UnsupportedOperationException("This method is only available in language specific ScriptManager implementations");
	}

	@Override
	public IScriptExecuter createExecutor(IScript script) {
		if (script == null)
			return null;
		IScriptManager delegate = getManagerForExtension(script.getLanguage());
		if (delegate != null)
			return delegate.createExecutor(script);
		return null;
	}

	@Override
	public boolean hasDebugSupport(IScript script) {
		if (script == null)
			return false;
		IScriptManager delegate = getManagerForExtension(script.getLanguage());
		if (delegate != null)
			return delegate.hasDebugSupport(script);
		return false;
	}
	@Override
	public boolean hasEditorSupport(final IScript script) {
		if (script == null)
			return false;
		IScriptManager delegate = getManagerForExtension(script.getLanguage());
		if (delegate != null)
			return delegate.hasEditorSupport(script);
		return false;
	}

	@Override
	public IScriptDebugExecutor createDebugExecutor(IScript script) {
		if (script == null)
			return null;
		IScriptManager delegate = getManagerForExtension(script.getLanguage());
		if (delegate != null)
			return delegate.createDebugExecutor(script);
		return null;
	}



	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	//											DEBUG												  //
	////////////////////////////////////////////////////////////////////////////////////////////////////	
	
	private HashMap<String, BreakPoint> 		mBreakPoints = new HashMap<>();
	
	public BreakPoint getBreakPoint(final String src, final int line) {
		final String key = getKey(src, line);
		return mBreakPoints.get(key);
	}

	
	public static final String getBreakPointIdentifier(final String _src) {
		String src = _src.replace("\\", "/");
		if (src.length() > 20)
			return src.substring(src.length()-20);
		return src;
	}
	private String getKey(final String _src, int line) {
		return getBreakPointIdentifier(_src) + ":" + line;
	}

	public BreakPoint addBreakPoint(File file, int line) {
		return addBreakPoint(file.getAbsolutePath(), line);
	}
	public BreakPoint addBreakPoint(final String src, final int line) {
		BreakPoint bp = new BreakPoint(getBreakPointIdentifier(src), line);
		addBreakPoint(bp);
		return bp;
	}
	public void addBreakPoint(final BreakPoint bp) {
		final String key = getKey(bp.sourceIdentifier, bp.lineNumber);
		mBreakPoints.put(key, bp);
	}
	public void removeBreakPoint(final BreakPoint bp) {
		removeBreakPoint(bp.sourceIdentifier, bp.lineNumber);
	}

	private void removeBreakPoint(String src, int line) {
		final String key = getKey(src, line);
		mBreakPoints.remove(key);
	}
	
	public Collection<BreakPoint> getAllBreakPoints() { return mBreakPoints.values(); }
	public void clearAllBreakPoints() { mBreakPoints.clear(); }






}
