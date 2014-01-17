import java.awt.Component;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;

public class ExpressionListCellRenderer implements ListCellRenderer<Expression> {
        
        public Component getListCellRendererComponent(JList<? extends Expression> list, Expression value, int index, boolean isSelected, boolean cellHasFocus) {
                String statementNumberString = "\\textrm{" + (index+1) + ".} \\:";
        		String latex = statementNumberString + value.toLatex();
                if (isSelected)
                        latex = "{\\bgcolor{" + LookAndFeel.SELECTED_LATEX_COLOR + "}" + statementNumberString + latex + "}";
                
                BufferedImage image = (BufferedImage) LatexHandler.latexToImage(latex);
                ImageIcon icon = new ImageIcon(image);
                JLabel label = new JLabel(icon);
                label.setHorizontalAlignment(SwingConstants.LEFT);
                return label;
        }
}

