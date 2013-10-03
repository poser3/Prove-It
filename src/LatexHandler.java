import java.awt.Image;
import java.awt.Toolkit;
import java.net.MalformedURLException;
import java.net.URL;

import acm.graphics.GImage;

public class LatexHandler {
	
	private static String makeServerFriendly(String latex) {
		// Some processing of the raw latex will need to occur to get 
		// the server to be able to accept it.  In particular:
		// 
		// If we need any spaces, we will need to use "&space;" instead 
		
		String serverFriendlyLatex = latex;
		serverFriendlyLatex = serverFriendlyLatex.replaceAll(" ", "&space;");
		return serverFriendlyLatex;
	}
	
	/** 
	 * Uses the latex string given to it to create an image of the corresponding 
	 * "pretty mathematics" and returns this image.
	 * @param latex string containing raw latex to be turned into an image
	 * @return the image of the "pretty mathematics" described by the latex string
	 */
	public static Image latexToImage(String latex) {
		//TODO: what happens if it fails?  we need to return an "error image" that 
		//we don't need a network connection to retrieve (i.e., build it into the package
		//resources.)
		String baseUrl = "http://latex.codecogs.com/gif.latex?";
	
		URL url = null;
		try {
			url = new URL(baseUrl + makeServerFriendly(latex));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		java.awt.Image image = Toolkit.getDefaultToolkit().createImage(url);
		return image;
	}
	
	/** 
	 * Uses the latex string given to it to create an image of the corresponding 
	 * "pretty mathematics" and returns this image as a acm.graphics.GImage.
	 * @param latex string containing raw latex to be turned into an image
	 * @return the GImage of the "pretty mathematics" described by the latex string
	 */
	public static GImage latexToGImage(String latex) {
		Image image = latexToImage(latex);
		GImage gImage = new GImage(image);
		return gImage;
	}

}

