package os.union.server;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

import os.union.Distributer;
import os.union.NetLocation;
import os.union.ObjectSocket;
import os.union.ProgramPacket;
import os.union.util.Closing;

public class WorkerServer implements AutoCloseable
{
	private ServerSocket server;
	private ConcurrentHashMap<Long, WorkerClient> jobsManager;
	private Distributer distributer;
	private Thread acceptConnThread;
	private ControlComm controlComm;
	
	public WorkerServer(int port) throws IOException
	{
		this.server = new ServerSocket(port);
		this.controlComm = null;
		this.jobsManager = new ConcurrentHashMap<>();
		this.acceptConnThread = new Thread(() ->
			{
				while(!Thread.interrupted())
				{
					try
					{
						awaitConnection();
					}
					catch(IOException e)
					{
						System.err.println(e);
					}
				}
			});
		this.acceptConnThread.start();
	}
	
	private void awaitConnection() throws IOException
	{
		Socket newConn = server.accept();
		// Only one thread should be calling this function.
		ObjectSocket obSock = new ObjectSocket(newConn);
		if (this.controlComm == null)
		{
			NetLocation brainLoc = (NetLocation)obSock.receiveObject();
			this.distributer = new Distributer(brainLoc);
			// Send an update every 100ms.
			this.controlComm = new ControlComm(obSock, 100);
		}
		else
		{
			@SuppressWarnings("unchecked")
			ProgramPacket<Serializable, Serializable> program = (ProgramPacket<Serializable, Serializable>) obSock.receiveObject();
			WorkerClient client = new WorkerClient(obSock, program.getProgram(), this.distributer);
			jobsManager.put(program.getIndex(), client);
		}
	}
	
	@Override
	public void close() throws IOException
	{
		Closing.closeAll(
				Closing.closeThread(acceptConnThread), 
				server, 
				Closing.collect(jobsManager.values().toArray(new AutoCloseable[0])), 
				distributer);
	}
}
