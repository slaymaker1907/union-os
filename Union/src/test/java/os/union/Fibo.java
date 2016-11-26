package os.union;

import java.math.BigInteger;

public class Fibo implements Program<BigInteger, Integer>
{
	private static final long serialVersionUID = 1L;
	
	private int n, pos;
	private BigInteger result, lastResult;
	
	public Fibo()
	{
		this.result = BigInteger.ONE;
		this.lastResult = BigInteger.ZERO;
		this.pos = 0;
		this.n = -1;
	}
	
	public static BigInteger blockFibo(int n)
	{
		BigInteger lastResult = BigInteger.ZERO, result = BigInteger.ONE;
		
		for(int pos = 0; pos < n; pos++)
		{
			BigInteger nextResult = lastResult.add(result);
			lastResult = result;
			result = nextResult;
		}
		
		return result;
	}

	@Override
	public synchronized void feedInput(Integer input)
	{
		this.n = input;
		this.pos = 0;
		this.result = BigInteger.ONE;
		this.lastResult = BigInteger.ZERO;
	}

	@Override
	public boolean computationComplete()
	{
		return false;
	}

	@Override
	public synchronized void computeNext(ResultHandler<BigInteger> handler)
	{
		if (this.pos > this.n)
		{
			try
			{
				Thread.sleep(20);
			}
			catch (InterruptedException e)
			{
				Thread.currentThread().interrupt();
			}
		}
		else
		{
			if (this.pos == this.n)
			{
				handler.sendResult(result);
			}
			BigInteger nextResult = lastResult.add(result);
			lastResult = result;
			result = nextResult;
			this.pos++;
		}
	}
}
