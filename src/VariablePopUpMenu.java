import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;


@SuppressWarnings("serial")
public class VariablePopUpMenu extends JPopupMenu {
	
    JMenuItem pairItem;
    MainWindow mainWindow_;
    VariablePanel variableListPanel_;
    VariableExpression selectedVariable;
    int indexClicked_;
    
    //////////////////
    // Constructors //
    //////////////////
    

    public VariablePopUpMenu(MainWindow mainWindow, MouseEvent mouseEvent) {
    	
    	mainWindow_ = mainWindow;

    	variableListPanel_ = mainWindow_.getVariableListPanel();
    	
    	indexClicked_ = variableListPanel_.getVariableList().locationToIndex(mouseEvent.getPoint());
    	
    	if (indexClicked_ != -1) {
    		selectedVariable = variableListPanel_.getEnvironment().get(indexClicked_);
    	}
    	
    	/*
    	 * The pairItem is only enabled when (1) a variable is selected, (2) a
    	 * pairing from a theorem is selected, and (3) the type of the selected
    	 * subexpression matches the type of the variable in the theorem
    	 * pairing.
    	 * 
    	 * If we add multiple selection to the statement list then we must
    	 * TODO: make sure that only one statement is selected as we try to pair.
    	 */
    	pairItem = new JMenuItem("Pair with selected theorem variable");
    	final Pairing selectedPairing = mainWindow_.getTheoremPanel().getPairingsList().getSelectedValue();
    	pairItem.setEnabled((selectedVariable != null) && (selectedPairing != null)
    			&& (selectedVariable.getType() == selectedPairing.getVariableExpression().getType()));
    	pairItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedPairing.pair(selectedVariable);
				mainWindow_.getTheoremPanel().update();
				System.out.println("Pairing expression...");
			}});
    	add(pairItem);
    }
}
