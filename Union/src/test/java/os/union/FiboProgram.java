package os.union;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Scanner;

public class FiboProgram implements PauseableProgram<BigInteger, Integer>
{

	@Override
	public Program<BigInteger, Integer> getProgram()
	{
		return new Fibo();
	}

	@Override
	public void handleResult(BigInteger stdout)
	{
		System.out.println(stdout);
	}

	@Override
	public void initProgram(RemoteInput<Integer> stdin)
	{
		Thread myThread = new Thread(() ->
		{
			try(Scanner sc = new Scanner(System.in))
			{
				while(true)
				{
					Integer toSend = sc.nextInt();
					try
					{
						stdin.sendInput(toSend);
					}
					catch (IOException e)
					{
						System.err.println(e);
					}
				}
			}
		});
		myThread.start();
	}
}
