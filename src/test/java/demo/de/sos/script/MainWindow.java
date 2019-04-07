package demo.de.sos.script;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;

import de.sos.common.param.ParameterContext;
import de.sos.script.ScriptSource;
import de.sos.script.ast.lang.java.JarManager;
import de.sos.script.ui.CallStackPanel;
import de.sos.script.ui.DebugActions;
import de.sos.script.ui.ScriptEditorPanel;
import de.sos.script.ui.UIController;
import de.sos.script.ui.VariablePanel;

public class MainWindow {

	public static final int ICON_SIZE = 32;
	private JFrame frame;
	private ScriptEditorPanel 	mScriptPanel;
	private CallStackPanel 		mCallStack;
	private VariablePanel 		mVariablePanel;
	
	private UIController 		mController;
	
	private EditorActions		mEditorActions;
	private DebugActions		mDbgActions;
	
	private JToolBar 			mToolBar;
	private JMenuBar 			mMenuBar;
	private JSplitPane 			splitPane;
	
	
	
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					JarManager jmgr = JarManager.get();
					jmgr.addDirectory(new File("src/main/java"));
					jmgr.addDirectory(new File("src/test/java"));
					
					UIController controller = new UIController(new ParameterContext());
					MainWindow window = new MainWindow(controller);
					window.frame.setVisible(true);
					
					loadLastOpenedFile(controller);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	protected static void loadLastOpenedFile(UIController controller) throws IOException {
		File f = new File("LastOpened.txt");
		if (f.exists()) {
			String lo = new String(Files.readAllBytes(f.toPath()));
			f = new File(lo);
			if (f.exists())
				controller.setSource(new ScriptSource.FileSource(f), null);
		}		
	}

	/**
	 * Create the application.
	 * @param controller 
	 */
	public MainWindow(UIController controller) {
		mController = controller;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("Scripting");
		frame.setBounds(100, 100, 900, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		mEditorActions = new EditorActions(mController, frame);
		mDbgActions = new DebugActions(mController);
		mToolBar = new JToolBar();
		frame.getContentPane().add(mToolBar, BorderLayout.NORTH);
		
		mScriptPanel = new ScriptEditorPanel(mController);
		mVariablePanel = new VariablePanel(mController);
		
		splitPane = new JSplitPane();
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);		
		
		
		splitPane.setLeftComponent(mScriptPanel);
		splitPane.setRightComponent(mVariablePanel);
				
		mMenuBar = new JMenuBar();
		frame.setJMenuBar(mMenuBar);
		
		setupActions();
	}

	private void setupActions() {
		JMenu file_menu = new JMenu("File");
		mMenuBar.add(file_menu);
		
		file_menu.add(mEditorActions.getOpenAction());
		file_menu.add(mEditorActions.getSaveAction());
		file_menu.add(mEditorActions.getSaveAsAction());
		file_menu.addSeparator();
		file_menu.add(mEditorActions.getExitAction());
		
		mToolBar.add(mEditorActions.getOpenAction());
		mToolBar.add(mEditorActions.getSaveAction());
		mToolBar.add(mEditorActions.getSaveAsAction());
		mToolBar.addSeparator();
		
		JMenu dbg_menu = new JMenu("Execute");
		mMenuBar.add(dbg_menu);
		
		dbg_menu.add(mDbgActions.getDebugAction());
		dbg_menu.add(mDbgActions.getRunAction());
		dbg_menu.add(mDbgActions.getStopAction());
		dbg_menu.addSeparator();
		dbg_menu.add(mDbgActions.getStepOverAction());
		dbg_menu.add(mDbgActions.getStepIntoAction());
		dbg_menu.add(mDbgActions.getStepOutAction());
		
		mToolBar.add(mDbgActions.getDebugAction());
		mToolBar.add(mDbgActions.getRunAction());
		mToolBar.add(mDbgActions.getStopAction());
		mToolBar.addSeparator();
		mToolBar.add(mDbgActions.getStepOverAction());
		mToolBar.add(mDbgActions.getStepIntoAction());
		mToolBar.add(mDbgActions.getStepOutAction());
		
		
	}
}
