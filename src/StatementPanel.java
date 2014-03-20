
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


@SuppressWarnings("serial")
public class StatementPanel extends JPanel{
	
	//private final int STATEMENT_PANEL_WIDTH = 600;
	//private final int STATEMENT_PANEL_HEIGHT = 270;
    //private final int STATEMENT_SCROLLPANE_WIDTH = 600;
	//private final int STATEMENT_SCROLLPANE_HEIGHT = 270;
	private final int NOTHING_SELECTED = -1;

	private MainWindow mainWindow_;
	private StatementListModel statements_;
	private JList<Statement> statementList_;
	
	
	public StatementPanel(MainWindow mainWindow) {
		
		mainWindow_ = mainWindow;
		statements_ = new StatementListModel();
		statementList_ = new JList<Statement>(statements_);
		
		this.setLayout(new BorderLayout());
		statementList_.setCellRenderer(new StatementListCellRenderer());
		JScrollPane statementsScrollPane = new JScrollPane(statementList_);
		statementList_.setFocusable(true);
		statementList_.requestFocus();
		
		/*
		Action shiftDownAction = new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Shifter shifter = new Shifter(getSelectedStatement().getExpression());
				shifter.shiftSelectionDown();
				//getSelectedStatement().getExpression().shiftSelectionDeeper();
				statementList_.repaint();
			}
			
		}; 
		
		Action shiftUpAction = new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Shifter shifter = new Shifter(getSelectedStatement().getExpression());
				shifter.shiftSelectionUp();
				//getSelectedStatement().getExpression().shiftSelectionHigher();
				statementList_.repaint();
			}
	
		}; 
		*/
		
		Action leftAction = new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Shifter shifter = new Shifter(getSelectedStatement().getExpression());
				shifter.shiftSelectionBackward();
				//getSelectedStatement().getExpression().shiftSelectionLeft();
				statementList_.repaint();
			}
	
		}; 
		
		Action rightAction = new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Shifter shifter = new Shifter(getSelectedStatement().getExpression());
				shifter.shiftSelectionForward();
				//getSelectedStatement().getExpression().shiftSelectionRight();
				statementList_.repaint();
			}
	
		}; 
		
		Action testAction = new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Space bar was hit");
				Statement s = StatementPanel.this.getSelectedStatement();
				Expression selectedSubExpression = s.getExpression().getSelectedSubExpression();
				System.out.println("Selected SubExpression: " + selectedSubExpression);
			}
		};
		
		KeyStroke keyStroke;
		InputMap im = statementList_.getInputMap();
		
		keyStroke = KeyStroke.getKeyStroke("LEFT");
		statementList_.getActionMap().put(im.get(keyStroke), leftAction);
		keyStroke = KeyStroke.getKeyStroke("RIGHT");
		statementList_.getActionMap().put(im.get(keyStroke), rightAction);
		
		//NOTE FOR FUTURE REFERENCE - If you want to use other keystrokes, be aware some of these
		//won't be in the InputMap yet, so you have to add them.  For example, space bar is 
		//not automatically in the InputMap (i.e., no key binding is present), so if we want 
		//it to activate a testAction defined above, we have one additional step/line of 
		//code to put the keystroke and string into the input map (see below)
		String keyStrokeAndKey = "SPACE";
		keyStroke = KeyStroke.getKeyStroke(keyStrokeAndKey);
		statementList_.getInputMap().put(keyStroke, keyStrokeAndKey);
		statementList_.getActionMap().put(keyStrokeAndKey, testAction);
		
		statementList_.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				deselectAllStatements();
				if (statementList_.getSelectedIndex() != NOTHING_SELECTED) {
					Statement selectedStatement = statementList_.getSelectedValue();
					selectedStatement.getExpression().setSelected(true);
				}
			}});
		
		statementsScrollPane.setVerticalScrollBar(statementsScrollPane.createVerticalScrollBar());
		//statementsScrollPane.setPreferredSize(new Dimension(STATEMENT_SCROLLPANE_WIDTH,STATEMENT_SCROLLPANE_HEIGHT));
		this.add(statementsScrollPane);
		
		statementList_.addMouseListener(new PopClickListener(mainWindow_));
	}
	
	public Statement getSelectedStatement() {
		return statementList_.getSelectedValue();
	}
	
	public JList<Statement> getStatementList() {
		return statementList_;
	}
	
	public StatementListModel getStatements() {
		return statements_;
	}
	
	public void addStatement(Statement s) {
		statements_.addElement(s);
	}
	
	public void deselectAllStatements() {
		for (int i=0; i < statements_.size(); i++) {
			Statement statement = statements_.get(i);
			statement.getExpression().deselectRecursive();
		}
	}
	
	public void hideSelectedStatements() {
		for (Object selected : statementList_.getSelectedValuesList()) {
			((Statement) selected).setHidden(true);
		}
		update();
	}
	
	public void showHiddenStatements() {
		for (Object s : statements_.toArray()) {
			((Statement) s).setHidden(false);
		}
		update();
	}
	
	public void update() {
		statementList_.update(statementList_.getGraphics());
	}
}
