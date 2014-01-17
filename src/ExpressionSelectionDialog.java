import java.awt.Color;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import acm.gui.TableLayout;

/**
 * @author Lee Vian
 *
 */
@SuppressWarnings("serial")
public class ExpressionSelectionDialog extends JDialog {
	
	private final ArrayList<ArrayList<ExpressionButton>> buttons = new ArrayList<ArrayList<ExpressionButton>>();
	private final JButton okButton = new JButton("OK");
	private final JButton cancelButton = new JButton("Cancel");
	private final TableLayout layout;
	
	private boolean clickedOK = false;
	private LinkedList<ExpressionButton> selected = new LinkedList<ExpressionButton>(); // treated as a stack
	
	private final ActionListener buttonListener = new ActionListener() {
		public void actionPerformed(ActionEvent event) {
			JButton source = (JButton) event.getSource();
			ExpressionButton clickedOn = null;
			int r=0, c=0;
			for (r=0; clickedOn==null && r < buttons.size(); r++) {
				ArrayList<ExpressionButton> row = buttons.get(r);
				for (c=0; clickedOn==null && c < row.size(); c++)
					clickedOn = row.get(c).getButton().equals(source) ? row.get(c) : null;
			}
			setTitle(clickedOn.getExpression().toString());
			
			// Don't do anything if the user clicked again on the most recently selected button
			if (selected.peek().equals(clickedOn))
				return;
			
			// Back up until we're on the same row as the button that was clicked
			for (int currentRow = buttons.size()-1; currentRow > r; currentRow--) {
				selected.pop().setActive(false);
				buttons.remove(currentRow);
			}
			
			// If the clicked-on button was not already active, activate it
			if (! selected.peek().equals(clickedOn)) {
				selected.pop().setActive(false);
				clickedOn.setActive(true);
				selected.push(clickedOn);
			}
			
			// Add a new row with the subexpressions of the newly selected button
			Expression exp = clickedOn.getExpression();
			if (exp instanceof OperatorExpression) {
				OperatorExpression oExp = (OperatorExpression) exp;
				ArrayList<ExpressionButton> subExpressions = new ArrayList<ExpressionButton>();
				for (int i=0; i<oExp.getNumArgs(); i++)
					subExpressions.add(new ExpressionButton(oExp.getArg(i)));
				
				buttons.add(subExpressions);
			}
			
			update();
		}
	};
	private final ActionListener okCancelListener = new ActionListener() {
		public void actionPerformed(ActionEvent event) {
			clickedOK = event.getSource().equals(okButton);
			setVisible(false);
		}
	};
	
	public ExpressionSelectionDialog(final OperatorExpression exp) {
		super();
		setTitle(exp.toString());
		setSize(800, 600);
		layout = new TableLayout(2, 2);
		setLayout(layout);
		setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
		
		okButton.addActionListener(okCancelListener);
		cancelButton.addActionListener(okCancelListener);
		
		selected.push(new ExpressionButton(exp));
		ArrayList<ExpressionButton> subExpressions = new ArrayList<ExpressionButton>();
		for (int i=0; i < exp.getNumArgs(); i++)
			subExpressions.add(new ExpressionButton(exp.getArg(i)));
		buttons.add(subExpressions);
		update();
	}
	
	public Expression getSelected() {
		return clickedOK ? selected.peek().getExpression() : null;
	}
	
	private void update() {
		getContentPane().removeAll();
		int rows = buttons.size()+1, columns = 2;
		for (ArrayList<ExpressionButton> row : buttons)
			if (row.size() > columns)
				columns = row.size();
		layout.setRowCount(rows);
		layout.setColumnCount(columns);
		
		for (int r=0; r < rows-1; r++) {
			ArrayList<ExpressionButton> row = buttons.get(r);
			int c = 0;
			for(; c<row.size(); c++)
				add(row.get(c));
			for(; c < columns; c++)
				add(new JLabel());
		}
		
		for (int c=0; c < columns; c++) {
			// If there are an even number of columns, put the OK and cancel buttons in the middle.
			// If there are an odd number of columns, put a one-button gap between them.
			if (c == (columns-2)/2)
				add(okButton);
			else if (c == (columns+1)/2)
				add(cancelButton);
			else
				add(new JLabel());
		}
		
		validate();
		paint(getGraphics());
	}
	
	private class ExpressionButton extends JPanel {
		private final Expression expression;
		private final JButton button;
		
		public ExpressionButton(Expression exp) {
			super();
			expression = exp;
			button = new JButton(exp.toString());
			button.addActionListener(buttonListener);
			add(button);
		}
		
		public Expression getExpression() {
			return expression;
		}
		public JButton getButton() {
			return button;
		}
		
		public void setActive(boolean active) {
			if (active)
				setBorder(BorderFactory.createLineBorder(Color.BLACK));
			else
				setBorder(null);
		}
	}
	
}
