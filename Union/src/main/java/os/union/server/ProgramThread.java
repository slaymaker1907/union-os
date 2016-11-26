package os.union.server;

import java.io.Serializable;

import os.union.Program;
import os.union.ResultHandler;

public class ProgramThread extends Thread
{
	private Program<Serializable, Serializable> program;
	private ResultHandler<Serializable> handler;
	
	public ProgramThread(Program<Serializable, Serializable> program, ResultHandler<Serializable> handler)
	{
		this.program = program;
		this.handler = handler;
	}
	
	@Override
	public void run()
	{
		while(!(program.computationComplete() || Thread.interrupted()))
		{
			this.program.computeNext(this.handler);
		}
	}
	
	public Program<Serializable, Serializable> getProgram()
	{
		return this.program;
	}
}
