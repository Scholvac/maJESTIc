package de.sos.script.ui;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.Segment;

import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.CompletionProviderBase;
import org.fife.ui.autocomplete.ParameterizedCompletion;

import de.sos.script.IScript;
import de.sos.script.support.ScriptCompletionProvider;
import de.sos.script.support.completions.ICompletion;

public class UICompletionProvider extends CompletionProviderBase implements CompletionProvider {

//	private IScript							mScript = null;
	private UIParser						mParser = null;
	private ScriptCompletionProvider 		mDelegate = new ScriptCompletionProvider();
	protected Segment 						mSegment = new Segment();
	
		
	public UICompletionProvider(UIParser parser) {
		super();
		setParameterizedCompletionParams('(', ", ", ')');
		mParser = parser;
	}

	@Override
	public String getAlreadyEnteredText(JTextComponent comp) {
		Document doc = comp.getDocument();

		
		int dot = comp.getCaretPosition();
		Element root = doc.getDefaultRootElement();
		int index = root.getElementIndex(dot);
		Element elem = root.getElement(index);
		int start = elem.getStartOffset();
		int len = dot-start;
		try {
			doc.getText(start, len, mSegment);
		} catch (BadLocationException ble) {
			ble.printStackTrace();
			return EMPTY_STRING;
		}

		int segEnd = mSegment.offset + len;
		start = segEnd - 1;
		
		while (start>=mSegment.offset && isValidChar(mSegment.array[start])) {
			start--;
		}
		start++;

		len = segEnd - start;
		return len==0 ? EMPTY_STRING : new String(mSegment.array, start, len);
	}

	protected boolean isValidChar(char ch) {
		return Character.isLetterOrDigit(ch) || ch=='_';
	}

	@Override
	public List<Completion> getCompletionsAt(JTextComponent comp, Point p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ParameterizedCompletion> getParameterizedCompletions(JTextComponent tc) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<Completion> getCompletionsImpl(JTextComponent comp) {
		int pos = mSegment.getEndIndex();
		IScript uiScript = mParser.reparseScript(comp);
		List<ICompletion> completions = mDelegate.getCompletions(uiScript, pos);
		return convertCompletions(completions);
	}

	private List<Completion> convertCompletions(List<ICompletion> completions) {
		ArrayList<Completion> out = new ArrayList<>();
		for (ICompletion in : completions) {
			Completion ui_c = in.getUICompletion(this);
			if (ui_c != null)
				out.add(ui_c);
		}
		return out;
	}



	

}
