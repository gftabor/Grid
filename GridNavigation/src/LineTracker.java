import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.MoveController;
import lejos.robotics.navigation.Navigator;
import lejos.utility.Delay;

public class LineTracker {

	RegulatedMotor left = new EV3LargeRegulatedMotor(MotorPort.C);
	RegulatedMotor right = new EV3LargeRegulatedMotor(MotorPort.B);
	DifferentialPilot drive = new DifferentialPilot(
	MoveController.WHEEL_SIZE_NXT1, 12, left, right);
	Navigator navigate = new Navigator(drive);
	LineTrackerArray LineArray = new LineTrackerArray();
	EV3UltrasonicSensor ultrasonic = new EV3UltrasonicSensor(SensorPort.S4);
	double kp = 1.2;
	int waitCall=0;
	float smallestUltra=0.0f;
	
	
	/**
	 * Initializes and calibrates the line Sensors by spinning around the line  
	 */
	public void calibrate() {
		ultrasonic.enable();
		drive.setRotateSpeed(75);

		drive.rotate(90, true);
		while (drive.isMoving()) {
			LineArray.calibrate();
			Delay.msDelay(10);
		}
		drive.rotate(-180, true);
		while (drive.isMoving()) {
			LineArray.calibrate();
			Delay.msDelay(10);
		}
		turn(true);
	}
	
	/**
	 * Tracks line follower forward until the next intersection
	 */
	public void trackUntilCross() {
		drive.setTravelSpeed(15);
		do{ 	
			drive.steer((int) (kp*LineArray.getValue())); 
			Delay.msDelay(10); 
			//LCD.drawString(Boolean.toString(LineArray.atCross()), 0, 1);
		}while(!LineArray.atCross());
	}
	
	/**
	 * Moves foward off to center on intersection
	 */
	public void forwardOffCross(){
		drive.travel(5);//5 cm forward of travel
		while(drive.isMoving()){
			Delay.msDelay(10);
		}
	}
	
	/**
	 * Polls the ultrasonic sensor updating the samllestUltra with the smallest value seen
	 */
	public void logUltra(){
		float[] distance = new float[1];
		ultrasonic.getDistanceMode().fetchSample(distance, 0);
		float realDistance= distance[0];
		if(realDistance<smallestUltra){
			smallestUltra=realDistance;
		}
		if(smallestUltra==0.0){
			smallestUltra=realDistance;
		}
	}
	
	/**Turns robot to next line in the direction chosen
	 * 
	 * 
	 * @param boolean True for left, False for right 
	 */
	public void turn(boolean direction){
		if(direction){
			drive.rotate(55);//get off line
			Delay.msDelay(50);
			drive.rotateLeft(); 
		}
		if(!direction){
			drive.rotate(-55);//get off line 
			Delay.msDelay(50);
			drive.rotateRight();			
		}		
		while(Math.abs(LineArray.getValue())<20){//while  values are within 20% of eachother
			Delay.msDelay(10);//means neither has hit line yet
			//logUltra();
			//System.out.println("on edge");
		}
		while(Math.abs(LineArray.getValue())>10){//gets off edge to center on line
			Delay.msDelay(10);
			logUltra();
			//System.out.println("centered");
		}
		drive.stop();		
	}
	/**Returns the float that is the smallest ultrasonic value seen
	 * and resets the smallest seen value to 0
	 * 
	 * @return float that is smallest value seen this 
	 */
 public float getSmallestUltra(){
	 float storeSmallestUltra=smallestUltra;
	 smallestUltra=0.0f;
	 return storeSmallestUltra;	 
 }	
}


