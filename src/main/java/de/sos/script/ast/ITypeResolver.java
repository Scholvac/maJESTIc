package de.sos.script.ast;

import de.sos.script.ast.util.Scope;

public interface ITypeResolver {

	IType getType(final Scope scope);
}
