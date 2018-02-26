import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.Arrays;
import java.awt.image.ImageObserver;
import javax.swing.*;


import javax.swing.JPanel;

public class windowDraw extends JPanel{
    int[] xPoints = {};
    int[] yPoints = {};
    int nP = xPoints.length;//I didn't quite understand what this variable is for, but it seems to work like this.
    int[] Loc = {0,0};
    Image myTrack;
    int[] trackSize;
    JLabel timeLabel = new JLabel("1000", SwingConstants.LEFT);
    
    public windowDraw() {
    	setLayout(null);
    	timeLabel.setSize(100,100);
    	timeLabel.setLocation(0,0);
    	add(timeLabel);
    }

    //long start1 = System.currentTimeMillis();
    //long start2 = System.currentTimeMillis();
    
    public void refresh(int[][] points, int[] point, String time) {
    	xPoints = points[0];
    	yPoints = points[1];
    	nP = xPoints.length;
    	Loc = point;
    	timeLabel.setText(time);
    	/*long timeSince = System.currentTimeMillis() - start1;
    	System.out.printf("last refresh: %d \n", timeSince);
    	start1 = System.currentTimeMillis();*/
    }
    
    
    public void setTrack(Image track, int[] itrackSize) {
    	myTrack = track;
    	trackSize = itrackSize;
    	myTrack = myTrack.getScaledInstance(trackSize[0], trackSize[1], Image.SCALE_DEFAULT); 
    }
    
    
    protected void paintComponent(Graphics g) {
    	super.paintComponent(g);
    	
    	//myTrack = myTrack.getScaledInstance(trackSize[0], trackSize[1], Image.SCALE_DEFAULT); 
        g.drawImage(myTrack, 0, this.getHeight() - myTrack.getHeight(null), this);
        
        g.setColor(Color.RED);
        g.fillPolygon(xPoints,yPoints,nP);
        g.setColor(Color.BLUE);
        g.drawOval(Loc[0]-5, Loc[1]-5, 10, 10);
        
        timeLabel.setSize(40, 40);
        
        /*long timeSince = System.currentTimeMillis() - start2;
    	System.out.printf("last paint: %d \n", timeSince);
    	start2 = System.currentTimeMillis();*/
    }
}
