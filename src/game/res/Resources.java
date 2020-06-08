package game.res;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Resources {

	public static BufferedImage[] colors;
	
	static {
		colors = new BufferedImage[4];
		colors[0] = loadImage("/blue.png");
		colors[1] = loadImage("/red.png");
		colors[2] = loadImage("/blueKing.png");
		colors[3] = loadImage("/redKing.png");
	}
	
	private static BufferedImage loadImage (String path) {
		try {
			return ImageIO.read(Class.class.getResource(path));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return null;
	}
}
