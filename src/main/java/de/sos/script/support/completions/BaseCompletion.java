package de.sos.script.support.completions;


public class BaseCompletion implements ICompletion {

	private static final int FEATURE = 0;
	private static final int TYPE = 1;
	private static final int DEFINED_IN = 2;
	private static final int DESCRIPTON = 3;
	
	
	private final String[] mStrings = new String[4];
	
	public BaseCompletion(final String feature, final String type, final String defIn, final String desc) {
		mStrings[FEATURE] = feature;
		mStrings[TYPE] = type;
		mStrings[DEFINED_IN] = defIn;
		mStrings[DESCRIPTON] = desc;
	}
		
	@Override
	public String getFeatureString() {
		return mStrings[FEATURE];
	}

	@Override
	public String getTypeAsString() {
		return mStrings[TYPE];
	}

	@Override
	public String getDefinedIn() {
		return mStrings[DEFINED_IN];
	}

	@Override
	public String getDescription() {
		return mStrings[DESCRIPTON];
	}


	@Override
	public int hashCode() {
		return getFeatureString().hashCode();
	}
	@Override
	public String toString() {
		return "BaseCompletion [" + getFeatureString() + "]";
	}
	
	
}
