import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class PopClickListener extends MouseAdapter {
	
	public void mousePressed(MouseEvent e){
        if (e.isPopupTrigger())
            doPop(e);
    }

    public void mouseReleased(MouseEvent e){
        if (e.isPopupTrigger())
            doPop(e);
    }

    private void doPop(MouseEvent e){
        StatementPopUpMenu menu = new StatementPopUpMenu();
        menu.show(e.getComponent(), e.getX(), e.getY());
    }
}
