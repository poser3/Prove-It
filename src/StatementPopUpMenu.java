import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;


public class StatementPopUpMenu extends JPopupMenu {
	
    JMenuItem anItem;
    
    //////////////////
    // Constructors //
    //////////////////
    
    public StatementPopUpMenu() {
    	anItem = new JMenuItem("Click Me!");
    	add(anItem);
    }
}
