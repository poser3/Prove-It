package edu.emory.prove_it.statement_panel;
import javax.swing.DefaultListModel;

import edu.emory.prove_it.expression.Statement;

@SuppressWarnings("serial")
public class StatementListModel extends DefaultListModel<Statement> {
	
	@Override
	public Statement getElementAt(int index) {
		int count = 0;
		for (Object o : toArray())
			if (! ((Statement) o).isHidden()) {
				if (count == index)
					return (Statement) o;
				count++;
			}
		
		return null;
	}
	
	@Override
	public int getSize() {
		int count = 0;
		for (Object o : toArray())
			if (! ((Statement) o).isHidden())
				count++;
		
		return count;
	}

}
