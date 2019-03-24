package de.sos.script.ast.lang;

import java.util.Set;

import de.sos.script.ast.ASTName;
import de.sos.script.ast.INamedElement;
import de.sos.script.ast.IType;
import de.sos.script.ast.ITypeResolver;
import de.sos.script.ast.util.Scope;
import de.sos.script.support.completions.ICompletion;

public class NativeProperty extends ASTName implements INamedElement, ITypeResolver{

	private IType mType;

	public NativeProperty(final String name, final IType type) {
		super(null, name, -1, -1);
		mType = type;
	}
	
	@Override
	public IType getType(final Scope scope) {
		return mType;
	}

	@Override
	public void insertCompletions(Set<ICompletion> completions) {
		mType.insertCompletions(completions);
	}

}
