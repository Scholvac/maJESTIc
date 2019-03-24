package de.sos.script;

public class ParserProblem {

	public enum Level {
		INFO, WARN, ERROR
	}



	private final Level 			mLevel;
	private final String 			mMessage;
	private final int 				mLine;
	
		
	public ParserProblem(Level level, String message, int line) {
		mLevel = level;
		mMessage = message;
		mLine = line;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Level = [" + mLevel + "] ");
		sb.append("Line = ");
		sb.append(mLine);
		sb.append(" Message: ");
		sb.append(mMessage);
		return sb.toString();
	}

	public Level getLevel() { return mLevel; }
	public String getMessage() { return mMessage; }
	public int getLine() { return mLine; }
}
