package os.union;

public class ThinClient
{
	public static void main(String[] args) throws Exception
	{
		while(true)
		{
			try(Distributer distr = new Distributer(new NetLocation("127.0.0.1", 9000)))
			{
				distr.invokeMethod(new FiboProgram());
			}
			Thread.sleep(1000);
		}
	}
}
