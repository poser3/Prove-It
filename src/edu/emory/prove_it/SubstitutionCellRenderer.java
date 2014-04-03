package edu.emory.prove_it;
import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JList;

import edu.emory.prove_it.expression.Expression;
import edu.emory.prove_it.expression.OperatorExpression;
import edu.emory.prove_it.expression.Operators;
import edu.emory.prove_it.expression.Statement;
import edu.emory.prove_it.statement_panel.StatementListCellRenderer;

public class SubstitutionCellRenderer extends StatementListCellRenderer {
	public boolean reversed = false;
	
	@Override
	public Component getListCellRendererComponent(JList<? extends Statement> list, Statement value, int index, boolean isSelected, boolean cellHasFocus) {
		if (value.getExpression() instanceof OperatorExpression) {
			OperatorExpression opEx = (OperatorExpression) value.getExpression();
			
			if (opEx.getOp().equals(Operators.named("="))) {
				if (reversed) {
					ArrayList<Expression> args = new ArrayList<Expression>();
					args.add(opEx.getArg(1));
					args.add(opEx.getArg(0));
					Expression result = new OperatorExpression(Operators.named("="), args);
					return super.getListCellRendererComponent(list,
							new Statement(result, value.logicParents(), value.geometryParents()),
							index, isSelected, cellHasFocus);
				} else {
					return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				}
			}
		}
		
		return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			
	}
}
