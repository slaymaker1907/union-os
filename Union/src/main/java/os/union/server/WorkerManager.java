package os.union.server;

import java.io.IOException;
import java.util.ArrayList;
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
	
	public static final Logger LOG = Logger.getLogger(WorkerManager.class);
	
	public WorkerManager(List<NetLocation> locations, NetLocation brainLocation)
	{
		this.locations = locations;
		this.commThreads = new ArrayList<>();
		for(NetLocation location : locations)
		{
			commThreads.add(new Thread(() ->
			{
				try(ObjectSocket sock = new ObjectSocket(location))
				{
					sock.sendObject(brainLocation);
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
	}

	public NetLocation get(long program)
	{
		NetLocation worker = monitor.getLightestLoc(locations);
		monitor.addProgram(worker, program);
		return worker;
	}

	@Override
	public void close()
	{
		Closing.closeAll(commThreads.stream().map(Closing::closeThread).toArray(sz -> new AutoCloseable[sz]));
	}
}
