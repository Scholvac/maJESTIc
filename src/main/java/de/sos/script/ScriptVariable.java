package de.sos.script;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import de.sos.script.ast.TypeManager;

public class ScriptVariable implements IScriptVariable {
	public static enum VariableDirection {
		IN, OUT, INOUT
	}
	
	private final String			mName;
	private final Class<?>			mClazz;
	private Object					mValue;
	private VariableDirection		mDirection;
	private String mDescription;
	
	public ScriptVariable(final String name) {
		this(name, null, Object.class, VariableDirection.INOUT);
	}
	public ScriptVariable(final String name, final Class<?> expected) {
		this(name, null, expected, VariableDirection.INOUT);
	}
	public ScriptVariable(final String name, final Object value) {
		this(name, value, VariableDirection.INOUT);
	}
	public ScriptVariable(final String name, Object value, VariableDirection direction) {
		this(name, value, value != null ? value.getClass() : Object.class, direction);
	}
	public ScriptVariable(final String name, Object value, Class<?> expectedType, VariableDirection direction) {
		mName = name;
		mValue = value;
		mDirection = direction;
		mClazz = expectedType;
	}
	
	/* (non-Javadoc)
	 * @see de.sos.script.IScriptVariable#getName()
	 */
	@Override
	public String getName() { return mName; }
	/* (non-Javadoc)
	 * @see de.sos.script.IScriptVariable#getValue()
	 */
	@Override
	public Object getValue() { return mValue; }
	/* (non-Javadoc)
	 * @see de.sos.script.IScriptVariable#getDirection()
	 */
	@Override
	public VariableDirection getDirection() { return mDirection; }
	/* (non-Javadoc)
	 * @see de.sos.script.IScriptVariable#getExpectedType()
	 */
	@Override
	public Class<?> getExpectedType() { return mClazz; }
	
	
	public VariableDirection setDirection(VariableDirection direction) {
		VariableDirection old = mDirection;
		mDirection = direction;
		return old;
	}
	/* (non-Javadoc)
	 * @see de.sos.script.IScriptVariable#setValue(java.lang.Object)
	 */
	@Override
	public Object setValue(Object newValue) {
		Object old = mValue;
		mValue = newValue;
		return old;
	}
	/* (non-Javadoc)
	 * @see de.sos.script.IScriptVariable#getStringValue()
	 */
	@Override
	public String getStringValue() {
		return "" + getValue();
	}
	
	
	
	public List<ScriptVariable> getChildren() {
		final Object value = getValue();
		if (value == null) 
			return null;
		final Class<?> clazz = value.getClass();
		if (TypeManager.isJavaPrimitive(clazz))
			return null;
		
		try {
			ArrayList<ScriptVariable> out = new ArrayList<>();
			addFields(value, clazz.getDeclaredFields(), out);
			addFields(value, clazz.getFields(), out);
			return out;
		}catch(Exception | Error e) {
			e.printStackTrace();
		}
		return null;
	}
	private void addFields(final Object value, Field[] fields, ArrayList<ScriptVariable> out) {
		if (fields != null && fields.length > 0) {
			for (Field f : fields) {
				try{
					final boolean acc = f.isAccessible();
					f.setAccessible(true);
					Object v = f.get(value);
					if (v != null)
						out.add(new ScriptVariable(f.getName(), v));
					f.setAccessible(acc);
				}catch(Exception e) {
					out.add(new ScriptVariable(f.getName() + "_Error", "Could not receive value", f.getType(), VariableDirection.INOUT));
				}
			}
		}
	}
	public void setDescription(String description) {
		mDescription = description;
	}
	public String getDescription() { return mDescription; }
}
