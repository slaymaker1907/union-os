package os.union;

import java.net.Socket;

public class ThinClient
{
	public static void main(String[] args) throws Exception
	{
		int n = 10000;
		System.out.println(Fibo.blockFibo(n));
		try(Distributer distr = new Distributer(new Socket("127.0.0.1", 9000)))
		{
			distr.invokeMethod(new FiboProgram(n));
		}
	}
}
