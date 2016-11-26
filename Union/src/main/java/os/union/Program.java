package os.union;

import java.io.Serializable;

public interface Program <OutputT extends Serializable, InputT extends Serializable> extends Serializable
{
	public void feedInput(InputT input);
	public boolean computationComplete();
	public void computeNext(ResultHandler<OutputT> handler);
}
