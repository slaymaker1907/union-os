package os.union;

import java.io.Serializable;

public class Executor
{
	public <T extends Serializable> void run(SerialIterable<T> toRun, ResultHandler<T> handler) 
	{
		while(toRun.hasNext()) 
		{
			handler.receiveResult(toRun.next());
		}
	}
}
