import java.awt.Component;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;

public class PairingListCellRenderer implements ListCellRenderer<Pairing> {
	
	public Component getListCellRendererComponent(JList<? extends Pairing> list, Pairing value, int index, boolean isSelected, boolean cellHasFocus) {
		
		String latex = "?";
		
		String varTypeString = value.getVariableExpression().getTypeInLatex();
		
		if (value != null) {
			if (value.isPaired()) {
				//make a copy of the expression so that any selections can be removed,
				//otherwise the selected subexpression will show as highlighted in the pairings list.
				Expression originalPairedExpression = value.getPairedExpression();
				Expression copyOfPairedExpression = originalPairedExpression.duplicate();
				copyOfPairedExpression.deselectRecursive();
				latex = varTypeString + " " + value.getVariableExpression().toLatex() + " \\leftarrow " + copyOfPairedExpression.toLatex();
				//latex = value.getVariableExpression().toLatex() + " \\leftarrow " + value.getPairedExpression().toLatex();
			}
			else {
				latex = varTypeString + " " + value.getVariableExpression().toLatex() + " \\textrm{ is unpaired}";
			}
			
			if (isSelected) {
	            latex = "\\bgcolor{" + LookAndFeel.SELECTED_LATEX_COLOR + "}{" + latex + "}";
			}
		}
		
		BufferedImage image = (BufferedImage) LatexHandler.latexToImage(latex);
		ImageIcon icon = new ImageIcon(image);
		JLabel label = new JLabel(icon);
		return label;
	}
}