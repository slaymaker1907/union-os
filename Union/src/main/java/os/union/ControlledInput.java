package os.union;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayDeque;

public class ControlledInput <T extends Serializable> implements RemoteInput<T>
{
	private RemoteInput<T> other;
	private ArrayDeque<T> queue;
	
	public ControlledInput(RemoteInput<T> other)
	{
		this.other = other;
		this.queue = null;
	}
	
	public ControlledInput()
	{
		this.other = null;
		this.disableOther();
	}
		
	@Override
	public synchronized void sendInput(T toSend) throws IOException
	{
		if (queue == null)
			this.other.sendInput(toSend);
		else
			queue.add(toSend);
	}

	public synchronized void disableOther()
	{
		if (this.queue == null)
			this.queue = new ArrayDeque<>();
	}
	
	public synchronized void setOther(RemoteInput<T> other) throws IOException
	{
		this.other = other;
		if (this.queue != null)
		{
			for(T ele : queue)
				other.sendInput(ele);
		}
		this.queue = null;
	}
}
