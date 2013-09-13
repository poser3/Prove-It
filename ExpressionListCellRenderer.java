import java.awt.Component;
import java.awt.Dimension;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

//Some random comment
@SuppressWarnings("serial")
public class ExpressionListCellRenderer extends DefaultListCellRenderer {
	@Override
	public Component getListCellRendererComponentFIXME(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
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
