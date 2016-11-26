package os.union;

import java.io.Serializable;

public class ProgramPacket <OutputT extends Serializable, InputT extends Serializable> implements Serializable
{
	private static final long serialVersionUID = 1L;
	private long index;
	private Program<OutputT, InputT> program;
	
	public ProgramPacket(long index, Program<OutputT, InputT> program)
	{
		this.index = index;
		this.program = program;
	}
	
	public long getIndex()
	{
		return this.index;
	}
	
	public Program<OutputT, InputT> getProgram()
	{
		return this.program;
	}
	
	@Override
	public boolean equals(Object o)
	{
		try
		{
			@SuppressWarnings("unchecked")
			ProgramPacket<OutputT, InputT> other = (ProgramPacket<OutputT, InputT>)o;
			return this.index == other.index;
		} catch(Exception e)
		{
			return false;
		}
	}
	
	@Override
	public int hashCode()
	{
		return (int)this.index;
	}
}
