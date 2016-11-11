package os.union;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;

public class Distributer implements AutoCloseable
{
	private ObjectSocket<Serializable, Serializable> toBrain;
	
	public Distributer(Socket toBrain) throws IOException
	{
		this.toBrain = new ObjectSocket<>( toBrain);
	}
	
	@SuppressWarnings("unchecked")
	public <OutputT extends Serializable, InputT> void invokeMethod(PauseableProgram<OutputT, InputT> program) throws IOException
	{
		SerialIterable<OutputT> toSend = program.getProgram();
		this.toBrain.sendObject(toSend);
		while(true) {
			program.handleResult((OutputT) this.toBrain.receiveObject());
		}
	}

	public void close() throws Exception
	{
		toBrain.close();
	}
}
