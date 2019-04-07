package de.sos.script.impl.lang.js;


import java.util.function.Function;

import org.slf4j.event.Level;

import de.sos.script.IEntryPoint;
import de.sos.script.IScript;
import de.sos.script.IScriptManager;
import de.sos.script.ScriptManager;
import de.sos.script.ScriptVariable;
import de.sos.script.ast.lang.java.JarManager;
import de.sos.script.ast.lang.js.JSASTConverter;
import de.sos.script.ast.lang.js.JSSystemScope;
import de.sos.script.impl.AbstractScriptManager;
import de.sos.script.run.IScriptDebugExecutor;
import de.sos.script.run.IScriptExecuter;
import de.sos.script.run.lang.js.RhinoDebugExecutor;
import de.sos.script.run.lang.js.RhinoReleaseExecutor;

public class JSScriptManager extends AbstractScriptManager implements IScriptManager {
	
	public static final String LANG_JAVASCRIPT = "JavaScript";
	
	private JSASTConverter			mASTConverter = null;
	private JSSystemScope			sSystemScope = null;
	
	
	public JSScriptManager() {
	}

	@Override
	public String getLanguage() {
		return LANG_JAVASCRIPT;
	}
	
	@Override
	public JSASTConverter getASTConverter() {
		if (mASTConverter == null) {
			mASTConverter = new JSASTConverter(JarManager.get(), getSystemScope());
		}
		return mASTConverter;
	}

	@Override
	public JSSystemScope getSystemScope() {
		if (sSystemScope == null) {
			sSystemScope = new JSSystemScope(IScriptManager.theInstance.getJarManager());
			registerDefaultNativeFunctions();
		}
		return sSystemScope;
	}
		
	
	@Override
	public int getBlockStartIndex(String content, int pos) {
		int idx = Math.max(content.lastIndexOf('{', pos), content.lastIndexOf(';', pos))+1;
		return Math.min(content.length(), idx);
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Override
	public IScriptExecuter createExecutor(IScript script) {
		if (script == null )
			return null;
		return RhinoReleaseExecutor.createExecutor(script);		
	}

	@Override
	public boolean hasDebugSupport(IScript script) {
		return true;
	}
	@Override
	public boolean hasEditorSupport(IScript script) {
		return true;
	}
	
	@Override
	public IScriptDebugExecutor createDebugExecutor(IScript script) {
		if (script == null)
			return null;
		return RhinoDebugExecutor.createExecutor(script);
	}

	
	
	
	
	@Override
	public String createTemplate(IEntryPoint entryPoint) {
		if (entryPoint == null)
			return "";
		StringBuilder sb = new StringBuilder("function ");
		sb.append(entryPoint.getFunctionName());
		sb.append("(");
		String[] argNames = entryPoint.getArgumentNames();
		String sig = "";
		for (String arg : argNames)
			sig += arg + ", ";
		if (sig.length() > 2)
			sig = sig.substring(0, sig.length()-2);//remove ", "
		sb.append(sig);
		sb.append(")\n{\n\t//TODO: insert your script content here \n\t/*return some value */\n}");
		return sb.toString();
	}
	
	
	
	
	
	
	
	
	private void registerDefaultNativeFunctions() {
		ScriptVariable msgParam = new ScriptVariable("message", String.class);
		registerNativeVoidFunction("print", "Prints the given String into the current System.out", msgParam, new Function<Object[], Object>(){
			@Override
			public Object apply(Object[] t) {
				System.out.println(t[0]);
				return null;
			}
		});
		String doc = "/**\n* Log a message at the LEVEL level according to the specified format and argument.\n* @param message the string to be logged\n*/";
		
		registerNativeVoidFunction("log", "Logs a message using a default logger with level INFO", msgParam, new LogFunction(Level.INFO));
		registerNativeVoidFunction("trace", doc.replace("LEVEL", "TRACE"), msgParam, new LogFunction(Level.TRACE));
		registerNativeVoidFunction("debug", doc.replace("LEVEL", "DEBUG"), msgParam, new LogFunction(Level.DEBUG));
		registerNativeVoidFunction("info", doc.replace("LEVEL", "INFO"), msgParam, new LogFunction(Level.INFO));
		registerNativeVoidFunction("warn", doc.replace("LEVEL", "WARN"), msgParam, new LogFunction(Level.WARN));
		registerNativeVoidFunction("error", doc.replace("LEVEL", "ERROR"), msgParam, new LogFunction(Level.ERROR));
		
		 doc = "/**\n* Is the logger instance enabled for the LEVEL level?\n\n @return True if this Logger is enabled for the LEVEL level, false otherwise.\n */";
		 registerNativeFunction("isTraceEnabled", doc.replace("LEVEL", "TRACE"), boolean.class, new IsLogFunction(Level.TRACE));
		 registerNativeFunction("isDebugEnabled", doc.replace("LEVEL", "DEBUG"), boolean.class, new IsLogFunction(Level.DEBUG));
		 registerNativeFunction("isInfoEnabled" , doc.replace("LEVEL", "INFO") , boolean.class, new IsLogFunction(Level.INFO ));
		 registerNativeFunction("isWarnEnabled" , doc.replace("LEVEL", "WARN") , boolean.class, new IsLogFunction(Level.WARN ));
		 registerNativeFunction("isErrorEnabled", doc.replace("LEVEL", "ERROR"), boolean.class, new IsLogFunction(Level.ERROR));
		
	}




}
