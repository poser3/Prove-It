import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import acm.gui.TablePanel;

@SuppressWarnings("serial")
public class SketchPanel extends TablePanel {
	
	//Instance Variables
	private SketchCanvas sketchCanvas_;
	private JButton selectButton_;
	private JButton pointButton_;
	private JButton segmentButton_;
	private JButton circleButton_;
	private JButton lineButton_;
	private JButton rayButton_;
	private JButton constructMidpointButton_;
	private JButton intersectionButton_;
	
	private JButton jButtonFromResource(String pathToResource) {
        try {
        	Image image = ImageIO.read(new File(
        			SettingsReader.getSetting("image-path") + pathToResource));
        	ImageIcon imageIcon = new ImageIcon(image);
        	JButton jButton = new JButton(imageIcon);
        	jButton.setBorder( new EmptyBorder(0, 0, 0, 0) ); 
        	return jButton;
        }
        catch (IOException e) {
        	System.out.println("There was a problem loading the image");
        	return null;
        }
	}
	
	//Constructors
	public SketchPanel() {
		 //invoke super-class constructor to set rows and cols
		super(1,15);
		  
      //Setup select button
		selectButton_ = jButtonFromResource("select.png");
		selectButton_.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				sketchCanvas_.setMode(sketchCanvas_.SELECT_MODE);
			}
			
		});
		this.add(selectButton_);
		
	  //Setup point button
		pointButton_ = jButtonFromResource("draw_point.png");
		pointButton_.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				sketchCanvas_.setMode(sketchCanvas_.ADDING_POINT_MODE);
			}
			
		});
		this.add(pointButton_);
		
		//Setup segment button
		segmentButton_ = jButtonFromResource("draw_segment.png");
		segmentButton_.addActionListener( new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				sketchCanvas_.setMode(sketchCanvas_.ADDING_FIRST_POINT_MODE);
				sketchCanvas_.setElementBeingAdded(sketchCanvas_.SEGMENT);
			}
		});
		this.add(segmentButton_);
		
		//Setup circle button
		circleButton_ = jButtonFromResource("draw_circle.png");
		circleButton_.addActionListener( new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				sketchCanvas_.setMode(sketchCanvas_.ADDING_FIRST_POINT_MODE);
				sketchCanvas_.setElementBeingAdded(sketchCanvas_.CIRCLE);
			}
		});
		this.add(circleButton_);
		
		//Setup line button
		lineButton_ = jButtonFromResource("draw_line.png");
		lineButton_.addActionListener( new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				sketchCanvas_.setMode(sketchCanvas_.ADDING_FIRST_POINT_MODE);
				sketchCanvas_.setElementBeingAdded(sketchCanvas_.LINE);
			}
		});
		this.add(lineButton_);
		
		//Setup ray button
		rayButton_ = jButtonFromResource("draw_ray.png");
		rayButton_.addActionListener( new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				sketchCanvas_.setMode(sketchCanvas_.ADDING_FIRST_POINT_MODE);
				sketchCanvas_.setElementBeingAdded(sketchCanvas_.RAY);
			}
		});
		this.add(rayButton_);
		
		//Setup constructMidpointButton
		constructMidpointButton_ = jButtonFromResource("construct_midpoint.png");
		constructMidpointButton_.addActionListener( new ActionListener() {
					
			@Override
			public void actionPerformed(ActionEvent arg0) {
				sketchCanvas_.addMidpoint();
				sketchCanvas_.setMode(sketchCanvas_.SELECT_MODE);
			}
		});
		this.add(constructMidpointButton_);
		
		//Setup constructIntersectionButton
		intersectionButton_ = jButtonFromResource("construct_intersection.png");
		intersectionButton_.addActionListener( new ActionListener() {
					
			@Override
			public void actionPerformed(ActionEvent arg0) {
				sketchCanvas_.addIntersection();
			}
		});
		this.add(intersectionButton_);
	
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

}
