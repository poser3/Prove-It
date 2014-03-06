import java.awt.Component;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class VariableListCellRenderer implements ListCellRenderer<VariableExpression> {
	
	@Override
	public Component getListCellRendererComponent(JList<? extends VariableExpression> list, VariableExpression value, int index, boolean isSelected, boolean cellHasFocus) {
		String latex = value.getTypeInLatex() + value.toLatex();
		BufferedImage image = (BufferedImage) LatexHandler.latexToImage(latex);
		ImageIcon icon = new ImageIcon(image);
		JLabel label = new JLabel(icon);
		return label;
	}
}
