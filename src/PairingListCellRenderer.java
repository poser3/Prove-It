import java.awt.Component;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

@SuppressWarnings("unused")
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
				latex = type + " " + value.getVariableExpression().toLatex() + " \\leftarrow " + copy.toLatex();
				//latex = value.getVariableExpression().toLatex() + " \\leftarrow " + value.getPairedExpression().toLatex();
			}
			else {
				latex = type + " " + value.getVariableExpression().toLatex() + " \\textrm{ is unpaired}";
			}
			
			if (isSelected) {
	            latex = "\\bgcolor{" + LookAndFeel.SELECTED_LATEX_COLOR + "}{" + latex + "}";
			}
		}
		
		return new JLabel(new ImageIcon(LatexHandler.latexToImage(latex)));
	}
}