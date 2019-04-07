package demo.de.sos.script;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import de.sos.script.IScriptSource;
import de.sos.script.ScriptSource;
import de.sos.script.ScriptSource.WriteableScriptSource;
import de.sos.script.ui.UIActions;
import de.sos.script.ui.UIController;


public class EditorActions extends UIActions {
	
	private final JFrame		mFrame;
	private final UIController	mController;

	private SaveAsDialogAction	mSaveAsAction = null;
	private SaveAction			mSaveAction = null;
	private OpenDialogAction	mOpenAction = null;
	private ExitAction 			mExitAction = null;
	
	
	public EditorActions(UIController controller, JFrame frame) {
		mFrame = frame;
		mController = controller;
	}
	
	public OpenDialogAction getOpenAction() {
		if (mOpenAction == null)
			mOpenAction = new OpenDialogAction();
		return mOpenAction;
	}
	public SaveAction getSaveAction() {
		if (mSaveAction == null)
			mSaveAction = new SaveAction();
		return mSaveAction;
	}
	public SaveAsDialogAction getSaveAsAction() { 
		if (mSaveAsAction == null)
			mSaveAsAction = new SaveAsDialogAction();
		return mSaveAsAction;
	}
	public ExitAction getExitAction() {
		if (mExitAction == null)
			mExitAction = new ExitAction();
		return mExitAction;
	}
	
	
	
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//													OPEN & SAVE																   //
	// doing open & save & saveAs here as this is expected to be handled seperatley when embeeded as library into a bigger project //
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private File				mFile;
	
	public void save(File file) {
		assert(file != null);
		try {
			FileWriter fw = new FileWriter(mFile);
			String content = mController.getContentProvider().getScriptContent();
			fw.write(content);
			fw.close();
			mFile = file;
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void save() throws IOException {
		String newContent = mController.getContentProvider().getScriptContent();
		IScriptSource source = mController.getSource();
		if (source != null) {
			if (source instanceof WriteableScriptSource == false || ((WriteableScriptSource)source).canWrite(newContent) == false)
				throw new IOException("ScriptSource is readonly");
			else
				((WriteableScriptSource)source).writeContent(newContent);
		}
	}
	public boolean open(File file) throws IOException {
		if (file.exists() == false || mController.getContentProvider() == null)
			return false;
		
		if (file.canRead()) {
			return mController.setSource(new ScriptSource.FileSource(file), null);
		}
		return false;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	private class ExitAction extends AbstractAction {

		ExitAction() {
			super("Exit");//, IconManager.getIcon(EditorActions.this, "icons/folder_document.png", ICON_SIZE));
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			//not the best way - but it's a demo
			System.exit(0);
		}
	}
	
	/**
     * Shows the Find dialog.
     */
	private class OpenDialogAction extends AbstractAction {

		OpenDialogAction() {
			super("Open", getIcon("icons/folder_document.png"));
			int c = mFrame.getToolkit().getMenuShortcutKeyMask();
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, c));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser(new File("."));
			chooser.setFileFilter(new FileFilter() {				
				@Override
				public String getDescription() {
					return "Python Files (*.py), JavaScript Files (*.js), Java Files (*.java)";
				}
				
				@Override
				public boolean accept(File f) {
					if (f.isDirectory() || f.getName().endsWith(".py") || f.getName().endsWith(".js") || f.getName().endsWith(".java"))
						return true;
					return false;
				}
			});
			chooser.setFileHidingEnabled(true);
			int res = chooser.showOpenDialog(mFrame);
			if (res == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				try {
					open(file);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			
		}

	}
	
	/**
     * Shows the Find dialog.
     */
	private class SaveAction extends AbstractAction {

		SaveAction() {
			super("Save", getIcon("icons/floppy_disk.png"));
			int c = mFrame.getToolkit().getMenuShortcutKeyMask();
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, c));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				save();
			} catch (IOException e1) {
				e1.printStackTrace();
			}			
		}
	}
	
	
	/**
     * Shows the Find dialog.
     */
	public class SaveAsDialogAction extends AbstractAction {

		SaveAsDialogAction() {
			super("Save As", getIcon("icons/save_as.png"));
			int c = mFrame.getToolkit().getMenuShortcutKeyMask();
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, c));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser(new File("."));
			chooser.setFileFilter(new FileFilter() {				
				@Override
				public String getDescription() {
					return "Python Files (*.py), JavaScript Files (*.js), Java Files (*.java)";
				}
				
				@Override
				public boolean accept(File f) {
					if (f.isDirectory() || f.getName().endsWith(".py") || f.getName().endsWith(".js") || f.getName().endsWith(".java"))
						return true;
					return false;
				}
			});
			chooser.setFileHidingEnabled(true);
			int res = chooser.showSaveDialog(mFrame);
			if (res == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				save(file);
			}
			
		}

	}



	

}
