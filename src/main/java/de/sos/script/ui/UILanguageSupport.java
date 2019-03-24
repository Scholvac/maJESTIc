package de.sos.script.ui;

import org.fife.rsta.ac.AbstractLanguageSupport;
import org.fife.rsta.ac.java.JavaParamListCellRenderer;
import org.fife.rsta.ac.java.JavadocUrlHandler;
import org.fife.rsta.ac.js.JavaScriptCellRenderer;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import de.sos.script.IScriptManager;

public class UILanguageSupport extends AbstractLanguageSupport {

	
//	/**
//	 * Listens for various events in a text area editing Java (in particular,
//	 * caret events, so we can track the "active" code block).
//	 */
//	private class Listener implements CaretListener, ActionListener {
//
//		private RSyntaxTextArea textArea;
//		private Timer t;
//
//		public Listener(RSyntaxTextArea textArea) {
//			this.textArea = textArea;
//			textArea.addCaretListener(this);
//			t = new Timer(650, this);
//			t.setRepeats(false);
//		}
//
//		@Override
//		public void actionPerformed(ActionEvent e) {
////			reparseScript();
//			System.out.println("Action Performed");
//		}
//
//		@Override
//		public void caretUpdate(CaretEvent e) {
//			t.restart();
//		}
//
//		/**
//		 * Should be called whenever Java language support is removed from a
//		 * text area.
//		 */
//		public void uninstall() {
//			textArea.removeCaretListener(this);
//		}
//
//	}
	
	
	private IScriptManager 			mScriptManager;
	
	private AutoCompletion			mAutoCompletion = null;
	private UICompletionProvider 	mCompletionProvider;
	private RSyntaxTextArea	 		mTextArea;
//	private Listener 				mListener;
	private UIParser				mParser = null;
	

	public UILanguageSupport() {
		mParser = new UIParser();
		AutoCompletion ac = new AutoCompletion(mCompletionProvider = new UICompletionProvider(mParser));
		ac.setListCellRenderer(new JavaScriptCellRenderer()); //this one has html support for short-desc
		ac.setAutoCompleteEnabled(true);
		ac.setAutoActivationEnabled(true);
		ac.setAutoActivationDelay(300);
		ac.setExternalURLHandler(new JavadocUrlHandler());
		ac.setParameterAssistanceEnabled(true);
		ac.setParamChoicesRenderer(new JavaParamListCellRenderer());
		ac.setShowDescWindow(true);
		mAutoCompletion = ac;
	}

	public void setScriptManager(IScriptManager managerForExtension) {
		mScriptManager = managerForExtension;
		mParser.setScriptManager(managerForExtension);
		
		
	}
	
	public void setStyle(String constant) {
		
	}
	
	@Override
	public void install(RSyntaxTextArea textArea) {
		mTextArea = textArea;
		mAutoCompletion.install(textArea);
//		mListener = new Listener(textArea);
		
		
		textArea.putClientProperty(PROPERTY_LANGUAGE_PARSER, mParser);
		textArea.addParser(mParser);
		super.installImpl(textArea, mAutoCompletion);
	}

	
	@Override
	public void uninstall(RSyntaxTextArea textArea) {
		super.uninstallImpl(textArea);
		
		mAutoCompletion.uninstall();
//		mListener.uninstall();
		textArea.removeParser(mParser);
		textArea.putClientProperty(PROPERTY_LANGUAGE_PARSER, null);
		textArea.setToolTipSupplier(null);
	}

}
