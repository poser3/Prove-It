package edu.emory.prove_it.statement_panel;
import java.awt.Component;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import edu.emory.prove_it.expression.Statement;
import edu.emory.prove_it.util.LatexHandler;

public class StatementListCellRenderer implements ListCellRenderer<Statement> {
	
	@Override
	public Component getListCellRendererComponent(JList<? extends Statement> list, Statement value, int index, boolean isSelected, boolean cellHasFocus) {
		String latex = value.toLatex();
		BufferedImage image = (BufferedImage) LatexHandler.latexToImage(latex);
		ImageIcon icon = new ImageIcon(image);
		JLabel label = new JLabel(icon);
		return label;
	}
}
