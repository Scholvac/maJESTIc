package de.sos.script.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import de.sos.script.ast.INamedElement;
import de.sos.script.ast.IType;
import de.sos.script.ast.TypeManager;
import de.sos.script.support.completions.ICompletion;

public class CompletionUtils {
	
	private static Comparator comparator = new Comparator() {
		@Override
		public int compare(Object o1, Object o2) {
			String s1 = o1 instanceof ICompletion ? ((ICompletion)o1).getFeatureString() : o1.toString();
			String s2 = o2 instanceof ICompletion ? ((ICompletion)o2).getFeatureString() : o2.toString();
			return String.CASE_INSENSITIVE_ORDER.compare(s1, s2);
		}
	};
	
	public static class NameFilter implements ICompletionFilter {
		private final String 	alreadyInserted;
		public NameFilter(final String n) { alreadyInserted = n; }
		
		@Override
		public List<ICompletion> filter(List<ICompletion> completions) {
			String txt = alreadyInserted;
			if (txt.endsWith("."))
				txt = txt.substring(0, txt.length()-1);
			
			int start = Collections.binarySearch(completions, txt, comparator);
			if (start < 0) {
				start = -(start + 1);
			}
			else {
				// There might be multiple entries with the same input text.
				while (start > 0 && comparator.compare(completions.get(start - 1), txt) == 0) {
					start--;
				}
			}

			int end = Collections.binarySearch(completions, txt + '{', comparator);
			end = -(end + 1);
			return completions.subList(start, end);
		}
	}
	
	public static class MultipleTypeFilter implements ICompletionFilter {
		private final Set<IType> 	possibleTypes;
		public MultipleTypeFilter(final Set<IType> pt) {
			possibleTypes = pt;
		}
		@Override
		public List<ICompletion> filter(List<ICompletion> completions) {
			List<ICompletion> out = new ArrayList<>();
			TypeManager tmgr = TypeManager.get();
			for (ICompletion comp : completions) {
				String typeStr = comp.getTypeAsString();
				if (typeStr != null) {
					IType t = tmgr.getType(typeStr);
					if (t != null) {
						if (isFiltered(t))
							continue;
					}
				}
				out.add(comp); 
			}
			return out;
		}
		private boolean isFiltered(IType t) {
			for (IType pt : possibleTypes) {
				try {
					if (t.inherits(pt)) {
						return false;
					}
				}catch(Exception e) {
					return false;
				}
			}
			return true;
		}
	}

	public static void completionsFromNamedElement(Collection<INamedElement> namedElements, Set<ICompletion> completions) {
		for (INamedElement ne : namedElements) {			
			ne.insertCompletions(completions);
		}
	}

	public static void addNameFilter(String name, Set<ICompletionFilter> filter) {
		filter.add(new NameFilter(name));		
	}

	public static void addMultipleTypeFilter(final Set<IType> possibleTypes, Set<ICompletionFilter> filter) {
		filter.add(new MultipleTypeFilter(possibleTypes));
	}

}
