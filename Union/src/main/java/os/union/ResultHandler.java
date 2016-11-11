package os.union;

import java.io.Serializable;

public interface ResultHandler <T extends Serializable>
{
	public void receiveResult(T result);
}
