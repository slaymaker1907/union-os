package os.union;

import java.io.Serializable;
import java.io.IOException;

public interface RemoteInput <T extends Serializable>
{
	public void sendInput(T toSend) throws IOException;
}
