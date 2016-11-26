package os.union.server;

import java.io.IOException;
import java.io.Serializable;

import os.union.Distributer;
import os.union.ObjectSocket;
import os.union.Program;

public class WorkerClient implements AutoCloseable
{
	private ProgramThread thread;
	private Thread inputThread;
	private ObjectSocket socket;
	
	public WorkerClient(ObjectSocket socket, Program<Serializable, Serializable> program,
			Distributer distributer)
	{
		this.socket = socket;
		this.thread = new ProgramThread(program, new WorkerHandler(socket, distributer));
		this.inputThread = new Thread(() ->
		{
			while(!Thread.interrupted())
			{
				try
				{
					Serializable input = socket.receiveObject();
					program.feedInput(input);
				} catch (IOException e)
				{
					e.printStackTrace();
					Thread.currentThread().interrupt();
				}
			}
		});
		this.inputThread.start();
		this.thread.start();
	}

	@Override
	public void close() throws IOException
	{
		try
		{
			this.inputThread.interrupt();
			try
			{
				this.inputThread.join();
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		finally
		{
			try
			{
				this.thread.interrupt();
				try
				{
					this.thread.join();
				}
				catch(InterruptedException e)
				{
					System.err.println(e);
				}
			}
			finally
			{
				this.socket.close();
			}
		}
	}
}
