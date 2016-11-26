package os.union;

import java.io.Serializable;

public class NetLocation implements Serializable
{
	private static final long serialVersionUID = 1L;

	private String address;
	private int port;
	
	public NetLocation(String address, int port)
	{
		this.address = address;
		this.port = port;
	}
	
	public String getAddress()
	{
		return this.address;
	}
	
	public int getPort()
	{
		return this.port;
	}
}
