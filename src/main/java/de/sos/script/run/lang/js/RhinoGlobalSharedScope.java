package de.sos.script.run.lang.js;

import java.util.Collection;
import java.util.function.Function;

import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import de.sos.script.ast.lang.NativeFunctionDeclaration;
import de.sos.script.ast.lang.SystemScope;

public class RhinoGlobalSharedScope {

	
	static class RhinoNativeFunctionDelegate extends BaseFunction {

		private final NativeFunctionDeclaration			mNativeFunction;
		private final Function<Object[], Object>		mCallable;
		
		public RhinoNativeFunctionDelegate(final NativeFunctionDeclaration nf) {
			mNativeFunction = nf;
			mCallable = nf.getCallable();
			if (mCallable == null)
				throw new NullPointerException("Missing callable for: " + nf);
		}
		
		@Override
		public int getArity() {
			return 1;//????? //super.getArity();
		}
		
		@Override
		public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
			return mCallable.apply(args);
		}
		
		@Override
		public String getFunctionName() {
			return mNativeFunction.getName();
		}
		
		
		
	}
	
	public static ScriptableObject getSystemScope(Context cx, SystemScope scopeTemplate) {
		ScriptableObject systemScope = cx.initStandardObjects(null, true);
		
		Collection<NativeFunctionDeclaration> nfdecls = scopeTemplate.getNativeFunctions();
		for (NativeFunctionDeclaration nfd : nfdecls) {
			final String fn = nfd.getName();
			systemScope.defineProperty(fn, new RhinoNativeFunctionDelegate(nfd), ScriptableObject.DONTENUM);
		}
		
		
		return systemScope;
	}

	
}
