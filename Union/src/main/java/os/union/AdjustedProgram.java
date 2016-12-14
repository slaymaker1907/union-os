package os.union;

import java.io.Serializable;

public class AdjustedProgram <OutputT extends Serializable, InputT extends Serializable> implements PauseableProgram<OutputT, InputT>
{
	private PauseableProgram<OutputT, InputT> origin;
	private Program<OutputT, InputT> newProgram;
	
	public AdjustedProgram(PauseableProgram<OutputT, InputT> origin, Program<OutputT, InputT> newProgram)
	{
		this.origin = origin;
		this.newProgram = newProgram;
	}

	@Override
	public Program<OutputT, InputT> getProgram()
	{
		return this.newProgram;
	}

	@Override
	public void handleResult(OutputT stdout)
	{
		this.origin.handleResult(stdout);
	}

	@Override
	public void initProgram(RemoteInput<InputT> stdin)
	{
		this.origin.initProgram(stdin);
	}

}
