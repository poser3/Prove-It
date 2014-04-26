package edu.emory.prove_it;
import java.awt.Component;

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
					Expression result = OperatorExpression.make("=", opEx.getArg(1), opEx.getArg(0));
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
