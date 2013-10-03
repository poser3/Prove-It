import java.awt.Image;

import acm.graphics.GImage;
import acm.program.GraphicsProgram;


public class LatexExample extends GraphicsProgram {
	
	public void run() {
		
		final int NUM_OF_LATEX_STRINGS = 6;
		final int V_OFFSET = 18;
		final int H_OFFSET = 15;
		final int MIN_WIDTH = 100;
		
		this.setSize(WIDTH,HEIGHT);

		//let's define some latex strings (notice the "\" chars are escaped)
		String[] latexStrings = new String[NUM_OF_LATEX_STRINGS];
		latexStrings[0] = "\\bigtriangleup ABC \\cong \\bigtriangleup ABC";
		latexStrings[1] = "ax^2 + bx + c = 0";
		latexStrings[2] = "\\overleftrightarrow{AC} \\textrm{ contains } B";
		latexStrings[3] = "\\frac{AB}{DE} = \\frac{AC}{DF}";
		latexStrings[4] = "2 + {\\color{Blue}\\frac{\\sqrt{x^2+1}}{x-1}} = 7";
		latexStrings[5] = "m \\angle ABC = m \\angle ABD + m \\angle DBC";
		
		//if we just wanted an Image, we call this...
		Image image = LatexHandler.latexToImage(latexStrings[0]);
		
		//if we wanted to play with GImages, instead...
		GImage[] gImages = new GImage[NUM_OF_LATEX_STRINGS];
		double maxWidth = 100;
		double y = V_OFFSET;
		for (int i = 0; i < latexStrings.length; i++) {
			gImages[i] = LatexHandler.latexToGImage(latexStrings[i]);
			this.add(gImages[i],H_OFFSET,y);
			y += gImages[i].getHeight() + V_OFFSET;
			maxWidth = Math.max(maxWidth, gImages[i].getWidth());
		}
		
		this.setSize((int)(maxWidth + 2*H_OFFSET), (int) (y + 2*V_OFFSET));
		
	}

}

