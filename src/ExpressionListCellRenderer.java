import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import acm.graphics.GCanvas;

public class ExpressionListCellRenderer implements ListCellRenderer<Expression> {
	
	public Component getListCellRendererComponent(JList<? extends Expression> list, Expression value, int index, boolean isSelected, boolean cellHasFocus) {
		if (value.isHidden()) {
			JLabel l = new JLabel("");
			l.setPreferredSize(new Dimension(1,30));
			return l;
		}
		else {
			GCanvas canvas = new GCanvas();
			String latex = value.toLatex();
			//if (isSelected)
			//	latex = "\\usepackage{color} {\\color{Blue}" + latex + "}";
			canvas.add(LatexHandler.latexToGImage(latex));
			return canvas;
		}
	}
}
