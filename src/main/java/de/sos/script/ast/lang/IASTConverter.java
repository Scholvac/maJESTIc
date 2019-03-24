package de.sos.script.ast.lang;

import de.sos.script.ScriptSource;
import de.sos.script.ast.CompilationUnit;

public interface IASTConverter {

	public CompilationUnit convert(final ScriptSource script, int start, int end);
	
	default CompilationUnit convert(final ScriptSource source) {
		return convert(source, 0, -1);
	}

}
