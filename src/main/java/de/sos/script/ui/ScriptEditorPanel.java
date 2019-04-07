package de.sos.script.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.undo.CompoundEdit;

import org.fife.ui.rsyntaxtextarea.ErrorStrip;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.Gutter;
import org.fife.ui.rtextarea.GutterIconInfo;
import org.fife.ui.rtextarea.IconRowHeader;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.fife.ui.rtextarea.RUndoManager;

import de.sos.common.param.ParameterContext;
import de.sos.script.EntryPoint;
import de.sos.script.IEntryPoint;
import de.sos.script.IScriptManager;
import de.sos.script.IScriptSource;
import de.sos.script.ScriptManager;
import de.sos.script.impl.lang.js.JSScriptManager;
import de.sos.script.impl.lang.py.PyScriptManager;
import de.sos.script.run.dbg.BreakPoint;
import de.sos.script.ui.UIController.IContentEditor;
import de.sos.script.ui.UIDebugController.DebugEvent;
import de.sos.script.ui.UIDebugController.DebugEventType;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class ScriptEditorPanel extends JPanel implements IContentEditor {

	public static final String ROW_COUNT = "ROWS";
	public static final String COLUMN_COUNT = "COLS";
	public static final String CODE_FOLDING = "CODE_FOLDING";
	public static final String MARK_OCCURENCES = "MARK_OCCURENCES";
	public static final String ENABLE_ERROR_STRIP = "ENABLE_ERROR_STRIP";
	public static final String ENABLE_LINE_NUMBERS = "ENABLE_LINE_NUMBERS";
	public static final String ENABLE_BREAKPOINTS = "ENABLE_BREAKPOINTS";
	public static final String DEBUG_POINT_ICON_PATH = "DEBUG_POINT_ICON_PATH";
	public static final String ROW_HEADER_ICON_SIZE = "ROW_HEADER_ICON_SIZE";
	public static final String PROGRAM_COUNTER_ICON_PATH = "PROGRAM_COUNTER_ICON_PATH";
	
	static class BreakPointIcon {
		GutterIconInfo		gii;
		BreakPoint			bp;
	}
	
	
	private final UIController		mController;
	
	private RSyntaxTextArea 		mTextArea;
	private RTextScrollPane			mScrollPane;
	private Gutter 					mGutter;
	private ErrorStrip 				mErrorStrip;
	private IconRowHeader			mIconRowHeader;
	
	private UILanguageSupport 		mLanguageSupport;
	private Set<BreakPointIcon> 	mBreakPointItems = new HashSet<>();
	
	
	private ImageIcon				mDbgPointIcon;
	private ImageIcon				mProgramCounterIcon;
	
	
	private Disposable 					mBreakPointDisp;
	private GutterIconInfo 				mDebugIcon;
	private ScriptEditorUndoableManager mUndoManager;
	
	
	public ScriptEditorPanel(UIController controller) {
		super();
		mController = controller;
		ParameterContext context = controller.getParameterContext();
		assert(mController != null);
		
		int size = context.getValue(ROW_HEADER_ICON_SIZE, 16);
		mDbgPointIcon 		= UIActions.getIcon(context.getValue(DEBUG_POINT_ICON_PATH, "icons/debug_debugPoint.png"), size);
		mProgramCounterIcon = UIActions.getIcon(context.getValue(PROGRAM_COUNTER_ICON_PATH, "icons/debug_linePointer.png"), size);
		
		setLayout(new BorderLayout());
		
		int rows = context.getValue(ROW_COUNT, 25);
		int cols = context.getValue(COLUMN_COUNT, 80);
		
		mTextArea = new RSyntaxTextArea(rows, cols) {
			protected RUndoManager createUndoManager() {
				return mUndoManager = new ScriptEditorUndoableManager(this, 300);
			};
		};
		mTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
		mTextArea.setCodeFoldingEnabled(context.getValue(CODE_FOLDING, true));
		mTextArea.setMarkOccurrences(context.getValue(MARK_OCCURENCES, true));
		
		mScrollPane = new RTextScrollPane(mTextArea);
		mGutter = mScrollPane.getGutter();
		mScrollPane.setLineNumbersEnabled(context.getValue(ENABLE_LINE_NUMBERS, true));
		
		mLanguageSupport = new UILanguageSupport();
		mLanguageSupport.install(mTextArea);
		
		add(mScrollPane, BorderLayout.CENTER);
		
		if (context.getValue(ENABLE_ERROR_STRIP, true)) {
			mErrorStrip = new ErrorStrip(mTextArea);
		}
		if (context.getValue(ENABLE_BREAKPOINTS, true)) {
			mScrollPane.setIconRowHeaderEnabled(true);
			//we do not get direct access to the iconrowheader, thus we search it
			for (Component c : mGutter.getComponents())
				if (c instanceof IconRowHeader) {
					mIconRowHeader = (IconRowHeader) c;
					break;
				}
			//register the listener for breakpoints
			if (mIconRowHeader == null)
				System.err.println("Could not find the IconRowHeader for Breakpoints");
			else {
				mIconRowHeader.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						if(e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1)
							toggleBreakPoint(e.getPoint());
					}
				});
			}
		}
		
		connectController(controller);
		mController.setContentProvider(this);
	}

	public Disposable subscribeEdit(final Consumer<CompoundEdit> consumer) {
		return mUndoManager.subscribeEdit(consumer);
	}
	
	public void applyTheme(Theme theme) {
		if (theme != null)
			theme.apply(mTextArea);
	}
	
	public void setPreferedRows(int rows) {
		mTextArea.setRows(rows);
	}
	
	
	public boolean canRedo() {
		return mTextArea != null ? mTextArea.canRedo() : false;
	}
	public void redoLastAction() {
		if (mTextArea != null){
			mTextArea.redoLastAction();
		}
	}
	
	public boolean canUndo() { return mTextArea != null ? mTextArea.canUndo(): false; }
	public void undoLastActions() { 
		if (mTextArea != null)
			mTextArea.undoLastAction();
	}
	
	public Document getDocument() {
		return mTextArea != null ? mTextArea.getDocument() : null;
	}
	
	private void connectController(UIController controller) {
		UIDebugController dbgC = controller.getDebugController();
		mBreakPointDisp = dbgC.subscribeBreakPointChanges(pcl -> updateBreakpoints());
		
		dbgC.subscribeDebugEvents(de -> onDebugEvent(de));
	}

	private void onDebugEvent(DebugEvent de) {
		if (de.type == DebugEventType.INTERRUPT_Start)
			onInterruptStart(de.breakpoint);
		else if (de.type == DebugEventType.INTERRUPT_End)
			onInterruptEnd(de.breakpoint);
	}

	private void onInterruptStart(final BreakPoint bp) {
		assert(bp != null);
		try {
			System.out.println("Line: " + bp.lineNumber);
			mTextArea.setCaretPosition(mTextArea.getLineStartOffset(bp.lineNumber));
			if (mProgramCounterIcon != null) {
				mDebugIcon = mGutter.addLineTrackingIcon(bp.lineNumber, mProgramCounterIcon, "Current position of debugger");
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
	}

	private void onInterruptEnd(final BreakPoint bp) {
		if (mDebugIcon != null) {
			mGutter.removeTrackingIcon(mDebugIcon);
		}
	}

	public int getLineNumber(final Point pt) {
		try {
			int offs = mTextArea.viewToModel(pt);
			int line = offs>-1 ? mTextArea.getLineOfOffset(offs) : -1;
			return line;
		} catch (BadLocationException e) {
			e.printStackTrace();
			return -1;
		}
	}
	protected void toggleBreakPoint(Point point) {
		int line = getLineNumber(point);
		if (line >= 0)
			mController.getDebugController().toggleBreakPoint(mController.getIdentifier(), line);
	}

	
	
	
	public void updateBreakpoints() {
		if (SwingUtilities.isEventDispatchThread())
			_updateBreakpoints();
		else
			SwingUtilities.invokeLater(new Runnable() { @Override public void run() { _updateBreakpoints(); } });
	}
	
	private void _updateBreakpoints() {
		List<BreakPoint> scriptBreakPoints = ScriptManager.theInstance.getBreakPointsForScript(mController.getIdentifier());
		Set<BreakPointIcon> toDelete = new HashSet<>(mBreakPointItems);
		Set<BreakPointIcon> toAdd = new HashSet<>();
		
		
		for (BreakPoint bp : scriptBreakPoints) {
			BreakPointIcon bpi = getBreakPointIcon(bp);
			if (bpi != null) {
				toDelete.remove(bpi);
			}else {
				try {
					GutterIconInfo gii = mGutter.addLineTrackingIcon(bp.lineNumber, mDbgPointIcon);
					bpi = new BreakPointIcon();
					bpi.bp = bp; bpi.gii = gii;
					toAdd.add(bpi);
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		mBreakPointItems.removeAll(toDelete);
		for (BreakPointIcon bpi : toDelete) {
			mGutter.removeTrackingIcon(bpi.gii);
		}
		mBreakPointItems.addAll(toAdd);
	}
	
	private BreakPointIcon getBreakPointIcon(BreakPoint bp) {
		for (BreakPointIcon bpi : mBreakPointItems)
			if (bpi.bp.sourceIdentifier.equals(bp.sourceIdentifier) && bpi.bp.lineNumber == bp.lineNumber)
				return bpi;
		return null;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////
	//									INTERFACES											   //
	/////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	
	
	
	//---------------------------	IContentProvider	---------------------------------------//
	


	@Override
	public String getScriptContent() {
		return mTextArea.getText();
	}

	@Override
	public boolean changeSource(final IScriptSource source, final IEntryPoint ep) {		
		if (SwingUtilities.isEventDispatchThread())
			_setContent(source, ep);
		else
			SwingUtilities.invokeLater(new Runnable() {				
				@Override
				public void run() {
					_setContent(source, ep);
				}
			});
		return true;
	}

	protected void _setContent(IScriptSource source, IEntryPoint ep) {
		String txt = source != null ? source.getContentAsString() : "";
		//we do not want the undo manager to undo the first insert
		mUndoManager.enable(false);
		mTextArea.setText(txt != null ? txt : "");
		mUndoManager.enable(true);
		//change layout settings
		if (source != null) {
			final String lang = source.getLanguage();
			final IScriptManager smgr = ScriptManager.theInstance.getManagerForExtension(lang);
			if (smgr != null) {
				mLanguageSupport.setScriptManager(smgr);
				mLanguageSupport.setEntryPoint(ep);
			}
			mTextArea.setSyntaxEditingStyle(getSyntaxConstant(lang));
			
		}
    	
    	_updateBreakpoints();
	}



	private String getSyntaxConstant(String lang) {
		if (lang != null) {
			if (lang.equals(JSScriptManager.LANG_JAVASCRIPT))
				return SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT;
			if (lang.equals("Java"))
				return SyntaxConstants.SYNTAX_STYLE_JAVA;
			if (lang.equals(PyScriptManager.LANG_JYPTHON))
				return SyntaxConstants.SYNTAX_STYLE_PYTHON;
		}
		return SyntaxConstants.SYNTAX_STYLE_NONE;
	}

	
}
