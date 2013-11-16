import javax.swing.DefaultListModel;

@SuppressWarnings("serial")
public class ExpressionListModel extends DefaultListModel<Statement> {
	
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
	
	public int getSize() {
		int count = 0;
		for (Object o : toArray())
			if (! ((Statement) o).isHidden())
				count++;
		
		return count;
	}

}
