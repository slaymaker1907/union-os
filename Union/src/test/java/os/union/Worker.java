package os.union;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;

import os.union.Executor;
import os.union.ResultHandler;

public class Worker
{
	public static void main(String[] args) throws Exception
	{
		try(ServerSocket servSock = new ServerSocket(9000);
			Socket sock = servSock.accept();
			ObjectSocket<SerialIterable<Serializable>, Serializable> obSock = new ObjectSocket<>(sock))
		{
			SendResultBack sender = new SendResultBack(obSock);
			Executor exec = new Executor();
			exec.run(obSock.receiveObject(), sender);
		}
	}
	
	public static class SendResultBack implements ResultHandler<Serializable>
	{
		private ObjectSocket<SerialIterable<Serializable>, Serializable>  sender;
		
		public SendResultBack(ObjectSocket<SerialIterable<Serializable>, Serializable> sock) throws IOException
		{
			sender = sock;
		}
		
		@Override
		public void receiveResult(Serializable result)
		{
			try {
				sender.sendObject(result);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
