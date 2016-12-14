package os.union.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import os.union.NetLocation;
import os.union.ObjectSocket;
import os.union.util.Closing;

import org.apache.log4j.Logger;

public class WorkerManager implements AutoCloseable
{
	private List<NetLocation> locations;
	private WorkerMonitor monitor = new WorkerMonitor();
	private List<Thread> commThreads;
	private HashMap<NetLocation, ObjectSocket> commandLines = new HashMap<>();
	
	public static final Logger LOG = Logger.getLogger(WorkerManager.class);
	
	public WorkerManager(List<NetLocation> locations, NetLocation brainLocation)
	{
		this.locations = locations;
		this.commThreads = new ArrayList<>();
		LOG.info("Sending brain location to workers.");
		for(NetLocation location : locations)
		{
			commThreads.add(new Thread(() ->
			{
				try(ObjectSocket sock = new ObjectSocket(location))
				{
					sock.sendObject(brainLocation);
					synchronized(commandLines)
					{
						commandLines.put(location, sock);
					}
					LOG.info(location + " was contacted, awaiting performance updates.");
					while(!Thread.interrupted())
					{
						PerformanceMeasurement meas = (PerformanceMeasurement) sock.receiveObject();
						monitor.addMeasurement(location, meas);
					}
				} catch (IOException e)
				{
					LOG.error("Could not connect to worker at location " + location, e);
				}
			}));
		}
		for(Thread thread : commThreads)
			thread.start();
	}

	public NetLocation get(long program)
	{
		NetLocation worker = monitor.getLightestLoc(locations);
		monitor.addProgram(worker, program);
		return worker;
	}
	
	public void moveProgram(Long programToMove) throws IOException
	{
		NetLocation oldLoc = monitor.getProgLoc(programToMove);
		NetLocation newLoc = monitor.getLightestLoc(locations);
		this.commandLines.get(oldLoc).sendObject(new MigrationCommand(newLoc, programToMove));
	}

	@Override
	public void close()
	{
		Closing.closeAll(commThreads.stream().map(Closing::closeThread).toArray(sz -> new AutoCloseable[sz]));
	}
}
