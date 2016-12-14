package os.union.server;

import os.union.NetLocation;
import java.io.Serializable;

public class MigrationCommand implements Serializable
{
	private static final long serialVersionUID = 1L;
	private NetLocation moveTo;
	private Long toMove;
	
	public MigrationCommand(NetLocation moveTo, Long toMove)
	{
		this.moveTo = moveTo;
		this.toMove = toMove;
	}
	
	public NetLocation getMoveTo()
	{
		return this.moveTo;
	}
	
	public Long getToMove()
	{
		return this.toMove;
	}	
}
