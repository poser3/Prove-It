package edu.emory.prove_it.variable_panel;
import java.awt.BorderLayout;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import edu.emory.prove_it.MainWindow;
import edu.emory.prove_it.expression.VariableEnvironment;
import edu.emory.prove_it.expression.VariableExpression;


@SuppressWarnings("serial")
public class VariablePanel extends JPanel {

	private final VariableEnvironment variables = new VariableEnvironment();
	private final JList<VariableExpression> variableList = new JList<VariableExpression>(variables);
	
	public VariablePanel(MainWindow mainWindow) {
		this.setLayout(new BorderLayout());
		variableList.setCellRenderer(new VariableListCellRenderer());
		variableList.addMouseListener(new VariablePanelPopClickListener(mainWindow));
		variableList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.add(variableList);
		
		JScrollPane variableScrollPane = new JScrollPane(variableList);
		variableScrollPane.setVerticalScrollBar(variableScrollPane.createVerticalScrollBar());
		this.add(variableScrollPane);
	}
	
	public void addVariable(VariableExpression variable) {
		variables.add(variable);
	}
	
	public VariableEnvironment getEnvironment() {
		return variables;
	}
	
	public VariableExpression getSelectedValue() {
		return variableList.getSelectedValue();
	}
}
