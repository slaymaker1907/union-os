package os.union.server;

import os.union.ObjectSocket;
import os.union.util.Closing;

import java.io.IOException;
import java.util.function.Consumer;

import org.apache.log4j.Logger;

public class ControlComm implements AutoCloseable
{
	private ObjectSocket control;
	private Thread sender, receiver;
	private MeasureStick measurer;
	
	private static final Logger LOG = Logger.getLogger(ControlComm.class);
	
	public ControlComm(ObjectSocket control, long sleepTime, Consumer<MigrationCommand> migHandler)
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
		this.receiver = new Thread(() ->
		{
			while(!Thread.interrupted())
			{
				try
				{
					migHandler.accept((MigrationCommand) this.control.receiveObject());
				} catch (IOException e)
				{
					LOG.fatal("Could not communicate with brain.", e);
					System.exit(2);
				}
			}
		});
		this.receiver.start();
		this.sender.start();
	}
	
	private void sendUpdate() throws IOException
	{
		PerformanceMeasurement measure = measurer.getPerfMeasurement();
		//LOG.info("Performance: " + measure);
		this.control.sendObject(measure);
	}

	@Override
	public void close()
	{
		Closing.closeAll(Closing.closeThread(this.sender), Closing.closeThread(receiver), this.measurer, this.control);
	}
}
