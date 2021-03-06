package os.union.server;

import java.io.IOException;
import java.net.ServerSocket;

import os.union.ObjectSocket;
import os.union.NetLocation;

import org.apache.log4j.Logger;

public class BrainServer implements AutoCloseable
{
	private ServerSocket clientPortal;
	private WorkerManager programPlacer;
	
	public static final Logger LOG = Logger.getLogger(BrainServer.class);
	
	public BrainServer(int port, WorkerManager programPlacer) throws IOException
	{
		this.clientPortal = new ServerSocket(port);
		this.programPlacer = programPlacer;
	}
	
	public void startHandlingClients()
	{
		Thread toRun = new Thread(() ->
		{
			while(!Thread.interrupted())
			{
				try
				{
					this.handleNewClient();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		});
		toRun.start();
	}
	
	public void handleNewClient() throws IOException
	{
		LOG.info("Waiting for new client.");
		try(ObjectSocket clientSock = new ObjectSocket(this.clientPortal.accept()))
		{
			LOG.info("New client connected.");
			Long programLocation = (Long) clientSock.receiveObject();
			NetLocation worker = this.programPlacer.get(programLocation);
			LOG.info("Allocating worker " + worker);
			clientSock.sendObject(worker);
		}
		LOG.info("Client disconnected.");
	}

	@Override
	public void close() throws IOException
	{
		this.clientPortal.close();
	}	
}
