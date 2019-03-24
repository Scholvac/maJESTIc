package de.sos.script;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import de.sos.script.ast.CompilationUnit;
import de.sos.script.ast.lang.SystemScope;
import de.sos.script.run.IScriptDebugExecutor;
import de.sos.script.run.IScriptExecuter;

public interface IScriptManager {

	static ScriptManager		theInstance = ScriptManager.init();
	
	String getLanguage();
	IScript loadScript(final ScriptSource source);
	
	
	SystemScope getSystemScope();
	
	boolean hasEditorSupport(final IScript script);
	CompilationUnit createCompilationUnit(final ScriptSource source);
	int getBlockStartIndex(final String content, final int pos);
	
	
	boolean hasDebugSupport(final IScript script);
	IScriptExecuter createExecutor(final IScript script);	
	IScriptDebugExecutor createDebugExecutor(final IScript script);
	
	
	
	/** 
	 * Loads a script from a file, using a ScriptSource.FileSource
	 * @param file file to be loaded
	 * @return the loaded script or null
	 */
	static IScript loadScript(File file) {
		if (file == null) 
			return null;
		if (file.exists() == false || file.canRead() == false)
			return null;
		return theInstance.loadScript(new ScriptSource.FileSource(file));
	}


		
}
