
public class codeHandler {
	private Boolean ready = false;
	
	
	private int getSensorValue(String sensor) {
		return 0;
	}
	
	
	private void motorWrite(String motor, int power) {
		
	}
	
	public void runCode() {
		int[] sensor_values = {-8,-7,-6,-5,-4,-3,-2,-1,1,2,3,4,5,6,7,8};
		String[] sensors = {"sensor15","sensor14","sensor13","sensor12","sensor11","sensor10","sensor9","sensor8","sensor0","sensor1","sensor2","sensor3","sensor4","sensor5","sensor6","sensor7"};
		ready = true;
	}
	
	public Boolean getReady() {
		return ready;
	}
	public void setReady(Boolean state) {
		ready = state;
	}
}
