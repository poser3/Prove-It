import java.awt.Component;

import javax.swing.JList;

@SuppressWarnings("serial")
public class SubstitutionCellRenderer extends ExpressionListCellRenderer {
	public boolean reversed = false;
	
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		if (value instanceof OperatorExpression) {
			OperatorExpression opEx = (OperatorExpression) value;
			if (opEx.getOp().equals(Operators.named("="))) {
				StringBuilder sb = new StringBuilder();
				if (reversed) {
					sb.append(opEx.getArg(1).toLatex());
					sb.append(" = ");
					sb.append(opEx.getArg(0).toLatex());
				}
				else {
					sb.append(opEx.getArg(0).toLatex());
					sb.append(" = ");
					sb.append(opEx.getArg(1).toLatex());
				}
				return super.getListCellRendererComponent(list, sb.toString(), index, isSelected, cellHasFocus);
			}
		}
		
		return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			
	}
}
