package os.union.server;

import java.io.IOException;
import java.io.Serializable;

import os.union.Distributer;
import os.union.NetLocation;
import os.union.ObjectSocket;
import os.union.Program;
import os.union.util.Closing;

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
	public void close()
	{
		Closing.closeAll(Closing.closeThread(thread), socket, Closing.closeThread(inputThread));
	}
	
	public Program<Serializable, Serializable> getProgram()
	{
		return this.thread.getProgram();
	}
	
	public void move(NetLocation location) throws IOException
	{
		socket.sendObject(new MovedProgram<>(this.getProgram(), location));
	}
}
