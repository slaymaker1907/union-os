package os.union;

import java.io.EOFException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Random;

public class Distributer implements AutoCloseable
{
	private NetLocation brainLoc;
	private Random gen = new Random();
	
	public Distributer(NetLocation brainLoc) throws IOException
	{
		this.brainLoc = brainLoc;
	}
	
	public <OutputT extends Serializable, InputT extends Serializable> void invokeMethod(PauseableProgram<OutputT, InputT> program) throws IOException
	{
		ProgramPacket<OutputT, InputT> toSend = new ProgramPacket<>(gen.nextLong(), program.getProgram());
		NetLocation workerLoc = null;
		try(ObjectSocket<NetLocation, Serializable> toBrain = new ObjectSocket<>(this.brainLoc))
		{
			workerLoc = toBrain.receiveObject();
		}
		try(ObjectSocket<OutputT, Serializable> worker = new ObjectSocket<>(workerLoc))
		{
			worker.sendObject(toSend);
			program.initProgram(worker::sendObject);
			while(true) 
			{
				program.handleResult((OutputT) worker.receiveObject());
			}
		} catch (EOFException e)
		{
			// Program is complete.
		}
	}

	public void close() throws IOException
	{
	}
}
