package os.union.server;

import java.io.Serializable;

import os.union.NetLocation;
import os.union.Program;

public class MovedProgram <OutputT extends Serializable, InputT extends Serializable> implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private Program<OutputT, InputT> moved;
	private NetLocation newLoc;

	public MovedProgram(Program<OutputT, InputT> moved, NetLocation newLoc)
	{
		this.moved = moved;
		this.newLoc = newLoc;
	}
	
	public NetLocation getNewLoc()
	{
		return this.newLoc;
	}
	
	public Program<OutputT, InputT> getMoved()
	{
		return this.moved;
	}
}
