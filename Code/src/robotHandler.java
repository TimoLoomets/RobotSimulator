import org.json.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class robotHandler {
    private JSONObject robot;
    physicsHandler myPhysicsHandler;
    Float[] robotLocation;
    Float robotRotation;
    
    private int maxLength(Float[][] input) {//get the length of longest array in a 2d Float array
    	return Arrays.stream(input).mapToInt(row -> row.length).max().getAsInt();
    }
    private Float pyt(Float[] ab) {return (float) Math.sqrt(ab[0]*ab[0]+ab[1]*ab[1]);}
    private Float[] JSONToFloat(JSONArray jsonArray){//converts JSONArray to Float array.
        Float[] fData = new Float[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            fData[i] = (float) jsonArray.optDouble(i);
        }
        return fData;
    }
    private Float[][] clockwiser(Float[][] input){//return the inserted vectors sorted in a clockwise rotational order
    	Float[][] output = new Float[input.length][maxLength(input)];
    	double[] angles = new double[input.length];
    	
    	for(int i=0; i<input.length; i++) {
    		angles[i] = Math.atan2((double) input[i][0], (double) input[i][1]);
    	}
    	
    	Arrays.sort(angles); 
    	for(Float[] point:input) {
    		double angle = Math.atan2(point[0], point[1]);
    		int myIndex = 0;
    		while(angles[myIndex] != angle || output[myIndex][0] != null) {
    			myIndex++;
    		}

    		output[myIndex][0] = point[0];
    		output[myIndex][1] = point[1];
    	}
    	return output;
    }
    private int[][] floatToInteger2D(Float[][] input){//converts 2 dimensional Float array to a 2 dimensional int array (uses Math.round() to round the numbers).
        int[][] output = new int[input.length][maxLength(input)];
        for(int i=0; i<input.length; i++){
            for(int j=0; j<input[i].length; j++){
                output[i][j] = (int) Math.round(input[i][j]);
            }
        }
        return output;
    }
    private Float[] rotateVector(Float[] vector, Float radians)//rotates a vector by the given amount of radians.
    {
        Float[] result = new Float[2];
        result[0] = (float) (vector[0] * Math.cos(radians) - vector[1] * Math.sin(radians));
        result[1] = (float) (vector[0] * Math.sin(radians) + vector[1] * Math.cos(radians));
        return result;
    }
    private Float[] getLocation(String object) {//calculates the current location of a point using the initial location from the center of mass.
    	try {
			Float[] centerOfMassLocation = JSONToFloat(((JSONObject) robot.get("center of mass")).getJSONArray("location"));
			Float[] newVector = JSONToFloat(((JSONObject) robot.get(object)).getJSONArray("location"));
	    	for(int i=0; i<newVector.length; i++) {
	    		newVector[i] -= centerOfMassLocation[i];
	    	}
	    	newVector = rotateVector(newVector, -robotRotation);
	    	newVector = new Float[] {newVector[0] + robotLocation[0], newVector[1] + robotLocation[1]};
	    	return newVector;
		} catch (JSONException e) {
			e.printStackTrace();
			return new Float[] {null, null};
		}
    }
    private void frictionCalc() {
    	Iterator<?> keys = robot.keys();
    	while(keys.hasNext()) {
    		String key = (String)keys.next();
    		try {
				if (robot.get(key) instanceof JSONObject) {
					JSONObject obj = ((JSONObject)(robot.get(key)));
					if(obj.get("type") == "motor") {
						Float[] centerOfMassLocation = JSONToFloat(((JSONObject) robot.get("center of mass")).getJSONArray("location"));
						Float[] loc = JSONToFloat(obj.getJSONArray("location"));
				    	for(int i=0; i<loc.length; i++) {
				    		loc[i] -= centerOfMassLocation[i];
				    	}
						Float reactionForce = (Float) obj.get("reaction force");
						
						Float wheelFrict = (Float) obj.get("wheel friction");
						Float[] wheelDir = rotateVector(JSONToFloat(obj.getJSONArray("direction")),(float)(-robotRotation+(Math.PI/2)));
						wheelDir = new Float[]{wheelDir[0] * wheelFrict * reactionForce, wheelDir[1] * wheelFrict * reactionForce};
						myPhysicsHandler.frictionAdd(loc, wheelDir);
						
						Float motorFrict = (Float) obj.get("motor friction");
						Float[] motorDir = JSONToFloat(obj.getJSONArray("direction"));
						motorDir = new Float[]{motorDir[0] * motorFrict * reactionForce, motorDir[1] * motorFrict * reactionForce};
						myPhysicsHandler.frictionAdd(loc, motorDir);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
    	}
    }
    private Float[] motorPower(Float[] direction, Float power) {//changes the vector length by the given coefficient (used on power vectors with length 1).
    	Float[] output = new Float[direction.length];
    	for(int i=0; i<direction.length; i++) {
    		output[i] = direction[i] * power / (float) Math.sqrt(Math.pow(direction[0], 2) + Math.pow(direction[1], 2));
    	}
    	return output;
    }
    
    private Float dot_product(Float[] vector1, Float[] vector2) {//dot multiplies the given vectors.
		return vector1[0]*vector2[0] + vector1[1]*vector2[1];
	}
    
    private Float[] getRotationalSpeed(String object) {
    	try {
			Float[] centerOfMassLocation = JSONToFloat(((JSONObject) robot.get("center of mass")).getJSONArray("location"));
			Float[] objectLocation = JSONToFloat(((JSONObject) robot.get(object)).getJSONArray("location"));
			Float[] location = new Float[objectLocation.length];
			for(int i=0; i<objectLocation.length; i++) location[i] = objectLocation[i] - centerOfMassLocation[i];
			Float direction = (float) ( Math.atan2(location[0], location[1]) + robotRotation + Math.PI/2);
			Float rotationalSpeed = pyt(location) * myPhysicsHandler.getRotationSpeed();
			Float[] rotationalSpeedVector = rotateVector(new Float[] {0f, rotationalSpeed}, -direction);
			return rotationalSpeedVector;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
    }
    
    private void update() {//updates the robots location and rotation values using the physics handler.
    	robotLocation = myPhysicsHandler.getLocation();
    	robotRotation = myPhysicsHandler.getRotation();
    } 
    
    
    public robotHandler() {
    	buildReader myBuildReader = new buildReader();
    	try {
			robot = myBuildReader.reader2("Information/build.json").getJSONObject("robot");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
    	robotLocation = new Float[]{0.25f, 1.5f};//robots initial location in meters
        robotRotation = (float) Math.PI * 0f;//robots initial rotation in radians
        JSONObject centerOfMass;
		try {
			centerOfMass = (JSONObject) robot.get("center of mass");
			
			myPhysicsHandler = new physicsHandler((float) centerOfMass.getDouble("mass"), robotLocation, robotRotation, (float) centerOfMass.getDouble("rotational inertia"));
			frictionCalc();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		update();
    }
    
    
    public int[][] getDisplayPoints(int winWidth, int winHeight, int pixelsPerMeter){//returns the bodypoints of the robot ready to be displayed.
    	List<Float[]> pointsList = new ArrayList<Float[]>();//list for collecting all of the points.
    	Iterator keys = robot.keys();
		try {
			JSONObject centerOfMass = (JSONObject) robot.get("center of mass");
			JSONArray centerOfMassLocation = (JSONArray) centerOfMass.get("location");
			Float[] COM = {(float) centerOfMassLocation.optDouble(0), (float) centerOfMassLocation.optDouble(1)};//gets the location of the center of mass (all of the other points will be centered around it so that COM becomes {0,0}=.
			while(keys.hasNext()) {//iterates through the robot and picks out the sensors' and motors' locations.
				String key = (String)keys.next();
				if (robot.get(key).getClass() == JSONObject.class) {
					JSONObject currentObject = (JSONObject)robot.get(key);
					if(currentObject.getString("type").toString().trim().equals("motor") || currentObject.getString("type").toString().trim().equals("sensor")) {
						JSONArray location = currentObject.getJSONArray("location");
						Float[] loc = {(float) location.optDouble(0) - COM[0], (float) location.optDouble(1) - COM[1]};//calculates the object's locations from the center of mass.
						pointsList.add(loc);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		Float[][] pointsTemp = new Float[pointsList.size()][2];//temporary array used to convert the list to array and to reorder the points in a clockwise rotation.
		pointsList.toArray(pointsTemp);//copys list to array
		pointsTemp = clockwiser(pointsTemp);//sorts the points in the correct order so that the final shape would be drawn logically.
		for(int i=0; i<pointsTemp.length; i++) {//converts initial local coordinates to global coordinates.
			pointsTemp[i] = rotateVector(pointsTemp[i], -robotRotation);
			pointsTemp[i] = new Float[] {pointsTemp[i][0] + robotLocation[0], pointsTemp[i][1] + robotLocation[1]};
		}
    	Float[][] points = new Float[2][pointsTemp.length];//array in the format {x-points, y-points} that can be used in the fillPolygon command.
    	for(int i=0; i<pointsTemp.length; i++) {//converts meter based coordinates to pixel based coordinates.
    		points[0][i] = pointsTemp[i][0] * pixelsPerMeter;
    		points[1][i] = pointsTemp[i][1] * pixelsPerMeter;
    	}

        int[][] robotLoc = floatToInteger2D(points);//makes all the location number int so they could be displayed (uses Math.round()).
        for(int i=0; i<robotLoc[1].length; i++){//flips the y-axis so the 0,0 would be in the bottom left (makes it more logical to look at).
            robotLoc[1][i] = winHeight-robotLoc[1][i];
        }
    	return robotLoc;
    }
    
    
    public int[] getCOM(int winWidth, int winHeight, int pixelsPerMeter) {//get the global location of center of mass in pixels.
    	Float[] Loc = robotLocation.clone();
    	for(int i=0; i<Loc.length; i++) {
    		Loc[i] = Loc[i]*pixelsPerMeter;
    	}
    	int[] locOut = {(int) Math.round(Loc[0]), winHeight - (int) Math.round(Loc[1])};
    	return locOut;
    }
    
    
    public void move(Float time) {//move the robot using the previously given forces for the given amount of seconds.
    	myPhysicsHandler.positionCalc(time);
    	myPhysicsHandler.flush();//reset the forces.
    	update();
    }
    
    
    public void foo() {
		try {
			Float[] loc1 = getLocation("motor1");
			Float[] loc2 = getLocation("motor2");
	    	Float[] pow1 = motorPower(rotateVector(JSONToFloat((JSONArray)((JSONObject)robot.get("motor1")).getJSONArray("direction")), -robotRotation), 0.001f);
			Float[] pow2 = motorPower(rotateVector(JSONToFloat((JSONArray)((JSONObject)robot.get("motor2")).getJSONArray("direction")), -robotRotation), 0.00f);
	    	myPhysicsHandler.forceAdd(loc1, pow1); 
			myPhysicsHandler.forceAdd(loc2, pow2);
		} catch (JSONException e) {
			e.printStackTrace();
		}
    }
    
    
    public void motorWrite(String motor, int power) {
    	try {
	    	Float[] loc = getLocation(motor);
	    	Float[] speed = {getRotationalSpeed(motor)[0] + myPhysicsHandler.getSpeed()[0], getRotationalSpeed(motor)[1] + myPhysicsHandler.getSpeed()[1]};
	    	Float motorSpeed = dot_product(speed,rotateVector(JSONToFloat((JSONArray)((JSONObject)robot.get(motor)).getJSONArray("direction")), -robotRotation));
	    	Float rpm = Math.min((motorSpeed / (float) ((JSONObject)robot.get(motor)).get("wheel diameter")),(float) ((JSONObject)robot.get(motor)).get("max rpm"));
	    	Float maxrpm = (float) ((JSONObject)robot.get(motor)).get("max rpm");
	    	Float stallTorque = (float) ((JSONObject)robot.get(motor)).get("stall torque");
	    	Float[] pow = motorPower(rotateVector(JSONToFloat((JSONArray)((JSONObject)robot.get(motor)).getJSONArray("direction")), -robotRotation),(power/255) * stallTorque * (maxrpm - rpm) / maxrpm);
	    	myPhysicsHandler.forceAdd(loc, pow);
    	} catch (JSONException e) {
			e.printStackTrace();
		}
		//myPhysicsHandler.forceAdd(loc, force);
    }
    
    
    public Float[] getObjectLocation(String name) {
    	return getLocation(name);
    }
}