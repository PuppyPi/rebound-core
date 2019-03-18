package rebound.io.bitstream;

import java.io.EOFException;
import java.io.IOException;

//TODO Deduplicate and implement this on top of RIO :D

public interface BitInputStream
{
	/**
	 * @return true for 1, false for 0, binary :33
	 */
	public boolean read() throws EOFException, IOException;
}
