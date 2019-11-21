package rebound.io.packeted;

import java.io.EOFException;
import java.io.IOException;
import javax.annotation.Nonnegative;
import rebound.exceptions.ClosedIOException;

public interface OutputPacketream
{
	/**
	 * @return the length of the written packet (possibly 0!)
	 */
	public @Nonnegative int write(byte[] array, int offset, int length) throws IOException, EOFException, ClosedIOException;
}
