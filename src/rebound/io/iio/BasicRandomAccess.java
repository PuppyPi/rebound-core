package rebound.io.iio;

import java.io.IOException;
import javax.annotation.Nonnegative;

public interface BasicRandomAccess
extends ResettableAccess
{
	public @Nonnegative long getFilePointer() throws IOException;
	public @Nonnegative long length() throws IOException;
	public void seek(@Nonnegative long newFilePointerPosition) throws IOException;
	
	
	@Override
	public default void seekToStart() throws IOException
	{
		seek(0);
	}
}
