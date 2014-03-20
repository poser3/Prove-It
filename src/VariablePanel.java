import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


@SuppressWarnings("serial")
public class VariablePanel extends JPanel {

	private final VariableListModel variables = new VariableListModel();
	private final JList<VariableExpression> variableList = new JList<VariableExpression>(variables);
	
	public VariablePanel() {
		this.setLayout(new BorderLayout());
		variableList.setCellRenderer(new VariableListCellRenderer());
		this.add(variableList);
		
		JScrollPane variableScrollPane = new JScrollPane(variableList);
		variableScrollPane.setVerticalScrollBar(variableScrollPane.createVerticalScrollBar());
		//variableScrollPane.setPreferredSize(new Dimension(VARIABLE_SCROLLPANE_WIDTH,VARIABLE_SCROLLPANE_HEIGHT));
		this.add(variableScrollPane);
	}
	
	public void addVariable(VariableExpression variable) {
		variables.addElement(variable);
	}
	
	public ArrayList<VariableExpression> getEnvironment() {
		return variables.getDelegate();
	}
}
