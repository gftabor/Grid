import java.io.IOException;


public class GridNavigation {

	public static void main(String[] args) throws IOException{
		Navigation Navigator = new Navigation();		
		CommunicationEvanIsMean Communicator =new CommunicationEvanIsMean(Navigator);
		Navigator.initialize(Communicator);
		Communicator.establishConnection();
		Navigator.followGui();				
	}
}
