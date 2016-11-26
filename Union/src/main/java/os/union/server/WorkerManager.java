package os.union.server;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import os.union.NetLocation;

public class WorkerManager implements Supplier<NetLocation>
{
	private List<NetLocation> locations;
	private AtomicInteger currentPos;
	
	public WorkerManager(List<NetLocation> locations)
	{
		this.locations = locations;
		this.currentPos = new AtomicInteger(0);
	}

	@Override
	public NetLocation get()
	{
		int locPos = this.currentPos.getAndIncrement() % locations.size();
		return locations.get(locPos);
	}
}
