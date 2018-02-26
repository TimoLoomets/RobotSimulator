import java.awt.Image;
import javax.swing.JFrame;


public class windowMaker extends JFrame /*implements Runnable*/{
	protected windowDraw myWindowDraw = new windowDraw();
    
    
    
    public windowMaker(Image track, int pixelsPerMeter){
        super();
        myWindowDraw.setTrack(track, new int[] {(int) Math.round(1.5 * pixelsPerMeter), Math.round(3 * pixelsPerMeter)});
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1000, 1000);
        this.setTitle("Simulator");
        this.setContentPane(myWindowDraw);
        this.setVisible(true);
        
    }
    

    /*public void run() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }*/
    
    public void refresh(int[][] displayPoints, int[] Location, String time) {
    	myWindowDraw.refresh(displayPoints, Location, time);
    	this.repaint();
    }
}
