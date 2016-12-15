package os.union;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

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
		private double[] dummyData;
		
		public MigrationProgram(double megabytes)
		{
			int size = (int)(megabytes * 1_000_000.0 / 8.0);
			dummyData = new double[size];
			Random gen = new Random(8675309);
			for(int i = 0; i < size; i++)
				dummyData[i] = gen.nextFloat();
		}
		
		@Override
		public void feedInput(Long input)
		{
			needSend = true;
		}
		
		// To make sure dummyData is not optimized away.
		public double[] getDummy() {
			return this.dummyData;
		}
		
		public double getMegs() {
			return dummyData.length / 1_000_000.0 * 8.0;
		}

		@Override
		public boolean computationComplete()
		{
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
		private MigrationProgram program;
		private RemoteInput<Long> stdin;
		private long startTime;
		
		public MigrationDetector(MigrationProgram program) {
			this.program = program;
		}
		
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
		double megs;
		if (args.length == 0)
			megs = 100.0;
		else
			megs = Double.parseDouble(args[0]);
		LOG.info("Preparing to send message of size: " + megs);
		
		LOG.info("Initializing brain.");
		List<NetLocation> locations = Arrays.asList(new NetLocation("172.31.30.162", 9001), new NetLocation("172.31.23.125", 9001));
		NetLocation brainLocation = new NetLocation("127.0.0.1", 9000);
		WorkerManager man = new WorkerManager(locations, brainLocation);
		try(BrainServer server = new BrainServer(9000, man))
		{
			server.startHandlingClients();
			LOG.info("Brain fully operational. Waiting for migration command.");
			try(Distributer dist = new Distributer(brainLocation))
			{
				MigrationDetector detector = new MigrationDetector(new MigrationProgram(megs));
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
		
		System.exit(0);
	}
}
