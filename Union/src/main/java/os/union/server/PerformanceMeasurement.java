package os.union.server;

import java.io.Serializable;

public class PerformanceMeasurement implements Serializable
{
	private static final long serialVersionUID = 1L;
	private double cpuUsage, memUsage;
	
	public PerformanceMeasurement(double cpuUsage, double memUsage)
	{
		this.cpuUsage = cpuUsage;
		this.memUsage = memUsage;
	}
	
	public double getCpuUsage()
	{
		return this.cpuUsage;
	}
	
	public double getMemUsage()
	{
		return this.memUsage;
	}
}
