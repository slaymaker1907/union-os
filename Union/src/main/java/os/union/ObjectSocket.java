package os.union;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public class ObjectSocket <OutputT extends Serializable, InputT extends Serializable> implements AutoCloseable
{
	private ObjectOutputStream outStream;
	private ObjectInputStream inpStream;
	private Socket sock;
	
	public ObjectSocket(Socket sock) throws IOException
	{
		this.sock = sock;
		outStream = new ObjectOutputStream(sock.getOutputStream());
		try {
			inpStream = new ObjectInputStream(sock.getInputStream());
		} catch (IOException e) {
			try {
				outStream.close();
			} finally {
				sock.close();
			}
			throw e;
		}
	}
	
	public ObjectSocket(NetLocation location) throws IOException
	{
		this(new Socket(location.getAddress(), location.getPort()));
	}
	
	public void sendObject(InputT toSend) throws IOException {
		outStream.writeObject(toSend);
	}
	
	@SuppressWarnings("unchecked")
	public OutputT receiveObject() throws IOException {
		try
		{
			return (OutputT) inpStream.readObject();
		} catch (ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}
	}

	public void close() throws IOException
	{
		try {
			outStream.close();
		} finally {
			try {
				inpStream.close();
			} finally {
				sock.close();
			}
		}
	}
}
