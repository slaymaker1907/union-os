package os.union.server;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.util.function.Supplier;

import os.union.ObjectSocket;
import os.union.NetLocation;

public class BrainServer implements AutoCloseable
{
	private ServerSocket clientPortal;
	private Supplier<NetLocation> programPlacer;
	
	public BrainServer(int port, Supplier<NetLocation> programPlacer) throws IOException
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
		try(ObjectSocket<Serializable, NetLocation> clientSock = new ObjectSocket<>(this.clientPortal.accept()))
		{
			NetLocation worker = this.programPlacer.get();
			clientSock.sendObject(worker);
		}
	}

	@Override
	public void close() throws IOException
	{
		this.clientPortal.close();
	}	
}
