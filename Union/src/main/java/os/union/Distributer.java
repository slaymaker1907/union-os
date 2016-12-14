package os.union;

import java.io.EOFException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Random;

import os.union.server.MovedProgram;

public class Distributer implements AutoCloseable
{
	private NetLocation brainLoc;
	private Random gen = new Random();
	
	public Distributer(NetLocation brainLoc) throws IOException
	{
		this.brainLoc = brainLoc;
	}
	
	@SuppressWarnings("unchecked")
	public <OutputT extends Serializable, InputT extends Serializable> 
		void invokeMethod(PauseableProgram<OutputT, InputT> program, long id) throws IOException
	{
		ProgramPacket<OutputT, InputT> toSend = new ProgramPacket<>(id, program.getProgram());
		NetLocation workerLoc = null;
		try(ObjectSocket toBrain = new ObjectSocket(this.brainLoc))
		{
			Long progInd = toSend.getIndex();
			toBrain.sendObject(progInd);
			workerLoc = (NetLocation)toBrain.receiveObject();
		}
		ControlledInput<InputT> input = new ControlledInput<>();
		ObjectSocket worker = null;
		program.initProgram(input);
		try
		{
			while(true)
			{
				worker = new ObjectSocket(workerLoc);
				worker.sendObject(toSend);
				input.setOther(worker::sendObject);
				while(true) 
				{
					Object received = worker.receiveObject();
					if (received instanceof MovedProgram)
					{
						input.disableOther();
						MovedProgram<OutputT, InputT> moved = (MovedProgram<OutputT, InputT>) received;
						workerLoc = moved.getNewLoc();
						toSend = new ProgramPacket<>(id, moved.getMoved());
						break;
					}
					else
					{
						program.handleResult((OutputT) received);
					}
				}
			}
		}catch (EOFException e)
		{
			// Program is complete.
		}
		finally
		{
			if (worker != null)
				worker.close();
			worker = null;
		}
	}
	
	public <OutputT extends Serializable, InputT extends Serializable> 
		void invokeMethod(PauseableProgram<OutputT, InputT> program) throws IOException
	{
		invokeMethod(program, gen.nextInt());
	}

	public void close() throws IOException
	{
	}
}
