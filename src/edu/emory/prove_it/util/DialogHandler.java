package edu.emory.prove_it.util;

import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class DialogHandler {

	public static String showArrangementAndGroupingDialog(String latex) {
		Image image = LatexHandler.latexToImage(latex);
		ImageIcon icon = new ImageIcon(image);
		String arrangementString = (String) JOptionPane.showInputDialog(null,
				                                       "Enter the new arrangement and grouping",
				                                       "Rearrange and Regroup",
				                                       JOptionPane.PLAIN_MESSAGE,
				                                       icon,
				                                       null,
				                                       "");
		return arrangementString;
	}
}
