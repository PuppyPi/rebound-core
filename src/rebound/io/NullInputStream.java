package rebound.io;

import java.io.IOException;
import java.io.InputStream;
import javax.annotation.concurrent.Immutable;

@Immutable
public class NullInputStream
extends InputStream
{
	public static final NullInputStream I = new NullInputStream();
	
	
	@Override
	public int read() throws IOException
	{
		return -1;
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException
	{
		return -1;
	}
	
	@Override
	public long skip(long n) throws IOException
	{
		return 0;  //right?!?
	}
}
