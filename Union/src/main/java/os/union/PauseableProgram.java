package os.union;

import java.io.Serializable;
import java.util.function.Consumer;

public interface PauseableProgram<OutputT extends Serializable, InputT>
{
	public SerialIterable<OutputT> getProgram();
	public void handleResult(OutputT stdout);
	public void initProgram(Consumer<InputT> stdin);
}
