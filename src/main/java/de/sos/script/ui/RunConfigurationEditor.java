package de.sos.script.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class RunConfigurationEditor extends JPanel{
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTable table;
	private JTable table_1;
	public RunConfigurationEditor() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JButton mBtnCancel = new JButton("Cancel");
		GridBagConstraints gbc_mBtnCancel = new GridBagConstraints();
		gbc_mBtnCancel.fill = GridBagConstraints.HORIZONTAL;
		gbc_mBtnCancel.insets = new Insets(0, 0, 0, 5);
		gbc_mBtnCancel.gridx = 0;
		gbc_mBtnCancel.gridy = 4;
		add(mBtnCancel, gbc_mBtnCancel);
		
		JLabel lblName = new JLabel("Name");
		GridBagConstraints gbc_lblName = new GridBagConstraints();
		gbc_lblName.insets = new Insets(0, 0, 5, 5);
		gbc_lblName.anchor = GridBagConstraints.WEST;
		gbc_lblName.gridx = 0;
		gbc_lblName.gridy = 0;
		add(lblName, gbc_lblName);
		
		textField = new JTextField();
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 0);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 1;
		gbc_textField.gridy = 0;
		add(textField, gbc_textField);
		textField.setColumns(10);
		
		JLabel lblScriptIdentifier = new JLabel("Script Identifier");
		GridBagConstraints gbc_lblScriptIdentifier = new GridBagConstraints();
		gbc_lblScriptIdentifier.anchor = GridBagConstraints.WEST;
		gbc_lblScriptIdentifier.insets = new Insets(0, 0, 5, 5);
		gbc_lblScriptIdentifier.gridx = 0;
		gbc_lblScriptIdentifier.gridy = 1;
		add(lblScriptIdentifier, gbc_lblScriptIdentifier);
		
		textField_1 = new JTextField();
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.insets = new Insets(0, 0, 5, 0);
		gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_1.gridx = 1;
		gbc_textField_1.gridy = 1;
		add(textField_1, gbc_textField_1);
		textField_1.setColumns(10);
		
		JLabel lblLanguage = new JLabel("Language");
		GridBagConstraints gbc_lblLanguage = new GridBagConstraints();
		gbc_lblLanguage.anchor = GridBagConstraints.WEST;
		gbc_lblLanguage.insets = new Insets(0, 0, 5, 5);
		gbc_lblLanguage.gridx = 0;
		gbc_lblLanguage.gridy = 2;
		add(lblLanguage, gbc_lblLanguage);
		
		JComboBox comboBox = new JComboBox();
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.insets = new Insets(0, 0, 5, 0);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 1;
		gbc_comboBox.gridy = 2;
		add(comboBox, gbc_comboBox);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "EntryPoint", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.gridwidth = 2;
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 3;
		add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0, 0};
		gbl_panel.rowHeights = new int[]{0, 0, 0, 0};
		gbl_panel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 1.0, 1.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JLabel lblFunctionName = new JLabel("Function Name");
		GridBagConstraints gbc_lblFunctionName = new GridBagConstraints();
		gbc_lblFunctionName.insets = new Insets(15, 0, 5, 5);
		gbc_lblFunctionName.gridx = 0;
		gbc_lblFunctionName.gridy = 0;
		panel.add(lblFunctionName, gbc_lblFunctionName);
		
		textField_2 = new JTextField();
		GridBagConstraints gbc_textField_2 = new GridBagConstraints();
		gbc_textField_2.insets = new Insets(0, 0, 5, 0);
		gbc_textField_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_2.gridx = 1;
		gbc_textField_2.gridy = 0;
		panel.add(textField_2, gbc_textField_2);
		textField_2.setColumns(10);
		
		JLabel lblAttributes = new JLabel("Attributes");
		GridBagConstraints gbc_lblAttributes = new GridBagConstraints();
		gbc_lblAttributes.insets = new Insets(0, 0, 5, 5);
		gbc_lblAttributes.gridx = 0;
		gbc_lblAttributes.gridy = 1;
		panel.add(lblAttributes, gbc_lblAttributes);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 1;
		gbc_scrollPane.gridy = 1;
		panel.add(scrollPane, gbc_scrollPane);
		
		table = new JTable();
		scrollPane.setViewportView(table);
		table.setModel(new DefaultTableModel(
			new Object[][] {
				{null, null, null},
				{null, null, null},
				{null, null, null},
				{null, null, null},
				{null, null, null},
			},
			new String[] {
				"Name", "Value", "Type"
			}
		) {
			Class[] columnTypes = new Class[] {
				String.class, Object.class, String.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		
		JLabel lblVariables = new JLabel("Variables");
		GridBagConstraints gbc_lblVariables = new GridBagConstraints();
		gbc_lblVariables.insets = new Insets(0, 0, 0, 5);
		gbc_lblVariables.gridx = 0;
		gbc_lblVariables.gridy = 2;
		panel.add(lblVariables, gbc_lblVariables);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
		gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_1.gridx = 1;
		gbc_scrollPane_1.gridy = 2;
		panel.add(scrollPane_1, gbc_scrollPane_1);
		
		table_1 = new JTable();
		table_1.setModel(new DefaultTableModel(
			new Object[][] {
				{null, null},
				{null, null},
				{null, null},
				{null, null},
				{null, null},
			},
			new String[] {
				"Name", "Value"
			}
		) {
			Class[] columnTypes = new Class[] {
				String.class, Object.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		table_1.getColumnModel().getColumn(0).setPreferredWidth(68);
		table_1.getColumnModel().getColumn(1).setPreferredWidth(221);
		scrollPane_1.setViewportView(table_1);
		
		JButton mBtnRun = new JButton("Run");
		GridBagConstraints gbc_mBtnRun = new GridBagConstraints();
		gbc_mBtnRun.fill = GridBagConstraints.HORIZONTAL;
		gbc_mBtnRun.gridx = 1;
		gbc_mBtnRun.gridy = 4;
		add(mBtnRun, gbc_mBtnRun);
	}

}
