package os.union.server;

import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public class MeasureStick implements AutoCloseable
{
	public static final String CURRENT_PROCESS_ID  = "$$";
	
	private Sigar sigar;
	private Runtime runtime;
	
	public MeasureStick()
	{
		sigar =  new Sigar();
		runtime = Runtime.getRuntime();
	}
	
	public double getCpuUsage()
	{
		try
		{
			return sigar.getCpuPerc().getCombined();
		} catch (SigarException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public double getMemUsage()
	{
		long totalMem = runtime.maxMemory();
		return (totalMem - runtime.freeMemory()) / ((double)totalMem);
	}
	
	public PerformanceMeasurement getPerfMeasurement()
	{
		return new PerformanceMeasurement(this.getCpuUsage(), this.getMemUsage());
	}

	@Override
	public void close()
	{
		this.sigar.close();
	}
	
	
}
