import java.awt.Component;
import java.util.ArrayList;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

public class SubstitutionCellRenderer extends StatementListCellRenderer {
	public boolean reversed = false;
	
	@Override
	public Component getListCellRendererComponent(JList<? extends Statement> list, Statement value, int index, boolean isSelected, boolean cellHasFocus) {
		if (value.getExpression() instanceof OperatorExpression) {
			OperatorExpression opEx = (OperatorExpression) value.getExpression();
			if (opEx.getOp().equals(Operator.named("="))) {
				if (reversed) {
					ArrayList<Expression> args = new ArrayList<Expression>();
					args.add(opEx.getArg(1));
					args.add(opEx.getArg(0));
					Expression result = new OperatorExpression(Operator.named("="), args);
					return super.getListCellRendererComponent(list, new Statement(result), index, isSelected, cellHasFocus);
				} else {
					return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				}
			}
		}
		
		return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			
	}
}

