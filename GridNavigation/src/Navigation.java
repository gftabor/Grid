import java.io.IOException;
import java.util.ArrayList;

import lejos.utility.Delay;

public class Navigation {
	final boolean LEFT = true;
	final boolean RIGHT = false;
	private int lastX = 0;
	private int lastY = 0;
	private int currentX = 0;
	private int currentY = 0;
	final private int NORTH = 0;
	final private int SOUTH = 2;
	final private int EAST = 1;
	final private int WEST = 3;
	private int lookingDirection = NORTH;
	public ArrayList<Node> selectedPath = new ArrayList<Node>();
	public Node[][] map = new Node[10][5];
	public ArrayList<Node> unBlockedNodes = new ArrayList<Node>(50);
	public CommunicationEvanIsMean Communicator;

	private void createGridMap() {
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 5; j++) {
				map[i][j] = new Node(i, j);
				unBlockedNodes.add(map[i][j]);
			}
		}
		System.out.println(unBlockedNodes.size());
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 5; j++) {
				if (i != 0) {
					map[i][j].addEdge(map[i - 1][j]);
				}
				if (j != 0) {
					map[i][j].addEdge(map[i][j - 1]);
				}
				if (i != 9) {
					map[i][j].addEdge(map[i + 1][j]);
				}
				if (j != 4) {
					map[i][j].addEdge(map[i][j + 1]);
				}
			}
		}
	}

	public void wipeGridIntersection(Node intersection) {
		for (Node j : intersection.getConnections()) {// for very edge in the
			// blocked node
			j.removeEdge(intersection); // delete the blocked node from its list
		}
		System.out.println(unBlockedNodes.size());
		unBlockedNodes.remove(intersection);// deletes blocked intersection
		// from overall list of
		// intersections
		System.out.println(unBlockedNodes.size());		
	}

	LineTracker Tracker = new LineTracker();

	public void initialize(CommunicationEvanIsMean givenCommunicator) throws IOException {
		Communicator=givenCommunicator;
		createGridMap();
		Tracker.calibrate();
		goForwardGridIntersections(0);// get to intersection
	}

	public void goForwardGridIntersections(int intersectionsToSkip) {

		for (int i = 0; i <= intersectionsToSkip; i++) {// goes n+1
			// intersections forward
			Tracker.trackUntilCross();
			Tracker.forwardOffCross();
		}
	}

	public void move() throws IOException {
		lastX = currentX;
		lastY = currentY;
		goForwardGridIntersections(0);
		if (lookingDirection == NORTH)
			currentY++;
		if (lookingDirection == SOUTH)
			currentY--;
		if (lookingDirection == EAST)
			currentX++;
		if (lookingDirection == WEST)
			currentX--;
		Communicator.sendPacket(lookingDirection, currentX, currentY,lastX,lastY);
	}

	public void face(int direction) throws IOException {
		if ((direction - 1 == lookingDirection)
				|| (lookingDirection == WEST && direction == NORTH))
			Tracker.turn(RIGHT);
		if ((direction + 1 == lookingDirection)
				|| (lookingDirection == NORTH && direction == WEST))
			Tracker.turn(LEFT);
		if (lookingDirection - direction == 2
				|| lookingDirection - direction == -2){
			Tracker.turn(LEFT);
			Tracker.turn(LEFT);
		}
		lookingDirection = direction;
		Communicator.sendPacket(lookingDirection, currentX, currentY,lastX,lastY);
	}

	private boolean frontClear() throws IOException {
		Tracker.logUltra();
		float realDistance = Tracker.getSmallestUltra();
		System.out.println(realDistance);

		if (realDistance == 0.0f) {
			Tracker.logUltra();
			realDistance = Tracker.getSmallestUltra();
		}
		if (realDistance < 0.13) {
			Communicator.sendPacket(-1, nextX(), nextY(),lastX,lastY);
			Node blockedNode = map[nextX()][nextY()];
			wipeGridIntersection(blockedNode);
			return false;
		} else {
			return true;
		}
	}

	private int nextX() {
		int nextx = currentX;
		if (lookingDirection == EAST)
			nextx++;
		if (lookingDirection == WEST)
			nextx--;
		return nextx;
	}

	private int nextY() {
		int nexty = currentY;
		if (lookingDirection == NORTH)
			nexty++;
		if (lookingDirection == SOUTH)
			nexty--;
		return nexty;
	}

	public void followPath() throws IOException {
		for (int i = selectedPath.size() - 1; i >= 0; i--) {
			int stepGoalY = selectedPath.get(i).getY();
			int stepGoalX = selectedPath.get(i).getX();
			if (currentY < stepGoalY)
				face(NORTH);
			if (currentY > stepGoalY)
				face(SOUTH);
			if (currentX < stepGoalX)
				face(EAST);
			if (currentX > stepGoalX)
				face(WEST);
			if (frontClear()) {
				move();
			} else {
				break;
			}
		}
	}

	public void createPath(int finalXGoal, int finalYGoal) {
		while(!selectedPath.isEmpty()){selectedPath.remove(0);}
		Node currentlyViewing = map[finalXGoal][finalYGoal];
		Node source = map[currentX][currentY];
		while (currentlyViewing != source) {
			selectedPath.add(currentlyViewing);// adds goal to the path
			currentlyViewing = currentlyViewing.getArrow();// follows path 1
			// closer to source
		}
		System.out.println("shortest "+selectedPath.size());
	}
	public Node minDistance(ArrayList<Node> unprocessedNodes)	{
		// Initialize min value
		int min = 10000;
		Node smallestDistanceNode = null;
		for(Node node:unprocessedNodes){
			if(node.getDistance()<min){
				smallestDistanceNode=node;
				min=node.getDistance();
			}			
		}	 
		return smallestDistanceNode;
	}

	public void computePath() {
		System.out.println("size"+ unBlockedNodes.size());
		ArrayList<Node> unprocessedNodes=new ArrayList<Node>(50);
		unprocessedNodes.addAll(unBlockedNodes);
		for (Node j : unprocessedNodes) {
			j.reset();// clears past distance related values
		}
		map[currentX][currentY].setDistance(0);//set distance to itself to 0
		while(!unprocessedNodes.isEmpty()){//need to figure out the order but know how many times to iterate
			Node processing=minDistance(unprocessedNodes);
			System.out.println("processing" +processing);
			ArrayList<Node> connections=processing.getConnections();
			for(Node edge:connections){
				if(processing.getDistance()+1<edge.getDistance()){
					edge.setDistance(processing.getDistance()+1);//set adjacent vertix's distance 
					edge.setArrow(processing);//set adjacent vertix's arrow pointing to processing 
				}
			}
			unprocessedNodes.remove(processing);//set node as finished			
		}
	}

	public void followGui() throws IOException {
		while (true) {
			Communicator.recievePacket();
			int x=Communicator.getX();
			int y=Communicator.getY();
			System.out.println(x);
			System.out.println(y);
			while(currentY!=y||currentX!=x){
				this.computePath();
				this.createPath(x,y);
				this.followPath();
				System.out.println("looping");
				Delay.msDelay(1000);//helpful for debugging to watch the robot stop
			}
		}
	}
}
