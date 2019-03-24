package de.sos.script.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import de.sos.script.ast.util.Scope;
import de.sos.script.support.ICompletionFilter;
import de.sos.script.support.completions.ICompletion;


public abstract class ASTNode {
		
	private static final int MAX_INTERFERENCE_DEPTH = 10;
	
	private ASTNode		 			mParent;
	private final int				mStart;
	private final int				mEnd;
	
	private List<ASTNode>			mChildren;
	
	public ASTNode(final ASTNode parent, final int start, final int end) {
		mParent = parent;
		mStart = start; mEnd = end;
		if (mParent != null) {
			mParent.addChild(this);
		}
	}
	
	public ASTNode getParent() { return mParent; }
	public int getStart() { return mStart; }
	public int getEnd() { return mEnd; }
	
	
	private void addChild(final ASTNode node) {
		if (mChildren == null)
			mChildren = new ArrayList<>();
		mChildren.add(node);
	}

	/** This method add the node to the child list AND set's its parent. 
	 * @note usually this is done the other way around
	 * @param node
	 */
	protected void _addChild(ASTNode node) {
		node.mParent = this;
		addChild(node);
	}
	
	
	public List<ASTNode> getChildren() {
		if (mChildren != null)
			return Collections.unmodifiableList(mChildren);
		return null;
	}
	
	public ASTNode getFirstChild() {
		if (mChildren == null || mChildren.isEmpty()) return null;
		return mChildren.get(0);
	}
	
	public ASTNode getLastChild() {
		if (mChildren == null || mChildren.isEmpty()) return null;
		int s = mChildren.size();
		return mChildren.get(s-1);
	}
	
//	public IScopeProvider getScope() {
//		if (this instanceof IScopeProvider)
//			return (IScopeProvider)this;
//		if (mParent != null)
//			return mParent.getScope();
//		return null;
//	}
	
	public ASTNode getNodeForIndex(int idx) {
		if (idx < mStart || idx > mEnd)
			return null;
		if (mChildren != null) {
			for (int i = 0; i < mChildren.size(); i++) {
				ASTNode r = mChildren.get(i).getNodeForIndex(idx);
				if (r != null)
					return r;
			}
		}
		return this;
	}
	
	public Scope getScopeForIndex(int idx) {
		ASTNode n = getNodeForIndex(idx);
		if (n != null) {
			while(n != null) {
				if (n instanceof IScopeProvider)
					return ((IScopeProvider) n).getScope();
				n = n.getParent();
			}
		}
		return null;
	}
	
	public Scope getNodeScope() {
		if (getStart() >= 0)
			return getScopeForIndex(getStart());
		return null;
	}

	

	
//	//@note this method may only be called with elements from within this scope
//	private IType _getTypeOf(final INamedElement ne) {
//		if (ne instanceof IType)
//			return (IType)ne;
//		if (ne instanceof ITypeResolver)
//			return ((ITypeResolver)ne).getType();		
//		return null;
//	}
	
	
	
	
	
	
	protected String printInterval(String name) {
		return name + "[" + mStart + "; " + mEnd + "]";
	}
	public String debugPrint() {
		StringBuilder sb = new StringBuilder();
		sb.append(toString() + "\n");
		if (mChildren != null) {
			for (int i = 0; i < mChildren.size(); i++) {
				mChildren.get(i).debugPrint("\t", sb);
			}
		}
		return sb.toString();
	}

