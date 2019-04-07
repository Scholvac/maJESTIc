package de.sos.script.support;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.sos.script.IScript;
import de.sos.script.IScriptManager;
import de.sos.script.IScriptSource;
import de.sos.script.ScriptSource.StringSource;
import de.sos.script.ast.ASTFuncCall;
import de.sos.script.ast.ASTNode;
import de.sos.script.ast.CompilationUnit;
import de.sos.script.ast.util.Scope;
import de.sos.script.support.completions.ICompletion;

public class ScriptCompletionProvider {

	private static final Logger 			LOG = LoggerFactory.getLogger(CompilationUnit.class);

	private static Comparator comparator = new Comparator() {
		@Override
		public int compare(Object o1, Object o2) {
			String s1 = o1 instanceof ICompletion ? ((ICompletion)o1).getFeatureString() : o1.toString();
			String s2 = o2 instanceof ICompletion ? ((ICompletion)o2).getFeatureString() : o2.toString();
			return String.CASE_INSENSITIVE_ORDER.compare(s1, s2);
		}
	};
	
	
	public List<ICompletion> getCompletions(final IScript script, final int _pos){
		if (script == null || _pos < 0 || _pos > script.getLength())
			return null;
		//first extract the full statement for which the completion is intended
		final IScriptSource source = script.getSource();
		final String compilable_content = source.getContentAsString();
		return getCompletions(script, _pos, compilable_content.substring(0, _pos));
	}

	public List<ICompletion> getCompletions(final IScript script, final int globalPos, final String _compilable_statement) {
		if (LOG.isTraceEnabled()) LOG.trace("Collect completions for: {}", _compilable_statement);
		
		final IScriptManager mgr = script.getManager();
		String compilable_statement = _compilable_statement;
		int localPos = globalPos;
		int blockStartIdx = mgr.getBlockStartIndex(compilable_statement, globalPos);
		if (blockStartIdx > 0) {
			localPos = globalPos - blockStartIdx;
			compilable_statement = compilable_statement.substring(blockStartIdx);
		}
		
		
		Set<ICompletion> completions = new TreeSet<>(comparator);
		Set<ICompletionFilter> filter = new HashSet<>();
		Scope scope = script.getScopeForPosition(globalPos);//queries shall be done on the complete scope
		if (compilable_statement.trim().isEmpty()) {
			CompletionUtils.completionsFromNamedElement(scope.getAllNamedElements(), completions);
		}else {
			boolean hasDotAtEnd = false;
			final int idxOfDot = compilable_statement.lastIndexOf('.');
			final int length = compilable_statement.length();
			if (idxOfDot > 0 && idxOfDot == length-1) {
				compilable_statement = compilable_statement.substring(0, length-1);
				hasDotAtEnd = true;
			}
			
			
			final String identifier = "CompletionProvider";
			StringSource strSource = new StringSource(identifier, compilable_statement, script.getLanguage());
			CompilationUnit localCU = mgr.createCompilationUnit(strSource);
			if (localCU == null) {
				return null;
			}
			
			//we are only interested in the last statement, e.g. the last AST-node.
			ASTNode last = localCU.getLastChild();
			ASTNode posNode = last.getNodeForIndex(localPos);
			if (posNode == null)
				posNode = last.getNodeForIndex(localPos-1);
			if (posNode == null)
				posNode = last;
			
			
			if (posNode != null) {			
				posNode.getCompletionsAndFilter(compilable_statement, hasDotAtEnd, scope, completions, filter);
				ASTNode pnp = posNode.getParent();
				if (pnp != null && pnp instanceof ASTFuncCall) {//try to add a filter for the current position
					Set<ICompletion> blackHole = new TreeSet<>(comparator); //we just want a filter but no additional completions
					pnp.getCompletionsAndFilter(compilable_statement, false, scope, blackHole, filter);
				}
			}else {
				CompletionUtils.completionsFromNamedElement(scope.getAllNamedElements(), completions);
			}
		}
		
		List<ICompletion> completionList = new ArrayList<>(completions);
		completionList.sort(comparator); 
		if (filter.isEmpty() == false) {
			completionList = filter(filter, completionList);
		}
		
		return completionList;
	}
	
	private static List<ICompletion> filter(Set<ICompletionFilter> filters, List<ICompletion> list) {
		for (ICompletionFilter f : filters) {
			List<ICompletion> tmpList = f.filter(list);
			if (tmpList != null)
				list = tmpList;
		}
		list.sort(comparator);
		return list;
	}
		
	
}
