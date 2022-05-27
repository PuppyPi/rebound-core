package rebound.io;
import java.util.List;

public interface CloseableList<E>
extends List<E>, UncheckedCloseable  //the other methods don't throw IOException X3
{
}
