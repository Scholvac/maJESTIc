package de.sos.script.ast.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.sos.script.ast.ASTNode;
import de.sos.script.ast.ASTNode.ASTNamedNode;
import de.sos.script.ast.INamedElement;
import de.sos.script.ast.IScopeProvider;
import de.sos.script.ast.IType;
import de.sos.script.ast.ITypeResolver;

public class Scope {

	private final ASTNode mNode;
	
	public Scope(final ASTNode n) { 
		mNode = n;
	}
	
	public Scope getParentScope() {
		if (mNode.getParent() != null)
		{
			ASTNode n = mNode.getParent();
			if (n != null) {
				while(n != null) {
					if (n instanceof IScopeProvider)
						return ((IScopeProvider) n).getScope();
					n = n.getParent();
				}
			}
			return null;
		}
		return null;
	}

	
	public INamedElement getNamedElement(final String name) {
		Map<String, INamedElement> map = new HashMap<>();
		collectNamedElements(map, new HashSet<>());
		return map.get(name);
	}

	
	public IType getTypeOf(ASTNode pn) {
		String name = null;
		if (pn instanceof INamedElement) name = ((INamedElement)pn).getName();
		else if (pn instanceof ASTNamedNode) name = ((ASTNamedNode)pn).getName();
		if (name == null)
			return null;
		//this method may be called with elements from another script (e.g. a script for autocompletion), 
		//therefore we extract the local element
		INamedElement ne = getNamedElement(name);
		if (ne == null)
			return null;
		if (ne instanceof ITypeResolver)
			return ((ITypeResolver)ne).getType(this);
		return null;
	}


	private IType _getTypeOf(final ASTNode pn, final Set<ASTNode> visited) {
		if (visited.contains(pn) || visited.size() > 100)
			return null;
		visited.add(pn);
		if (pn instanceof IType)
			return (IType)pn;
		
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
		return null;
	}

	public Collection<INamedElement> getAllNamedElements() {
		Map<String, INamedElement> map = new HashMap<>();
		collectNamedElements(map, new HashSet<>());
		return map.values();
	}
	
	
	protected void collectNamedElements(Map<String, INamedElement> map, Set<ASTNode> visited) {
		if (visited.contains(mNode))
			return ;
		visited.add(mNode);
		
		final List<ASTNode> children = mNode.getChildren();
		if (children != null && children.isEmpty() == false) {
			for (int i = 0; i < children.size(); i++) {
				ASTNode n = children.get(i);
				if (n instanceof INamedElement) {
					final INamedElement ne = (INamedElement)n;
					final String name = ne.getName();
					if (name != null && map.containsKey(name) == false) //do not overwrite elements from lower scopes
						map.put(name, ne);
				}
			}
		}
		
		Scope ps = getParentScope();
		if (ps != null) 
			ps.collectNamedElements(map, visited);
		
	}

}
