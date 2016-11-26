package os.union.server;

import os.union.ObjectSocket;
import os.union.util.Closing;

import java.io.IOException;
import org.apache.log4j.Logger;

public class ControlComm implements AutoCloseable
{
	private ObjectSocket control;
	private Thread sender;
	private MeasureStick measurer;
	
	private static final Logger LOG = Logger.getLogger("Performance Reporting");
	
	public ControlComm(ObjectSocket control, long sleepTime)
	{
		this.control = control;
		this.measurer = new MeasureStick();
		this.sender = new Thread(()->
		{
			try
			{
				while(true)
				{
					try
					{
						this.sendUpdate();
					} catch (IOException e)
					{
						LOG.fatal("Could not communicate with brain.", e);
						System.exit(1);
					}
					Thread.sleep(sleepTime);
				}
			}
			catch(InterruptedException e)
			{
				LOG.info("Control thread interrupted.");
			}
		});
	}
	
	private void sendUpdate() throws IOException
	{
		this.control.sendObject(measurer.getPerfMeasurement());
	}

	@Override
	public void close()
	{
		Closing.closeAll(Closing.closeThread(this.sender), this.measurer, this.control);
	}
}
