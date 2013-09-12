package edu.emory.oxford.proofpad.sketch;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JTextArea;

import acm.gui.TablePanel;

@SuppressWarnings("serial")
public class SketchPanel extends TablePanel {
	
	//Instance Variables
	private SketchCanvas sketchCanvas_;
	private JTextArea  selectedTypesTextArea_;
	private JButton selectButton_;
	private JButton pointButton_;
	private JButton segmentButton_;
	private JButton circleButton_;
	private JButton lineButton_;
	private JButton rayButton_;
	private JButton constructMidpointButton_;
	private JButton intersectionButton_;
	
	//Constructors
	public SketchPanel() {
		
	  //invoke super-class constructor to set rows and cols
		super(9,1);
		  
      //Setup select button
		selectButton_ = new JButton("Select");
		selectButton_.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				sketchCanvas_.setMode(sketchCanvas_.SELECT_MODE);
			}
			
		});
		this.add(selectButton_);
		
	  //Setup point button
		pointButton_ = new JButton("Point");
		pointButton_.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				sketchCanvas_.setMode(sketchCanvas_.ADDING_POINT_MODE);
			}
			
		});
		this.add(pointButton_);
		
		//Setup segment button
		segmentButton_ = new JButton("Segment");
		segmentButton_.addActionListener( new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				sketchCanvas_.setMode(sketchCanvas_.ADDING_FIRST_POINT_MODE);
				sketchCanvas_.setElementBeingAdded(sketchCanvas_.SEGMENT);
			}
		});
		this.add(segmentButton_);
		
		//Setup circle button
		circleButton_ = new JButton("Circle");
		circleButton_.addActionListener( new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				sketchCanvas_.setMode(sketchCanvas_.ADDING_FIRST_POINT_MODE);
				sketchCanvas_.setElementBeingAdded(sketchCanvas_.CIRCLE);
			}
		});
		this.add(circleButton_);
		
		//Setup line button
		lineButton_ = new JButton("Line");
		lineButton_.addActionListener( new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				sketchCanvas_.setMode(sketchCanvas_.ADDING_FIRST_POINT_MODE);
				sketchCanvas_.setElementBeingAdded(sketchCanvas_.LINE);
			}
		});
		this.add(lineButton_);
		
		//Setup ray button
		rayButton_ = new JButton("Ray");
		rayButton_.addActionListener( new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				sketchCanvas_.setMode(sketchCanvas_.ADDING_FIRST_POINT_MODE);
				sketchCanvas_.setElementBeingAdded(sketchCanvas_.RAY);
			}
		});
		this.add(rayButton_);
		
		//Setup constructMidpointButton
		constructMidpointButton_ = new JButton("Midpoint");
		constructMidpointButton_.addActionListener( new ActionListener() {
					
			@Override
			public void actionPerformed(ActionEvent arg0) {
				sketchCanvas_.addMidpoint();
				sketchCanvas_.setMode(sketchCanvas_.SELECT_MODE);
			}
		});
		this.add(constructMidpointButton_);
		
		//Setup constructIntersectionButton
				intersectionButton_ = new JButton("Intersect");
				intersectionButton_.addActionListener( new ActionListener() {
							
					@Override
					public void actionPerformed(ActionEvent arg0) {
						sketchCanvas_.addIntersection();
						sketchCanvas_.setMode(sketchCanvas_.INTERSECTION_MODE);
					}
				});
				this.add(intersectionButton_);
		
		//Setup selectedTypesTextArea
	    selectedTypesTextArea_ = new JTextArea("Selected Types...");
	    this.add(selectedTypesTextArea_);
	  
	
	}
	
	//Getters and Setters
	public SketchCanvas getSketchCanvas() {
		return sketchCanvas_;
	}
	
	public void setSketchCanvas(SketchCanvas sketchCanvas) {
		sketchCanvas_ = sketchCanvas;
	}
	
	public void setMidpointButtonEnabled(boolean enabled) {
		constructMidpointButton_.setEnabled(enabled);
	}
	
	public void setSelectedTypesText(String s) {
		selectedTypesTextArea_.setText(s);
	}

}
