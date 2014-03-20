import java.util.ArrayList;

import javax.swing.AbstractListModel;


@SuppressWarnings("serial")
public class VariableListModel extends AbstractListModel<VariableExpression> {
	
	private final ArrayList<VariableExpression> variables = new ArrayList<VariableExpression>();

	@Override
	public VariableExpression getElementAt(int index) {
		return variables.get(index);
	}

	@Override
	public int getSize() {
		return variables.size();
	}
	
	public void addElement(VariableExpression v) {
		int index = getSize();
		variables.add(v);
		fireContentsChanged(v, index, index);
		fireIntervalAdded(v, index, index);
	}
	
	public ArrayList<VariableExpression> getDelegate() {
		return variables;
	}

}