	protected void debugPrint(String intend, StringBuilder sb) {
		sb.append(intend + toString() + "\n");
		if (mChildren != null) {
			for (int i = 0; i < mChildren.size(); i++) {
				mChildren.get(i).debugPrint(intend+"\t", sb);
			}
		}
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////
	//								ScopeProvider - Methods											//
	//////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////
	//							ITypeResolver - Methods												//
	//////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	
//	public INamedElement getNamedElement(String name) {
//		Map<String, INamedElement> map = new HashMap<>();
//		collectNamedElements(map);
//		return map.get(name);
//	}
//	
//	public Collection<INamedElement> getAllNamedElements() {
//		Map<String, INamedElement> map = new HashMap<>();
//		collectNamedElements(map);
//		return map.values();
//	}
	
//	protected void collectNamedElements(Map<String, INamedElement> map) {
//		if (mChildren != null && mChildren.isEmpty() == false) {
//			for (int i = 0; i < mChildren.size(); i++) {
//				ASTNode n = mChildren.get(i);
//				if (n instanceof INamedElement) {
//					INamedElement ne = (INamedElement)n;
//					String name = ne.getName();
//					if (name != null)
//						map.put(name, ne);
//				}
//			}
//		}
//		IScope s = getScope(); //the method is defined in scope
//		if (s != null && s == this) {
//			IScope ps = s.getParentScope();
//			if (ps != null) {
//				if (ps instanceof ASTNode) { //just for perf-reasons
//					((ASTNode)ps).collectNamedElements(map);
//				}else {
//					Collection<INamedElement> collection = ps.getAllNamedElements();
//					for (INamedElement ne : collection)
//						if (map.containsKey(ne.getName()) == false)
//							map.put(ne.getName(), ne);
//				}
//			}
//		}
//	}
//	
//	public final IType getType() {
//		Set<ASTNode> visited = new HashSet<>();
//		return _saveGetType(visited);
//	}
//	
//	protected abstract IType _saveGetType(final Set<ASTNode> visited);
//
////	public IType getTypeOf(ASTNode node) {
////		return _getTypeOf(node, 0);
////	}
//	private IType _getTypeOf(ASTNode node, int depth) {
//		if (depth > MAX_INTERFERENCE_DEPTH)
//			return null;
//		if (node instanceof IType)
//			return (IType)node;
//		else if (node instanceof ASTFuncCall) {
//			ASTFuncCall fc = (ASTFuncCall)node;
//			ASTNode callOn = fc.getSourceNode();
//			if (callOn == null) { //its a function that shall be defined in this node, e.g same as a INamedElement
//				return _getTypeOf(getNamedElement(fc.getName()));
//			}else {
//				IType srcType = _getTypeOf(callOn, depth+1);
//				if (srcType != null) {
//					ArrayList<IType> paramTypes = new ArrayList<>();
//					if (fc.getArguments() != null) {
//						for (ASTNode pn : fc.getArguments()) {
//							paramTypes.add(_getTypeOf(pn, depth+1));
//						}
//					}
//					ITypeResolver tr = srcType.getTypeResolver(fc.getName(), paramTypes.toArray(new IType[paramTypes.size()]));
//					if (tr != null)
//						return tr.getType();
//				}
//			}
//			System.err.println();
//		}else if (node instanceof ASTPropAccess) {
//			ASTPropAccess pa = (ASTPropAccess)node;
//			IType srcType = _getTypeOf(pa.getSourceNode(), depth+1);
//			if (srcType != null) {
//				ITypeResolver propType = srcType.getTypeResolver(pa.getName(), null);
//				if (propType != null)
//					return propType.getType();
//			}
//		}else if (node instanceof ASTAssign){
//			ASTAssign as = (ASTAssign)node;
//			ASTNode child = as.getChildren().get(0);
//			return _getTypeOf(child, depth+1);
//		}else if (node instanceof ASTNamedNode) {
//			String n = ((ASTNamedNode) node).getName();
//			INamedElement ne = getNamedElement(n);
//			if (ne != null && ne != node) {
//				return _getTypeOf(ne);
//			}
//		}
//		return null;
//	}
	
	
	
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	//								Completions													   //
	/////////////////////////////////////////////////////////////////////////////////////////////////
	
	abstract public List<Class<? extends ASTNode>> getAllowedChildren();

	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	
	
	
	
	
	public static abstract class ASTNamedNode extends ASTNode {

		private final String		mName;
		private String				mDocumentation;
		
		public ASTNamedNode(final ASTNode parent, final String name, final int start, final int end) {
			super(parent, start, end);
			mName = name;
		}
		
		public void setDocumentation(String documentation) {
			mDocumentation = documentation;
		}
		public String getName() { return mName; }
		public String getDocumentation() { return mDocumentation; }
		
	}

	/** Returns an unfiltered set of completions and optional a set of filter
	 * @param statement the compiled statement
	 * @param hasDotAtEnd true if there was a dot at the end of the statement (which has been removed to allow compilation)
	 * @param scope the scope to search for types and names
	 * @param completions target set to store the completions inside
	 * @param filter target set to store the filter inside. 
	 **/
	public void getCompletionsAndFilter(final String statement, final boolean hasDotAtEnd, final Scope scope, final Set<ICompletion> completions, final Set<ICompletionFilter> filter) {
		System.out.println("Not implemented for class: " + getClass());
		
		throw new UnsupportedOperationException();
	}

	public IType getType(final Scope scope) {
		throw new UnsupportedOperationException("Not implemented");
	}












	

}
