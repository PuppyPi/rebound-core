package rebound.io.iio;

import java.io.IOException;
import javax.annotation.Nonnegative;

public interface RandomAccessOutputByteStream
extends OutputByteStream, BasicRandomAccess, ResettableOutputByteStream
{
	/**
	 * Note: if we expand it, then we fill the new space with 0x00's  :3
	 */
	public void setLength(@Nonnegative long newLength) throws IOException;
}
