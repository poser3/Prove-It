import java.awt.BorderLayout;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


@SuppressWarnings("serial")
public class VariablePanel extends JPanel {

	private final VariableEnvironment variables = new VariableEnvironment();
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
		variables.add(variable);
	}
	
	public VariableEnvironment getEnvironment() {
		return variables;
	}
}