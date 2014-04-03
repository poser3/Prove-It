import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;


@SuppressWarnings("serial")
public class VariablePanelPopUpMenu extends JPopupMenu {
	
	private final MainWindow mainWindow_;
	private final JMenuItem pairItem_;
	
	public VariablePanelPopUpMenu(MainWindow mainWindow, MouseEvent mouseEvent) {	    	
		mainWindow_ = mainWindow;
		
		final Pairing selectedPairing = mainWindow_.getTheoremPanel().getPairingsList().getSelectedValue();
		final Type pairingType = selectedPairing == null ? null : selectedPairing.getVariableExpression().getType();
		final VariableExpression selectedVariable = mainWindow_.getVariablePanel().getSelectedValue();
    	
		pairItem_ = new JMenuItem("Pair with selected theorem variable");
		pairItem_.setEnabled((selectedPairing != null) && (selectedVariable != null)
				&& (pairingType == selectedVariable.getType()));
		pairItem_.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedPairing.pair(selectedVariable);
				mainWindow_.getTheoremPanel().update();
			}});
    	add(pairItem_);
	}
}
