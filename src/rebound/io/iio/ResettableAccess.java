package rebound.io.iio;

import java.io.IOException;

public interface ResettableAccess
{
	public void seekToStart() throws IOException;
}
