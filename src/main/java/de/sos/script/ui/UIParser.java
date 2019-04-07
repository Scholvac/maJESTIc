package de.sos.script.ui;

import java.io.ByteArrayInputStream;

import javax.swing.text.Element;
import javax.swing.text.JTextComponent;

import org.fife.io.DocumentReader;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.parser.AbstractParser;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParseResult;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParserNotice;
import org.fife.ui.rsyntaxtextarea.parser.ParseResult;
import org.fife.ui.rsyntaxtextarea.parser.ParserNotice.Level;

import com.google.common.io.CharStreams;

import de.sos.script.IScript;
import de.sos.script.IScriptManager;
import de.sos.script.ParserProblem;
import de.sos.script.ScriptSource;
import de.sos.script.ScriptSource.StringSource;
import de.sos.script.ast.CompilationUnit;

public class UIParser extends AbstractParser {

	
	
	
	private IScriptManager 			mScriptManager;
	private StringSource			mScriptSource;
	private DefaultParseResult		mParseResult;
	private CompilationUnit 		mCompilationUnit;
	
	
	

	public void setScriptManager(IScriptManager mgr) {
		mScriptManager 	= mgr;
		mParseResult 	= new DefaultParseResult(this);
		mScriptSource 	= new StringSource("UIParser", "", mgr.getLanguage());
	}
	
	public IScript reparseScript(JTextComponent comp) {
		return reparseScript(comp.getText());
	}
	
	public synchronized IScript reparseScript(String content) {
		mScriptSource.writeContent(content);
		return mScriptManager.loadScript(mScriptSource);
	}
	
	@Override
	public ParseResult parse(RSyntaxDocument doc, String style) {
		if (mScriptManager == null) {
			if (mParseResult == null)
				mParseResult = new DefaultParseResult(this);
			mParseResult.clearNotices();
			return mParseResult;
		}
		Element root = doc.getDefaultRootElement();
		int lineCount = root.getElementCount();
		DocumentReader r = new DocumentReader(doc);
				
		//reset the parse-result
		mParseResult.clearNotices();
		try {
			long parse_start = System.currentTimeMillis();
			
			String content = CharStreams.toString(r);
			IScript script = reparseScript(content);
			CompilationUnit cu = script.getCompilationUnit();
			convertParserErrors(cu, mParseResult);			
			
			long parse_end = System.currentTimeMillis();
			
			mParseResult.setParsedLines(0, lineCount - 1);
			mParseResult.setParseTime(parse_end-parse_start);
			mCompilationUnit = cu;			
		}catch(Exception e) {
			mParseResult.setError(e);
		}
		
		return mParseResult;
	}

	private void convertParserErrors(final CompilationUnit cu, DefaultParseResult res) {
		for (ParserProblem pp : cu.getProblems()) {
			DefaultParserNotice dpn = new DefaultParserNotice(this, pp.getMessage(), pp.getLine());
			switch(pp.getLevel()) {
			case ERROR : dpn.setLevel(Level.ERROR); break;
			case INFO : dpn.setLevel(Level.INFO); break;
			case WARN : dpn.setLevel(Level.WARNING); break;
			}
			res.addNotice(dpn);
		}
	}

	public CompilationUnit getCompilationUnit() {
		return mCompilationUnit;
	}







}
