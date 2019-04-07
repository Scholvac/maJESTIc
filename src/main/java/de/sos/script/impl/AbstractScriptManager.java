package de.sos.script.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import de.sos.script.IScript;
import de.sos.script.IScriptManager;
import de.sos.script.IScriptSource;
import de.sos.script.ScriptVariable;
import de.sos.script.ast.CompilationUnit;
import de.sos.script.ast.lang.IASTConverter;
import de.sos.script.ast.lang.NativeFunctionDeclaration;

public abstract class AbstractScriptManager implements IScriptManager{

	private static final Logger 			LOG = LoggerFactory.getLogger(AbstractScriptManager.class);
	
	@Override
	public IScript loadScript(final IScriptSource source) {
		try {
			if (source == null) {
				LOG.warn("Provided invalid content or identifier for {}", this);
				return null;
			}
			final Script script = new Script(this, source);
			return script;			
		} catch (Exception e) {
			LOG.error("Failed to read script with excption", e);
			e.printStackTrace();
		}
		return null;
	}
	
	
	@Override
	public CompilationUnit createCompilationUnit(final IScriptSource source) {
		IASTConverter conv = getASTConverter();
		if (conv == null) {
			LOG.error("Failed to get AST-Converter");
			return null;
		}
		return conv.convert(source);
	}


	protected abstract IASTConverter getASTConverter();
	
	
	
	
	
	
	
	
	///classes to be used by subclasses of AbstractScriptManager
	protected static class LogFunction implements Function<Object[], Object> {
		private final Level mLevel;
		public LogFunction(final Level l) {
			mLevel = l;
		}
		@Override
		public Object apply(Object[] t) {
			String msg = (t != null && t.length > 0) ? t[0].toString() : "";
			switch(mLevel) {
			case DEBUG: return debug(msg);
			case ERROR: return error(msg);
			case INFO : return info(msg);
			case TRACE: return trace(msg);
			case WARN : return warn(msg);
			}
			throw new UnsupportedOperationException("Unknown Log Level");
		}
		private Object debug(final String msg) { if (LOG.isDebugEnabled()) {LOG.debug(msg); }return null;}
		private Object error(final String msg) { if (LOG.isErrorEnabled()) {LOG.error(msg); }return null;}
		private Object info (final String msg) { if (LOG.isInfoEnabled()) {LOG.info(msg); }return null;}
		private Object trace(final String msg) { if (LOG.isTraceEnabled()) {LOG.trace(msg); }return null;}
		private Object warn (final String msg) { if (LOG.isWarnEnabled()) {LOG.warn(msg); }return null;}
	}
	
	protected static class IsLogFunction implements Function<Object[], Object> {
		private final Level mLevel;
		public IsLogFunction(final Level l) {
			mLevel = l;
		}
		@Override
		public Object apply(Object[] t) {
			String msg = (t != null && t.length > 0) ? t[0].toString() : "";
			switch(mLevel) {
			case DEBUG: return LOG.isDebugEnabled();
			case ERROR: return LOG.isErrorEnabled();
			case INFO : return LOG.isInfoEnabled();
			case TRACE: return LOG.isTraceEnabled();
			case WARN : return LOG.isWarnEnabled();
			}
			throw new UnsupportedOperationException("Unknown Log Level");
		}
	}
	
	
	public void registerNativeVoidFunction(String name, String desc, ScriptVariable param1, Function<Object[], Object> function) {
		NativeFunctionDeclaration nf = new NativeFunctionDeclaration(name, null, Arrays.asList(param1), function, desc);
		registerNativeFunction(nf);		
	}
	public void registerNativeVoidFunction(String name, String desc, ScriptVariable param1, ScriptVariable param2, Function<Object[], Object> function) {
		NativeFunctionDeclaration nf = new NativeFunctionDeclaration(name, null, Arrays.asList(param1, param2), function, desc);
		registerNativeFunction(nf);		
	}
	public void registerNativeVoidFunction(String name, String desc, ScriptVariable param1, ScriptVariable param2, ScriptVariable param3, Function<Object[], Object> function) {
		NativeFunctionDeclaration nf = new NativeFunctionDeclaration(name, null, Arrays.asList(param1, param2, param3), function, desc);
		registerNativeFunction(nf);		
	}
	public void registerNativeFunction(String name, String desc, Class<?> returnType, ScriptVariable param1, Function<Object[], Object> function) {
		NativeFunctionDeclaration nf = new NativeFunctionDeclaration(name, returnType, Arrays.asList(param1), function, desc);
		registerNativeFunction(nf);		
	}
	public void registerNativeFunction(String name, String desc, Class<?> returnType, ScriptVariable param1, ScriptVariable param2, Function<Object[], Object> function) {
		NativeFunctionDeclaration nf = new NativeFunctionDeclaration(name, returnType, Arrays.asList(param1, param2), function, desc);
		registerNativeFunction(nf);		
	}
	public void registerNativeFunction(String name, String desc, Class<?> returnType, ScriptVariable param1, ScriptVariable param2, ScriptVariable param3, Function<Object[], Object> function) {
		NativeFunctionDeclaration nf = new NativeFunctionDeclaration(name, returnType, Arrays.asList(param1, param2, param3), function, desc);
		registerNativeFunction(nf);		
	}
	public void registerNativeFunction(String name, String desc, Class<?> returnType, Function<Object[], Object> function) {
		NativeFunctionDeclaration nf = new NativeFunctionDeclaration(name, returnType, null, function, desc);
		registerNativeFunction(nf);		
	}

	public void registerNativeFunction(NativeFunctionDeclaration nativeFunction) {
		if (LOG.isDebugEnabled()) LOG.debug("Register native function {}", nativeFunction.getSignature());
		getSystemScope().registerNativeFunction(nativeFunction);
	}
}
