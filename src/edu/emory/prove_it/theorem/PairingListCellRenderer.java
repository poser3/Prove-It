package edu.emory.prove_it.theorem;
import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import edu.emory.prove_it.expression.Expression;
import edu.emory.prove_it.util.LatexHandler;
import edu.emory.prove_it.util.LookAndFeel;

public class PairingListCellRenderer implements ListCellRenderer<Pairing> {
	
	@Override
	public Component getListCellRendererComponent(JList<? extends Pairing> list, Pairing value, int index, boolean isSelected, boolean cellHasFocus) {
		
		String latex = "?";
		
		if (value != null) {
			
			String type = value.getVariableExpression().getType().toString();
			
			if (value.isPaired()) {
				//make a copy of the expression so that any selections can be removed,
				//otherwise the selected subexpression will show as highlighted in the pairings list.
				Expression copy = value.getPairedExpression().duplicate();
				copy.deselectRecursive();
				latex = String.format("\\textrm{%s } %s \\leftarrow %s",
						type,
						value.getVariableExpression().toLatex(),
						copy.toLatex());
			}
			else {
				latex = String.format("\\textrm{%s } %s \\textrm{ is unpaired}",
						type,
						value.getVariableExpression().toLatex());
			}
			
			if (isSelected)
	            latex = String.format("\\bgcolor{%s}{%s}",
	            		LookAndFeel.SELECTED_LATEX_COLOR,
	            		latex);
		}
		
		return new JLabel(new ImageIcon(LatexHandler.latexToImage(latex)));
	}
}