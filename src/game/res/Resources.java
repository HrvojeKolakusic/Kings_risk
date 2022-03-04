package game.res;


import java.awt.image.BufferedImage;
//import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Resources {

	public BufferedImage[] colors;
	public BufferedImage[] numbers;
	
	public Resources() {
		colors = new BufferedImage[4];
		colors[0] = loadImage("/blue.png");
		colors[1] = loadImage("/red.png");
		colors[2] = loadImage("/blueKing.png");
		colors[3] = loadImage("/redKing.png");
		
		numbers = new BufferedImage[9];
		numbers[0] = loadImage("/1.png");
		numbers[1] = loadImage("/2.png");
		numbers[2] = loadImage("/3.png");
		numbers[3] = loadImage("/4.png");
		numbers[4] = loadImage("/5.png");
		numbers[5] = loadImage("/6.png");
		numbers[6] = loadImage("/7.png");
		numbers[7] = loadImage("/8.png");
		numbers[8] = loadImage("/9.png");
	}
	
	private BufferedImage loadImage (String path) {
		try {
			return ImageIO.read(Resources.class.getResource(path));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return null;
	}
}
