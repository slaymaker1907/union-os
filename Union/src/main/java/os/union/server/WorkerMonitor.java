package os.union.server;


import com.gs.collections.api.map.MutableMap;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import com.gs.collections.impl.multimap.list.FastListMultimap;
import org.apache.log4j.Logger;

import os.union.NetLocation;

public class WorkerMonitor
{
	private MutableMap<NetLocation, PerformanceMeasurement> perf = UnifiedMap.newMap();
	private FastListMultimap<NetLocation, Long> programs = FastListMultimap.newMultimap();
	
	public static final Logger MONITOR_LOG = Logger.getLogger(WorkerMonitor.class);
	
	public static final PerformanceMeasurement DEFAULT_MEASUREMENT = new PerformanceMeasurement(0, 0);
	
	public synchronized void addMeasurement(NetLocation worker, PerformanceMeasurement meas)
	{
		perf.put(worker, meas);
		MONITOR_LOG.info(String.format("Worker at %s has overal performance %s", worker, meas));
	}
	
	public synchronized void addProgram(NetLocation worker, long programId)
	{
		programs.put(worker, programId);
		MONITOR_LOG.info(String.format("Allocating program %d at location %s.", programId, worker));
	}
	
	public synchronized int getProgramCount(NetLocation worker)
	{
		return programs.get(worker).size();
	}
	
	public synchronized PerformanceMeasurement getPerformance(NetLocation worker)
	{
		return perf.getOrDefault(worker, DEFAULT_MEASUREMENT);
	}
		
	public synchronized NetLocation getLightestLoc(Iterable<NetLocation> workers)
	{
		// Check to see if there are any unused locations.
		for(NetLocation worker : workers)
		{
			if (this.getProgramCount(worker) == 0)
				return worker;
		}
		
		// If all are occupied, then return one with non-default lowest CPU
		// with acceptable memory usage.
		NetLocation bestWorker = null;
		double bestCpu = Double.MAX_VALUE;
		for(NetLocation worker : workers)
		{
			PerformanceMeasurement measure = this.getPerformance(worker);
			if (!measure.equals(DEFAULT_MEASUREMENT))
			{
				int progCount = this.getProgramCount(worker);
				double memPerProg = measure.getMemUsage() / progCount;
				if ((progCount + 1) * memPerProg < .9)
				{
					double cpuUsage = measure.getCpuUsage();
					if (cpuUsage < bestCpu)
					{
						bestWorker = worker;
						bestCpu = cpuUsage;
					}
				}
			}
		}
		
		if (bestWorker == null)
			MONITOR_LOG.error("Could not find a worker with low enough memory usage.");
		return bestWorker;
	}
}
