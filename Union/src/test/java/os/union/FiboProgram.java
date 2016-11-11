package os.union;

import java.math.BigInteger;
import java.util.function.Consumer;

public class FiboProgram implements PauseableProgram<BigInteger, Void>
{
	private Fibo fibo;
	
	public FiboProgram(int n)
	{
		this.fibo = new Fibo(n);
	}

	@Override
	public SerialIterable<BigInteger> getProgram()
	{
		return fibo;
	}

	@Override
	public void handleResult(BigInteger stdout)
	{
		if (stdout != null)
		{
			System.out.println(stdout);
		}
	}

	@Override
	public void initProgram(Consumer<Void> stdin)
	{
	}

}
