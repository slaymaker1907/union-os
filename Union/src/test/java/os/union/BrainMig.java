package os.union;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import os.union.server.BrainServer;
import os.union.server.WorkerManager;

public class BrainMig
{
	public static final Logger LOG = Logger.getLogger(Brain.class);
	
	public static class MigrationProgram implements Program<Long, Long>
	{
		private static final long serialVersionUID = 1L;
		private volatile boolean needSend = false;
		
		@Override
		public void feedInput(Long input)
		{
			needSend = true;
		}

		@Override
		public boolean computationComplete()
		{
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void computeNext(ResultHandler<Long> handler)
		{
			if (needSend)
			{
				handler.sendResult(0L);
				needSend = false;
			}
		}
	}
		
	public static class MigrationDetector implements PauseableProgram<Long, Long>
	{
		private MigrationProgram program = new MigrationProgram();
		private RemoteInput<Long> stdin;
		private long startTime;
		
		@Override
		public Program<Long, Long> getProgram()
		{
			return program;
		}

		@Override
		public void handleResult(Long stdout)
		{
			long totalTime = System.currentTimeMillis() - this.startTime;
			LOG.info(String.format("Migration took %d ms.", totalTime));
			synchronized(this)
			{
				this.notify();
			}
		}

		@Override
		public void initProgram(RemoteInput<Long> stdin)
		{
			this.stdin = stdin;
			synchronized(this)
			{
				this.notify();
			}
		}
				
		public void setStartTime() throws IOException
		{
			this.startTime = System.currentTimeMillis();
			this.stdin.sendInput(0L);
		}
	}
	
	public static void main(String[] args) throws Exception
	{
		LOG.info("Initializing brain.");
		List<NetLocation> locations = Arrays.asList(new NetLocation("127.0.0.1", 9001), new NetLocation("127.0.0.1", 9002));
		NetLocation brainLocation = new NetLocation("127.0.0.1", 9000);
		WorkerManager man = new WorkerManager(locations, brainLocation);
		try(BrainServer server = new BrainServer(9000, man))
		{
			server.startHandlingClients();
			LOG.info("Brain fully operational. Waiting for migration command.");
			try(Distributer dist = new Distributer(brainLocation))
			{
				MigrationDetector detector = new MigrationDetector();
				Thread th = new Thread(() -> 
				{
					try
					{
						dist.invokeMethod(detector, 10);
					} catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
				th.start();
				synchronized(detector)
				{
					detector.wait();
				}
				LOG.info("Beginning migration.");
				man.moveProgram(10L);
				detector.setStartTime();
				synchronized(detector)
				{
					detector.wait();
				}
				LOG.info("Migration complete.");
				Thread.sleep(100_000);
			}
		}
	}
}
