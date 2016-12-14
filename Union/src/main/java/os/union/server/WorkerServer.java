package os.union.server;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

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
	
	public static final Logger LOG = Logger.getLogger(WorkerServer.class);
	
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
				LOG.info("Connection thread interrupted, shutting down.");
			});
		this.acceptConnThread.start();
	}
	
	public synchronized void handleMigration(MigrationCommand mig)
	{
		Long id = mig.getToMove();
		WorkerClient client = this.jobsManager.get(id);
		try
		{
			client.move(mig.getMoveTo());
		} catch (IOException e)
		{
			LOG.fatal("Could not migrate job.");
			System.exit(3);
		}
		client.close();
		this.jobsManager.remove(id);
	}
	
	private void awaitConnection() throws IOException
	{
		LOG.info("Waiting for new client.");
		Socket newConn = server.accept();
		LOG.info("New client connected.");
		// Only one thread should be calling this function.
		ObjectSocket obSock = new ObjectSocket(newConn);
		if (this.controlComm == null)
		{
			NetLocation brainLoc = (NetLocation)obSock.receiveObject();
			LOG.info("Brain connected at " + brainLoc);
			this.distributer = new Distributer(brainLoc);
			// Send an update every 100ms.
			this.controlComm = new ControlComm(obSock, 100, this::handleMigration);
		}
		else
		{
			@SuppressWarnings("unchecked")
			ProgramPacket<Serializable, Serializable> program = (ProgramPacket<Serializable, Serializable>) obSock.receiveObject();
			LOG.info("Starting program " + program.getIndex());
			WorkerClient client = new WorkerClient(obSock, program.getProgram(), this.distributer);
			synchronized(this)
			{
				jobsManager.put(program.getIndex(), client);
			}
		}
	}
	
	@Override
	public void close() throws IOException
	{
		synchronized(this)
		{
			LOG.info("Shutting down worker.");
			Closing.closeAll(
					Closing.closeThread(acceptConnThread), 
					server, 
					Closing.collect(jobsManager.values().toArray(new AutoCloseable[0])), 
					distributer);
			LOG.info("Worker shut down.");
		}
	}
}
