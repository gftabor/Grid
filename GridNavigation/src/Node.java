import java.util.ArrayList;

///..,

public class Node {

	ArrayList<Node> connections = new ArrayList<Node>();
	public int xLocation;
	public int yLocation;
	public int distanceFromSource = 1000;
	public Node arrowLeadingToSource;

	public int getDistance() {
		return distanceFromSource;
	}
	public void setArrow(Node arrow){
		arrowLeadingToSource=arrow;
	}

	public Node getArrow() {
		return arrowLeadingToSource;
	}

	public void setDistance(int distance) {
		distanceFromSource = distance;
	}

	public void reset() {
		distanceFromSource = 1000;
		arrowLeadingToSource = null;
	}

	public Node(int x, int y) {
		xLocation = x;
		yLocation = y;
	}

	public int getX() {
		return xLocation;
	}

	public int getY() {
		return yLocation;
	}

	public void addEdge(Node edge) {
		connections.add(edge);
	}
	public void removeEdge(Node blockedEdge) {
		connections.remove(blockedEdge);
	}

	public ArrayList<Node> getConnections() {
		return connections;
	}
	@Override public String toString(){	
		String string="x"+xLocation+"y" +yLocation;
		return string;		
	}

}
