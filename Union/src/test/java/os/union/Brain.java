package os.union;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import os.union.server.BrainServer;
import os.union.server.WorkerManager;

public class Brain
{
	public static final Logger LOG = Logger.getLogger(Brain.class);
	
	public static void main(String[] args) throws Exception
	{
		LOG.info("Initializing brain.");
		List<NetLocation> locations = Arrays.asList(new NetLocation("54.197.116.180", 9001));
		NetLocation brainLocation = new NetLocation("127.0.0.1", 9000);
		WorkerManager man = new WorkerManager(locations, brainLocation);
		try(BrainServer server = new BrainServer(9000, man))
		{
			LOG.info("Brain fully operational.");
			while(true)
			{
				server.handleNewClient();
			}
		}
	}

}
