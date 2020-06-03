package rebound.io.util;

import static rebound.bits.BitfieldSafeCasts.*;
import static rebound.math.SmallIntegerMathUtilities.*;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import rebound.exceptions.ImpossibleException;

public class FixedLengthInputStreamDecorator
extends FilterInputStream
{
	protected final long lengthInBytes;
	protected long soFar = 0;
	
	public FixedLengthInputStreamDecorator(InputStream in, long lengthInBytes)
	{
		super(in);
		this.lengthInBytes = lengthInBytes;
	}
	
	
	@Override
	public int read() throws IOException
	{
		if (soFar < lengthInBytes)
		{
			int r = super.read();
			if (r == -1)
				throw new ImpossibleException("Premature input end!  Expected "+lengthInBytes+" bytes, but only got "+soFar);
			soFar++;
			return r;
		}
		else
		{
			return -1;
		}
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException
	{
		if (soFar < lengthInBytes)
		{
			int r = super.read(b, off, safeCastS64toS32(least(len, lengthInBytes - soFar)));
			if (r == -1)
				throw new ImpossibleException("Premature input end!  Expected "+lengthInBytes+" bytes, but only got "+soFar);
			soFar += r;
			return r;
		}
		else
		{
			return -1;
		}
	}
	
	@Override
	public long skip(long n) throws IOException
	{
		if (soFar < lengthInBytes)
		{
			long r = super.skip(least(n, lengthInBytes - soFar));
			if (r == -1)  //technically invalid but heck why not x'3
				throw new ImpossibleException("Premature input end!  Expected "+lengthInBytes+" bytes, but only got "+soFar);
			soFar += r;
			return r;
		}
		else
		{
			return -1;
		}
	}
}
