import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

import acm.program.GraphicsProgram;


public class MainMenuBar extends JMenuBar{
	
	//SETUP THE MENU SYSTEM...
	//JMenuBar menuBar;
	JMenu menu, submenu;
	JMenuItem menuItem;
	JRadioButtonMenuItem rbMenuItem;
	JCheckBoxMenuItem cbMenuItem;
	MainWindow mainWindow_;
	
	public MainMenuBar(MainWindow mainWindow) {

		super();
			
		mainWindow_ = mainWindow;
		
		menu = new JMenu("Manipulate");
		menuItem = new JMenuItem("Hide Selected Statements");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainWindow_.hideSelectedStatements();	
			}});
		menu.add(menuItem);
		menuItem = new JMenuItem("Show All Hidden Statements");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainWindow_.showHiddenStatements();	
			}});
		menu.add(menuItem);
		menuItem = new JMenuItem("Apply Theorem");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainWindow_.applyTheorem();	
			}});
		menu.add(menuItem);
		menuItem = new JMenuItem("Reload Theorems");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainWindow_.reloadTheorems();	
			}});
		menu.add(menuItem);
		menuItem = new JMenuItem("Load Statements...");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainWindow_.loadStatements();	
			}});
		menu.add(menuItem);
		menuItem = new JMenuItem("Substitute");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainWindow_.substitute();	
			}});
		menu.add(menuItem);
		this.add(menu);
		
		//Create the menu bar.
		//Build the first menu.
		menu = new JMenu("Example Menu");
		menu.setMnemonic(KeyEvent.VK_A);
		menu.getAccessibleContext().setAccessibleDescription(
		        "Showcase of different kinds of menu items");
		//menuBar.add(menu);
		this.add(menu);
		
		//a group of JMenuItems
		menuItem = new JMenuItem("A text-only menu item",
		                         KeyEvent.VK_T);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription(
		        "This doesn't really do anything");
		menu.add(menuItem);

		menuItem = new JMenuItem("Both text and icon",
		                         new ImageIcon("images/middle.gif"));
		menuItem.setMnemonic(KeyEvent.VK_B);
		menu.add(menuItem);

		menuItem = new JMenuItem(new ImageIcon("images/middle.gif"));
		menuItem.setMnemonic(KeyEvent.VK_D);
		menu.add(menuItem);
		
		//a group of radio button menu items
		menu.addSeparator();
		ButtonGroup group = new ButtonGroup();
		rbMenuItem = new JRadioButtonMenuItem("A radio button menu item");
		rbMenuItem.setSelected(true);
		rbMenuItem.setMnemonic(KeyEvent.VK_R);
		group.add(rbMenuItem);
		menu.add(rbMenuItem);

		rbMenuItem = new JRadioButtonMenuItem("Another one");
		rbMenuItem.setMnemonic(KeyEvent.VK_O);
		group.add(rbMenuItem);
		menu.add(rbMenuItem);

		//a group of check box menu items
		menu.addSeparator();
		cbMenuItem = new JCheckBoxMenuItem("A check box menu item");
		cbMenuItem.setMnemonic(KeyEvent.VK_C);
		menu.add(cbMenuItem);

		cbMenuItem = new JCheckBoxMenuItem("Another one");
		cbMenuItem.setMnemonic(KeyEvent.VK_H);
		menu.add(cbMenuItem);

		//a submenu
		menu.addSeparator();
		submenu = new JMenu("A submenu");
		submenu.setMnemonic(KeyEvent.VK_S);

		menuItem = new JMenuItem("An item in the submenu");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_2, ActionEvent.ALT_MASK));
		submenu.add(menuItem);

		menuItem = new JMenuItem("Another item");
		submenu.add(menuItem);
		menu.add(submenu);
		
		//Build second menu in the menu bar.
		menu = new JMenu("Another Menu");
		menu.setMnemonic(KeyEvent.VK_N);
		menu.getAccessibleContext().setAccessibleDescription(
		        "This menu does nothing");
		//menuBar.add(menu);
		this.add(menu);
	}

}

