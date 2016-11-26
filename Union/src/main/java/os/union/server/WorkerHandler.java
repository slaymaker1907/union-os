package os.union.server;

import java.io.IOException;
import java.io.Serializable;

import os.union.Distributer;
import os.union.ObjectSocket;
import os.union.ResultHandler;

public class WorkerHandler implements ResultHandler<Serializable>
{
	private ObjectSocket clientSocket;
	private Distributer distributer;
	
	public WorkerHandler(ObjectSocket client, Distributer distributer)
	{
		this.clientSocket = client;
		this.distributer = distributer;
	}

	@Override
	public void sendResult(Serializable result)
	{
		try
		{
			clientSocket.sendObject(result);
		}
		catch (IOException e)
		{
			// This could happen theoretically, but should not happen for the purpose of this project's experiments.
			throw new RuntimeException(e);
		}
	}

	@Override
	public Distributer getDistributer()
	{
		return this.distributer;
	}

}
