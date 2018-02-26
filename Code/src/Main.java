import java.io.FileNotFoundException;
import java.util.Arrays;

import org.json.JSONException;

public class Main {
	static robotHandler myRobotHandler;
    static trackHandler myTrackHandler;
    static codeHandler myCodeHandler;
    static int pixelsPerMeter = 333;
    
    private int getSensorValue(String sensor) {
    	Float[] location = myRobotHandler.getObjectLocation(sensor);
		return myTrackHandler.getLight(location);
	}
	
	
	private void motorWrite(String motor, Integer power) {
		myRobotHandler.motorWrite(motor, power);
	}
    
    private void code() {
    	Float P = 0f;
    	Float I = 0f;
    	Float D = 0f;
    	Float integralSum = 0f;
    	Float lastLocation = 0f;
    	int baseSpeed = 128;
    	while(true) {
    		int[] sensor_values = {-8,-7,-6,-5,-4,-3,-2,-1,1,2,3,4,5,6,7,8};
    		String[] sensors = {"sensor15","sensor14","sensor13","sensor12","sensor11","sensor10","sensor9","sensor8","sensor0","sensor1","sensor2","sensor3","sensor4","sensor5","sensor6","sensor7"};
    		Float lineLocation = 0f;
    		int sensorCount = 0;
    		for(int i=0; i<16; i++) {
    			lineLocation += (getSensorValue(sensors[i])%128)*sensor_values[i];
    			sensorCount += getSensorValue(sensors[i])%128;
    		}
    		lineLocation /= sensorCount;
    		integralSum += lineLocation;
    		Float lineChange = lineLocation - lastLocation;
    		Float P_Value = P * lineLocation;
    		Float I_Value = I * integralSum;
    		Float D_Value = D * lineChange;
    		Float turn = P_Value + I_Value + D_Value;
    		motorWrite("motor1", Math.max(Math.min(Math.round(baseSpeed-turn), 255), 0));
    		motorWrite("motor2", Math.max(Math.min(Math.round(baseSpeed+turn), 255), 0));
    		lastLocation = lineLocation;
    	}
    }

	public static void main(String[] args) throws FileNotFoundException, JSONException {
		myRobotHandler = new robotHandler();
        myTrackHandler = new trackHandler();
		windowMaker mainWindow = new windowMaker(myTrackHandler.getTrackImage(), pixelsPerMeter);
        //new Thread(mainWindow, "Window").start();
        //myTrackHandler.darknessCalc();
        Float time = 0.00001f;
        Float timeTotal = 0f;
        while(true) {
        	//while(!myCodeHandler.getReady()) {}
        	//myCodeHandler.setReady(false);
        	myRobotHandler.foo();
        	myRobotHandler.move(time);
        	timeTotal += time;
			int[][] displayPoints = myRobotHandler.getDisplayPoints(mainWindow.getWidth(), mainWindow.getHeight(), pixelsPerMeter);
			int[] Location = myRobotHandler.getCOM(mainWindow.getWidth(), mainWindow.getHeight(), pixelsPerMeter);
			mainWindow.refresh(displayPoints, Location, timeTotal.toString());
			//System.out.println(Arrays.toString(Location));//proves that while the physics and input are refreshing, the GUI is not changing despite repaint being called with new values.
        }
	}
}
