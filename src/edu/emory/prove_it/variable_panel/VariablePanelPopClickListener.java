package edu.emory.prove_it.variable_panel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import edu.emory.prove_it.MainWindow;


public class VariablePanelPopClickListener extends MouseAdapter {	
	MainWindow mainWindow_;
	
	public VariablePanelPopClickListener(MainWindow mainWindow) {
		mainWindow_ = mainWindow;
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger())
            doPop(e);
    }

    @Override
	public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger())
            doPop(e);
    }

    private void doPop(MouseEvent e) {
		VariablePanelPopUpMenu menu = new VariablePanelPopUpMenu(mainWindow_, e);
		menu.show(e.getComponent(), e.getX(), e.getY());  
    }
}