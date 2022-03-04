package game.res;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Resources {

	public static BufferedImage[] colors;
	public static BufferedImage[] numbers;
	
	static {
		colors = new BufferedImage[4];
		colors[0] = loadImage("src/res/blue.png");
		colors[1] = loadImage("src/res/red.png");
		colors[2] = loadImage("src/res/blueKing.png");
		colors[3] = loadImage("src/res/redKing.png");
		
		numbers = new BufferedImage[9];
		numbers[0] = loadImage("src/res/1.png");
		numbers[1] = loadImage("src/res/2.png");
		numbers[2] = loadImage("src/res/3.png");
		numbers[3] = loadImage("src/res/4.png");
		numbers[4] = loadImage("src/res/5.png");
		numbers[5] = loadImage("src/res/6.png");
		numbers[6] = loadImage("src/res/7.png");
		numbers[7] = loadImage("src/res/8.png");
		numbers[8] = loadImage("src/res/9.png");
	}
	
	private static BufferedImage loadImage (String path) {
		try {
			return ImageIO.read(new File(path));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return null;
	}
}
