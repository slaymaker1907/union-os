package os.union;

import java.io.Serializable;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

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
	
	public boolean equals(Object o)
	{
		try
		{
			NetLocation other = (NetLocation)o;
			return this.address.equals(other.getAddress()) && this.port == other.port;
		}
		catch (Exception e)
		{
			return false;
		}
	}
	
	public int hashCode()
	{
		return new HashCodeBuilder()
				.append(address)
				.append(port)
				.toHashCode();
	}
	
	public String toString()
	{
		return new ToStringBuilder(this)
				.append("address", this.address)
				.append("port", this.port)
				.build();
	}
}
