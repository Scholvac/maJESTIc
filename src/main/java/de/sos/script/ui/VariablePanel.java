package de.sos.script.ui;

import java.awt.BorderLayout;
import java.util.Arrays;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;

import de.sos.script.ScriptVariable;
import de.sos.script.run.dbg.DbgScriptVariable;
import de.sos.script.ui.UIDebugController.DebugEvent;
import de.sos.script.ui.UIDebugController.DebugEventType;
import io.reactivex.disposables.Disposable;

public class VariablePanel extends JPanel {

	
	
	private static class VariableTreeNode extends DefaultMutableTreeTableNode {

		private ScriptVariable	mVariable;
		private boolean			mChildsInitialized = false;
		
		public VariableTreeNode(ScriptVariable var) {
			super(var);
			mVariable = var;
		}

		public Object get(int column) {
			switch(column) {
			case 0: return mVariable.getName();
			case 1: return mVariable.getStringValue();
			}
			return null;
		}
		
		@Override
		public int getChildCount() {
			if (!mChildsInitialized) {
				mChildsInitialized = true;
				initializeChildren();
			}
			return super.getChildCount();
		}

		private void initializeChildren() {
			List<ScriptVariable> subs = mVariable.getChildren();
			if (subs != null && subs.isEmpty() == false)
				for (ScriptVariable sv : subs) {
					add(new VariableTreeNode(sv));
				}
		}
		
	}
	private static class VariableTreeTableModel extends DefaultTreeTableModel {
		public VariableTreeTableModel() {
			super(new VariableTreeNode(new DbgScriptVariable("Root", null)), Arrays.asList("Name", "Value"));
		}
		
		@Override
		public Object getValueAt(Object node, int column) {
			if (node instanceof VariableTreeNode) {
				return ((VariableTreeNode)node).get(column);
			}
			return super.getValueAt(node, column);
		}
		
	}

	private UIController 			mController;
	private VariableTreeTableModel	mTreeTableModel;
	private JXTreeTable 			mTreeTable;
	private Disposable 				mDebugEventDisp;
	
	public VariablePanel(UIController controller) {
		setLayout(new BorderLayout(0, 0));
		
		mTreeTableModel = new VariableTreeTableModel();
		mTreeTable = new JXTreeTable(mTreeTableModel);
		
		JScrollPane scrollPane = new JScrollPane(mTreeTable);
		add(scrollPane, BorderLayout.CENTER);
		mController = controller;
		
		mDebugEventDisp = mController.getDebugController().subscribeDebugEvents(de -> onDebugEvent(de));
	}

	private void onDebugEvent(DebugEvent de) {
		final MutableTreeTableNode root = (MutableTreeTableNode) mTreeTableModel.getRoot();
		if (de.type != DebugEventType.INTERRUPT_Start) {
			mTreeTable.setEnabled(false);
			while(root.getChildCount() > 0)
				mTreeTableModel.removeNodeFromParent((MutableTreeTableNode) root.getChildAt(0));
			return ;
		}
		mTreeTable.setEnabled(true);
		try {
			List<ScriptVariable> variables = de.context.getAccessableVariables();
			for (ScriptVariable v : variables) {
				mTreeTableModel.insertNodeInto(new VariableTreeNode(v),  root, root.getChildCount());
			}
		}catch(Exception | Error e) {
			e.printStackTrace();
		}
	}

}
