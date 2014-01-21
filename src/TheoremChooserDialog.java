import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import acm.gui.TableLayout;

@SuppressWarnings("serial")
public class TheoremChooserDialog extends JDialog {
	
	private final DefaultListModel<Theorem> theorems = new DefaultListModel<Theorem>();
	private final DefaultListModel<Statement> hypotheses = new DefaultListModel<Statement>();
	private final DefaultListModel<Statement> conclusions = new DefaultListModel<Statement>();
	private final JList<Theorem> theoremsList = new JList<Theorem>(theorems);
	private final JList<Statement> hypothesesList = new JList<Statement>(hypotheses);
	private final JList<Statement> conclusionsList = new JList<Statement>(conclusions);
	private final JButton okButton = new JButton("OK");
	private final JButton cancelButton = new JButton("Cancel");
	private final TableLayout layout = new TableLayout(7, 2, 10, 10);
	
	private boolean clickedOK = false;
	
	public TheoremChooserDialog(MainWindow owner) {
		super();
		setLayout(layout);
		setSize(800, 600);
		setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
		
		Theorem.loadTheorems();
		for (Theorem t : Theorem.theorems)
			theorems.addElement(t);
		
		theoremsList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent event) {
				update((Theorem) theoremsList.getSelectedValue());
			}
		});		
		hypothesesList.setCellRenderer(new StatementListCellRenderer());
		conclusionsList.setCellRenderer(new StatementListCellRenderer());
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				clickedOK = true;
				setVisible(false);
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				clickedOK = false;
				setVisible(false);
			}
		});
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridwidth = 2;
		constraints.weightx = 1;
		
		add(new JLabel("Theorems"), "gridwidth=2");
		add(theoremsList, constraints);
		add(new JLabel("Hypotheses"), "gridwidth=2");
		add(hypothesesList, constraints);
		add(new JLabel("Conclusions"), "gridwidth=2");
		add(conclusionsList, constraints);
		add(okButton, "weightx=1");
		add(cancelButton, "weightx=1");
		
		theoremsList.setSelectedIndex(0);
		update((Theorem) theoremsList.getSelectedValue());
	}
	
	private void update(Theorem selected) {
		hypotheses.clear();
		conclusions.clear();
		for (Statement s : selected.hypotheses)
			hypotheses.addElement(s);
		for (Statement s : selected.conclusions)
			conclusions.addElement(s);
	}
	
	public Theorem getSelected() {
		return clickedOK ? (Theorem) theoremsList.getSelectedValue() : null;
	}

}

