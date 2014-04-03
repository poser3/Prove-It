package edu.emory.prove_it.theorem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class TheoremMenuActionListener implements ActionListener {
	
	private TheoremPanel theoremPanel_;
	private Theorem theorem_;
	
	public TheoremMenuActionListener(TheoremPanel theoremPanel, Theorem theorem) {
		theoremPanel_ = theoremPanel;
		theorem_ = theorem;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		theoremPanel_.setCurrentTheorem(theorem_);
		System.out.println(theoremPanel_); //for DEBUGGING, shows current theorem 
	}

}
