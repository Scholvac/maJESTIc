package de.sos.script.impl;

import java.util.Arrays;
import java.util.List;

public class QualifiedName {



	public static QualifiedName createWithRegEx(String string, String seperator) {
		return new QualifiedName(string.split(seperator));
	}

	private List<String> mSegments;
	
	
	public QualifiedName(String[] segments) {
		this(Arrays.asList(segments));
	}
	
	public QualifiedName(List<String> segments) {
		mSegments = segments;
	}

	public String lastSegment() {
		return mSegments.get(mSegments.size()-1);
	}

	public QualifiedName removeSegmentsFromEnd(int i) {
		return new QualifiedName(mSegments.subList(0, mSegments.size()-i));
	}
	public QualifiedName removeSegmentsFromStart(int i) {
		return new QualifiedName(mSegments.subList(i, mSegments.size()));
	}

	public int numSegments() {
		return mSegments.size();
	}

	public String firstSegment() {
		return mSegments.get(0);
	}

	public void localRemoveSegmentsFromStart(int i) {
		mSegments = mSegments.subList(i, mSegments.size());		
	}

	public String toString(String seperator) {
		String out = "";
		for (int i = 0; i < mSegments.size()-1; i++)
			out += mSegments.get(i) + seperator;
		out += lastSegment();
		return out;
	}

}
