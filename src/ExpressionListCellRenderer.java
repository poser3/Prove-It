import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class ExpressionListCellRenderer implements ListCellRenderer<Expression> {
	
	public Component getListCellRendererComponent(JList<? extends Expression> list, Expression value, int index, boolean isSelected, boolean cellHasFocus) {
		String latex = value.toLatex();
		//if (isSelected)
		//	latex = "\\usepackage{color} {\\color{Blue}" + latex + "}";
		
		BufferedImage image = (BufferedImage) LatexHandler.latexToImage(latex);
		System.out.println("Image: "+image.getWidth()+" "+image.getHeight());
		ImageIcon icon = new ImageIcon(image);
		System.out.println("Icon: "+icon.getIconWidth()+" "+icon.getIconHeight());
		JLabel label = new JLabel(icon);
		label.setSize(image.getWidth(), image.getHeight());
		System.out.println("Label: "+label.getWidth()+" "+label.getHeight());
		
		if (isSelected) {
			label.setBackground(Color.BLUE);
			label.setForeground(Color.YELLOW);
		} else {
			label.setBackground(Color.YELLOW);
			label.setForeground(Color.BLUE);
		}
		return label;
	}
}
