package rebound.io.packeted;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import javax.annotation.Nonnegative;
import rebound.annotations.semantic.allowedoperations.ReadonlyValue;
import rebound.exceptions.ClosedIOException;

public interface OutputPacketream
extends Closeable
{
	/**
	 * @return the length of the written packet (possibly 0!)
	 */
	public @Nonnegative int send(@ReadonlyValue byte[] array, @Nonnegative int offset, @Nonnegative int length) throws IOException, EOFException, ClosedIOException;
}
