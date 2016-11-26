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

public class WorkerServer implements AutoCloseable
{
	private ServerSocket server;
	private volatile ObjectSocket<Serializable, Serializable> controlSocket;
	private ConcurrentHashMap<Long, WorkerClient> jobsManager;
	private Distributer distributer;
	private Thread acceptConnThread;
	
	public WorkerServer(int port) throws IOException
	{
		this.server = new ServerSocket(port);
		this.controlSocket = null;
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
		if (controlSocket == null)
		{
			this.controlSocket = new ObjectSocket<>(newConn);
			NetLocation brainLoc = (NetLocation)this.controlSocket.receiveObject();
			this.distributer = new Distributer(brainLoc);
		}
		else
		{
			ObjectSocket<Serializable, Serializable> obSock = new ObjectSocket<>(newConn);
			@SuppressWarnings("unchecked")
			ProgramPacket<Serializable, Serializable> program = (ProgramPacket<Serializable, Serializable>) obSock.receiveObject();
			WorkerClient client = new WorkerClient(obSock, program.getProgram(), this.distributer);
			jobsManager.put(program.getIndex(), client);
		}
	}

	@Override
	public void close() throws IOException
	{
		try
		{
			this.acceptConnThread.interrupt();
			try
			{
				this.acceptConnThread.join();
			}
			catch(InterruptedException e)
			{
				System.err.println(e);
			}
		}
		finally
		{
			try
			{
				this.server.close();
			}
			finally
			{
				Exception toThrow = null;
				for(WorkerClient client : jobsManager.values())
				{
					try
					{
						try
						{
							client.close();
						}
						catch (IOException | RuntimeException e)
						{
							if (toThrow != null)
								e.addSuppressed(toThrow);
							toThrow = e;
						}
						
						if (toThrow != null)
						{
							if (toThrow instanceof IOException)
								throw (IOException)toThrow;
							else
								throw (RuntimeException) toThrow;
						}
					}
					finally
					{
						try
						{
							this.distributer.close();
						}
						finally
						{
							this.controlSocket.close();
						}
					}
				}
			}
		}
	}
}
