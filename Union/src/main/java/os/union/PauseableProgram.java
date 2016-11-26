package os.union;

import java.io.Serializable;

public interface PauseableProgram<OutputT extends Serializable, InputT extends Serializable>
{
	public Program<OutputT, InputT> getProgram();
	public void handleResult(OutputT stdout);
	public void initProgram(RemoteInput<InputT> stdin);
}
