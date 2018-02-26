import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

public class trackHandler {
	private BufferedImage myTrack;
	private byte[][] darkness;
	private static Float pixelsPerMeter;
	
	
	private static int byteToUnsignedInt(byte b) {
		return b & 0xFF;
		}
	private static int[] locToAdress(Float[] loc) {
		int[] adr = new int[loc.length];
		for(int i=0; i<loc.length; i++) {
			adr[i] = Math.round(loc[i] * pixelsPerMeter);
		}
		return adr;
	}
	
	
	public trackHandler() {
		try {
			myTrack = ImageIO.read(new File("Information/rada_V1.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		pixelsPerMeter = myTrack.getHeight() / 3f;
	}
	
	
	public void darknessCalc() {
		darkness = new byte[myTrack.getWidth()][myTrack.getHeight()];
		for(int i=0; i<myTrack.getHeight(); i++) {
			for(int j=0; j<myTrack.getWidth(); j++) {
				darkness[j][myTrack.getHeight() - i - 1] = (byte) new Color(myTrack.getRGB(j, i)).getRed();
			}
		}
		System.out.println("done");
	}
	
	
	public BufferedImage getTrack() {
		return myTrack;
	}
	public Image getTrackImage() {
		Image myImage = null;
		try {
			myImage = ImageIO.read(new File("Information/rada_V1.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return myImage;
	}
	public int getLight(Float[] location) {
		int[] adr = locToAdress(location);
		int light;
		try {
			light = byteToUnsignedInt(darkness[adr[0]][adr[1]]);
		}
		catch(IndexOutOfBoundsException e) {
			System.out.println("OUT OF FIELD ERROR");
			light = 255;
		}
		return light;
	}
}
