import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import acm.gui.TableLayout;
import acm.gui.TablePanel;

@SuppressWarnings("serial")
public class ExpressionPanel extends TablePanel {
	
	//Instance Variables
	private MainWindow mainWindow_;
	private JButton createStatementBtn_;
	private JButton addBtn_;
	private JButton subtractBtn_;
	private JButton multiplyBtn_;
	private JButton divideBtn_;
	private JButton simplifyBtn_;
	private JTextField expressionTextField_;
	
	
	private JButton jButtonFromResource(String pathToResource) {
        try {
        	Image image = ImageIO.read(new File(
        			SettingsReader.getSetting("image-path") + pathToResource));
        	ImageIcon imageIcon = new ImageIcon(image);
        	JButton jButton = new JButton(imageIcon);
        	jButton.setBorder( new EmptyBorder(0, 0, 0, 0) ); 
        	return jButton;
        }
        catch (IOException e) {
        	System.out.println("There was a problem loading the image");
        	return null;
        }
	}
	
	//Constructors
	public ExpressionPanel(MainWindow mainWindow) {
		 //invoke super-class constructor to set rows and cols
		super(1,15);
		mainWindow_ = mainWindow;
		this.setLayout(new TableLayout(1,10));
		
	  //Setup text field (and JLabel for it)
		this.add(new JLabel("Input:"));
		expressionTextField_ = new JTextField();
		this.add(expressionTextField_,"width=200");
		this.add(new JLabel(" "));
		  
      //Setup createStatement button
		createStatementBtn_ = jButtonFromResource("create_statement.png");
		createStatementBtn_.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("adding statement and selecting it");
				mainWindow_.addStatementAndSelect(expressionTextField_.getText(), true);
				mainWindow_.revalidate();
			}
			
		});
		this.add(createStatementBtn_);
		
      //Setup add button
		addBtn_ = jButtonFromResource("add.png");
		addBtn_.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				//do whatever the add button should do
			}
			
		});
		this.add(addBtn_);
		
      //Setup subtract button
		subtractBtn_ = jButtonFromResource("subtract.png");
		subtractBtn_.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				//do whatever the subtract button should do
			}
			
		});
		this.add(subtractBtn_);
		
      //Setup multiply button
		multiplyBtn_ = jButtonFromResource("multiply.png");
		multiplyBtn_.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				//do whatever the multiply button should do
			}
			
		});
		this.add(multiplyBtn_);
		
	//Setup divide button
		divideBtn_ = jButtonFromResource("divide.png");
		divideBtn_.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				//do whatever the divide button should do
			}
			
		});
		this.add(divideBtn_);
		
	//Setup divide button
		simplifyBtn_ = jButtonFromResource("simplify.png");
		simplifyBtn_.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				//do whatever the divide button should do
			}
			
		});
		this.add(simplifyBtn_);
	}
	
	//Getters and Setters

}
