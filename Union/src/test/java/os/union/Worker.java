package os.union;

import os.union.server.WorkerServer;

public class Worker
{
	public static void main(String[] args) throws Exception
	{
		try(WorkerServer server = new WorkerServer(9001))
		{
			while(true)
				Thread.sleep(1000);
		}
	}
}
