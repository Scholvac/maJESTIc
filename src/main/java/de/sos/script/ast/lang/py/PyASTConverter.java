package de.sos.script.ast.lang.py;

import org.python.antlr.AnalyzingParser;
import org.python.antlr.runtime.ANTLRStringStream;

import de.sos.script.IScriptSource;
import de.sos.script.ast.CompilationUnit;
import de.sos.script.ast.lang.IASTConverter;

public class PyASTConverter implements IASTConverter {

	@Override
	public CompilationUnit convert(final IScriptSource source, int start, int end) {
		final String identifier = source.getIdentifier();
		final String content = source.getContentAsString();
		if (end < 0) end = content.length();
		
		AnalyzingParser bp = new AnalyzingParser(new ANTLRStringStream(content), identifier, "UTF-8");
		CompilationUnit cu = new CompilationUnit(identifier, start, end);
		
		return cu;
	}

}
