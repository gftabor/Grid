import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;


public class CommunicationEvanIsMean {

	Socket s;
	DataInputStream in;
	DataOutputStream out;
	Navigation Navigator;
	int x;
	int y;
	
	
	public void establishConnection() throws IOException{
		s = new Socket("10.0.1.11", 1111);	

	}
	
	public CommunicationEvanIsMean(Navigation givenNavigator){
		Navigator=givenNavigator;	//not needed but could be 	
	}

	public void sendPacket(int packetID, int x, int y,int lastX,int lastY) throws IOException {
		out.writeInt(packetID);
		out.writeInt(x);
		out.writeInt(y);
		out.writeInt(lastX);
		out.writeInt(lastY);
	}
	
	public void recievePacket() throws IOException{
		in = new DataInputStream(s.getInputStream());
		out = new DataOutputStream(s.getOutputStream());
		while(in.available()==0){}//wait for packets to ensure no errors
		x = in.readInt();
		y = in.readInt();
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}

}
