
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import acm.gui.TableLayout;


public class StatementPanel extends JPanel{
	
	private final int STATEMENT_PANEL_WIDTH = 460;
	private final int STATEMENT_PANEL_HEIGHT = 590;
    private final int STATEMENT_SCROLLPANE_WIDTH = 360;
	private final int STATEMENT_SCROLLPANE_HEIGHT = 580;

	private MainWindow mainWindow_;
	private StatementListModel statements_;
	private JList<Statement> statementList_;
	
	
	public StatementPanel(MainWindow mainWindow) {
		
		mainWindow_ = mainWindow_;
		statements_ = new StatementListModel();
		statementList_ = new JList<Statement>(statements_);
		
		this.setLayout(new TableLayout(1,1));
		this.setPreferredSize(new Dimension(STATEMENT_PANEL_WIDTH,STATEMENT_PANEL_HEIGHT));
		statementList_.setCellRenderer(new StatementListCellRenderer());
		JScrollPane statementsScrollPane = new JScrollPane(statementList_);
		statementList_.setFocusable(true);
		statementList_.requestFocus();
		
		Action shiftDownAction = new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				getSelectedStatement().getExpression().shiftSelectionDeeper();
				statementList_.repaint();
			}
			
		}; 
		
		Action shiftUpAction = new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				getSelectedStatement().getExpression().shiftSelectionHigher();
				statementList_.repaint();
			}
	
		}; 
		
		Action leftAction = new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				getSelectedStatement().getExpression().shiftSelectionLeft();
				statementList_.repaint();
			}
	
		}; 
		
		Action rightAction = new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				getSelectedStatement().getExpression().shiftSelectionRight();
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
		keyStroke = KeyStroke.getKeyStroke("shift DOWN");
		statementList_.getActionMap().put(im.get(keyStroke), shiftDownAction);
		keyStroke = KeyStroke.getKeyStroke("shift UP");
		statementList_.getActionMap().put(im.get(keyStroke), shiftUpAction);
		keyStroke = KeyStroke.getKeyStroke("LEFT");
		statementList_.getActionMap().put(im.get(keyStroke), leftAction);
		keyStroke = KeyStroke.getKeyStroke("RIGHT");
		statementList_.getActionMap().put(im.get(keyStroke), rightAction);
		
		//For DEBUGGING - Space bar activates a testAction defined above
		//note: additional step of putting keystroke and string to the input map
		//needed as this key binding is not present.
		String keyStrokeAndKey = "SPACE";
		keyStroke = KeyStroke.getKeyStroke(keyStrokeAndKey);
		statementList_.getInputMap().put(keyStroke, keyStrokeAndKey);
		statementList_.getActionMap().put(keyStrokeAndKey, testAction);
		
		statementList_.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				deselectAllStatements();
				Statement selectedStatement = statementList_.getSelectedValue();
				selectedStatement.getExpression().setSelected(true);
			}});
		
		statementsScrollPane.setVerticalScrollBar(statementsScrollPane.createVerticalScrollBar());
		statementsScrollPane.setPreferredSize(new Dimension(STATEMENT_SCROLLPANE_WIDTH,STATEMENT_SCROLLPANE_HEIGHT));
		this.add(statementsScrollPane);
	}
	
	public Statement getSelectedStatement() {
		return (Statement) statementList_.getSelectedValue();
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
