package rebound.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DuplicatingInputStream
extends FilterInputStream
{
	protected final OutputStream duplicatee;
	
	public DuplicatingInputStream(InputStream underlying, OutputStream duplicatee)
	{
		super(underlying);
		this.duplicatee = duplicatee;
	}

	@Override
	public int read() throws IOException
	{
		int r = super.read();
		if (r != -1)
			duplicatee.write(r);
		return r;
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException
	{
		int r = super.read(b, off, len);
		if (r != -1)
			duplicatee.write(b, off, r);
		return r;
	}
	
	@Override
	public void close() throws IOException
	{
		super.close();
		duplicatee.close();
	}
}
