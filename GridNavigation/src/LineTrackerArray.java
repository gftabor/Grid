import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;

public class LineTrackerArray {
	EV3ColorSensor leftSensor = new EV3ColorSensor(SensorPort.S1);
	EV3ColorSensor rightSensor = new EV3ColorSensor(SensorPort.S3);
	private int leftMax;
	private int leftMin;
	private int rightMax;
	private int rightMin;
	private float[] currentValues={0,0};
	private double percentRight;
	private double percentLeft;
	private double percentAtCrossThreshhold =30;
	/**
	 * Reads line sensors updating the min and max values for both sensors
	 * to calibrate
	 */
	public void calibrate() {
		leftSensor.getRedMode().fetchSample(currentValues, 0);
		rightSensor.getRedMode().fetchSample(currentValues, 1);
		int currentRight = (int) (10000 * currentValues[1]);
		int currentLeft = (int) (10000 * currentValues[0]);
		if (currentRight > rightMax)
			rightMax = currentRight;
		if (currentRight < rightMin)
			rightMin = currentRight;
		if (currentLeft > leftMax)
			leftMax = currentLeft;
		if (currentLeft < leftMin)
			leftMin = currentLeft;
	}
	
	/**
	 * Returns difference between line trackers 
	 * 
	 * @return int
	 */
	public int getValue(){
		leftSensor.getRedMode().fetchSample(currentValues, 0);
		rightSensor.getRedMode().fetchSample(currentValues, 1);
		int currentRight = (int) (10000 * currentValues[1]);
		int currentLeft = (int) (10000 * currentValues[0]);
		percentRight=(100*(currentRight-rightMin))/(rightMax-rightMin);
		percentLeft=(100*(currentLeft-leftMin))/(leftMax-leftMin);
				
		int returnvalue= (int)((percentRight-percentLeft));	
		//LCD.drawInt(returnvalue, 0, 6);
		return returnvalue;	
		
	}
	public boolean atCross(){
		return(percentLeft<percentAtCrossThreshhold&&percentRight<percentAtCrossThreshhold);
	}
}
