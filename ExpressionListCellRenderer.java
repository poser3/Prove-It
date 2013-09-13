import java.awt.Component;
import java.awt.Dimension;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

//Some random comment
@SuppressWarnings("serial")
public class ExpressionListCellRenderer extends DefaultListCellRenderer {
	@Override
<<<<<<< HEAD
	public Component getListCellRendererComponentFIXME(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
=======
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
>>>>>>> c27bbe583e86d9887b180eb57537cb4936c92d0b
		if (value instanceof Expression) {
			Expression ex = (Expression) value;
			if (ex.isHidden()) {
				JLabel l = new JLabel("");
				l.setPreferredSize(new Dimension(1,30));
				return l;
			}
			else
				return super.getListCellRendererComponent(list, ((Expression) value).toLatex(), index, isSelected, cellHasFocus);
		} else
			return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			
	}
}
