package de.sos.script.ast.lang;

import de.sos.script.IScriptSource;
import de.sos.script.ast.CompilationUnit;

public interface IASTConverter {

	public CompilationUnit convert(final IScriptSource script, int start, int end);
	
	default CompilationUnit convert(final IScriptSource source) {
		return convert(source, 0, -1);
	}

}
