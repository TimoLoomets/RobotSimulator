import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class physicsHandler {
	private Float[] forceSum = {0f, 0f};
	private Float torqueSum = 0f;
	private List<Map<String,Float[]>> frictionForces = new  ArrayList<Map<String,Float[]>>();
	private Map<String, Float[]> frictionForces2;//storing friction (maybe)
	private Float[][][] frictionForces3;//array of locations and powers
	
	private Float mass;
	private Float[] centerOfMassLocation;
	private Float[] speed = {0f, 0f};
	private Float rotation;
	private Float rotationSpeed = 0f;
	private Float rotationalInertia = 0f;
	private Float g = 9.80665f;
	private List<Float[][]> frictions = new ArrayList<Float[][]>();

	public physicsHandler(Float imass, Float[] icenterOfMassLocation, Float irotation, Float irotationalInertia) {
		mass = imass;
		centerOfMassLocation = icenterOfMassLocation;
		rotation = irotation;
		rotationalInertia = irotationalInertia;
	}
	
	
	private Float pyt(Float[] a) {//returns the hypothenuse using Pythagorean theorem.
		return (float) Math.sqrt(Math.pow(a[0], 2) + Math.pow(a[1], 2));
	}
	private Float[] normalize(Float[] vector) {return new Float[] {vector[0] / pyt(vector), vector[1] / pyt(vector)};}
	private Float dot_product(Float[] vector1, Float[] vector2) {return vector1[0]*vector2[0] + vector1[1]*vector2[1];}
	private Float[] rotateVector(Float[] vector, Float radians)//rotates a vector by the given amount of radians.
    {
        Float[] result = new Float[2];
        result[0] = (float) (vector[0] * Math.cos(radians) - vector[1] * Math.sin(radians));
        result[1] = (float) (vector[0] * Math.sin(radians) + vector[1] * Math.cos(radians));
        return result;
    }
	private Float getSign(Float a) {return a / Math.abs(a);}
	private Float[] multiplyVector(Float[] a, Float[] b) {return new Float[]{a[0]*b[0], a[1]*b[1]};}
	private Float[] absVector(Float[] a) {return new Float[] {Math.abs(a[0]), Math.abs(a[1])};}
	private Float[] multiplyScalar(Float[] vector, Float scalar) {return new Float[] {vector[0]*scalar, vector[1]*scalar};}
	private Float[] addVector(Float[] a, Float[] b) {return new Float[] {a[0]+b[0], a[1]+b[1]};}
	
	
	private void frictionCalc() {
		for(Float[][] f : frictions) {
			Float[] location = f[0];
			Float[] direction = f[1];
			forceAdd(addVector(centerOfMassLocation,rotateVector(location, -rotation)), multiplyVector(normalize(addVector(multiplyScalar(rotateVector(location, (float)(rotation + Math.PI/2)), rotationSpeed), speed)), absVector(rotateVector(direction, rotation))));
		}
	}
	
	
	public void forceAdd(Float[] forceLocation,	Float[] force) {
		Float[] locationVector = {centerOfMassLocation[0] - forceLocation[0], centerOfMassLocation[1] - forceLocation[1]};
		for(int i=0; i<forceSum.length; i++) {//maybe instead use the vector component through the centre of mass???
			forceSum[i] += force[i];//multiplyVectors(force, absVector(normalize(locationVector)))[i];
		}
		//System.out.println(Arrays.toString(force));
		Float distance = pyt(locationVector);
		torqueSum -= rotateVector(force, (float) Math.atan2(locationVector[0], locationVector[1]))[0];
	}
	
	
	public void frictionAdd(Float[] location, Float[] direction) {
		//forceAdd(location, multiplyVector(normalize(addVector(multiplyScalar(rotateVector(location, (float)(rotation + Math.PI/2)), rotationSpeed), speed)), absVector(rotateVector(direction, rotation))));
		frictions.add(new Float[][] {location,direction});
	}
	
	
	public void positionCalc(Float time) {
		/*for(Map<String,Float[]> friction : frictionForces) {
			Set<String> keys = friction.keySet();
			for(String key : keys) {
				System.out.print(key);
				System.out.println(Arrays.toString(friction.get(key)));
			}
		}
		System.out.println(frictionForces);*/
		frictionCalc();//Add the force from friction forces.
		for(int i=0; i<centerOfMassLocation.length; i++) {
			centerOfMassLocation[i] = (float) (centerOfMassLocation[i] + speed[i]*time + (forceSum[i]/mass)*Math.pow(time, 2)/2);
			speed[i] = speed[i] + (forceSum[i]/mass) * time;		
		}
		
		rotation = (float) (rotation + rotationSpeed*time + torqueSum/rotationalInertia*Math.pow(time, 2)/2);
		rotationSpeed = rotationSpeed + torqueSum * time;
	}
	
	
	public void flush() {
		forceSum = new Float[]{0f, 0f};
		torqueSum = 0f;
	}
	public Float[] getForce() {
		return forceSum;
	}
	public Float getTorque() {
		return torqueSum;
	}
	public Float[] getLocation() {
		return centerOfMassLocation;
	}
	public Float getRotation() {
		return rotation;
	}
	public Float[] getSpeed() {
		return speed;
	}
	public Float getRotationSpeed() {
		return rotationSpeed;
	}
}
