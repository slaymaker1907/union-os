package os.union.util;

public class Closing
{
	public static void closeAll(AutoCloseable ... toClose)
	{
		try
		{
			collect(toClose).close();
		} catch (Exception toThrow)
		{
			if (toThrow instanceof RuntimeException)
				throw (RuntimeException)toThrow;
			else
				throw new RuntimeException(toThrow);
		}
	}
	
	public static AutoCloseable collect(AutoCloseable ... toClose)
	{
		return () ->
		{
			Exception toThrow = null;
			for(AutoCloseable closeable : toClose)
			{
				try
				{
					closeable.close();
				}
				catch (Exception e)
				{
					if (toThrow != null)
						e.addSuppressed(toThrow);
					toThrow = e;
				}
			}
			
			if (toThrow != null)
			{
				if (toThrow instanceof RuntimeException)
					throw (RuntimeException)toThrow;
				else
					throw new RuntimeException(toThrow);
			}
		};
	}
	
	public static AutoCloseable closeThread(Thread toClose)
	{
		return () -> 
		{
			toClose.interrupt();
			toClose.join();
		};
	}
}
