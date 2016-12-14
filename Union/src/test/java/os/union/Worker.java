package os.union;

import org.apache.log4j.Logger;

import os.union.server.WorkerServer;

public class Worker
{
	public static final Logger LOG  = Logger.getLogger(Worker.class);
	
	public static void main(String[] args) throws Exception
	{
		LOG.info("Initializing server.");
		int port;
		if (args.length == 1)
			port = Integer.parseInt(args[0]);
		else
			port = 9001;
		try(WorkerServer server = new WorkerServer(port))
		{
			LOG.info("Server initialized.");
			while(true)
				Thread.sleep(1000);
		}
	}
}
