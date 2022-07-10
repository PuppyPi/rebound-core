package rebound.io;

import java.io.FilterReader;
import java.io.Reader;

public class CloseIgnoringReader
extends FilterReader
{
	public CloseIgnoringReader(Reader in)
	{
		super(in);
	}
	
	@Override
	public void close()
	{
	}
}
