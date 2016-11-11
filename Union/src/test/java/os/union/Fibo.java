package os.union;

import java.math.BigInteger;

public class Fibo implements SerialIterable<BigInteger>
{
	private static final long serialVersionUID = 1L;
	
	private int n, pos;
	private BigInteger result, lastResult;
	
	public Fibo(int n)
	{
		this.result = BigInteger.ONE;
		this.lastResult = BigInteger.ZERO;
		this.pos = 0;
		this.n = n;
	}

	@Override
	public boolean hasNext()
	{
		return this.pos <= this.n;
	}

	@Override
	public BigInteger next()
	{
		if (this.pos >= this.n)
		{
			this.pos++;
			return this.result;
		} 
		else
		{
			BigInteger nextResult = lastResult.add(result);
			lastResult = result;
			result = nextResult;
			this.pos++;
			return null;
		}
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
}
