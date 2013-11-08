import java.awt.Component;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class ExpressionListCellRenderer implements ListCellRenderer<Expression> {
	
	public Component getListCellRendererComponent(JList<? extends Expression> list, Expression value, int index, boolean isSelected, boolean cellHasFocus) {
		String latex = value.toLatex();
		if (isSelected)
			latex = "{\\bgcolor{Yellow}" + latex + "}";
		
		BufferedImage image = (BufferedImage) LatexHandler.latexToImage(latex);
		ImageIcon icon = new ImageIcon(image);
		JLabel label = new JLabel(icon);
		return label;
	}
}
