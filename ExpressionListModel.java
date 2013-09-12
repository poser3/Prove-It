import javax.swing.DefaultListModel;

@SuppressWarnings("serial")
public class ExpressionListModel extends DefaultListModel {
	
	public Expression getElementAt(int index) {
		int count = 0;
		for (Object o : toArray())
			if (! ((Expression) o).isHidden()) {
				if (count == index)
					return (Expression) o;
				count++;
			}
		
		return null;
	}
	
	public int getSize() {
		int count = 0;
		for (Object o : toArray())
			if (! ((Expression) o).isHidden())
				count++;
		
		return count;
	}

}
