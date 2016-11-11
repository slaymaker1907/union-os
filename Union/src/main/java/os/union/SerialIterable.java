package os.union;

import java.io.Serializable;
import java.util.Iterator;

public interface SerialIterable <T extends Serializable> extends Serializable, Iterator<T>
{

}
