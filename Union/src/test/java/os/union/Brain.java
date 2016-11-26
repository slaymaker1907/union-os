package os.union;

import java.util.Arrays;
import java.util.List;

import os.union.server.BrainServer;
import os.union.server.WorkerManager;

public class Brain
{

	public static void main(String[] args) throws Exception
	{
		List<NetLocation> locations = Arrays.asList(new NetLocation("127.0.0.1", 9001));
		NetLocation brainLocation = new NetLocation("127.0.0.1", 9000);
		WorkerManager man = new WorkerManager(locations, brainLocation);
		try(BrainServer server = new BrainServer(9000, man))
		{
			for(NetLocation location : locations)
			{
				try(ObjectSocket sock = new ObjectSocket(location))
				{
					sock.sendObject(brainLocation);
				}
			}
			while(true)
				server.handleNewClient();
		}
	}

}
