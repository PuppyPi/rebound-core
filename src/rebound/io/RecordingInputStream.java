package rebound.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class RecordingInputStream
extends InputStream
{
	protected final InputStream underlying;
	protected ByteArrayOutputStream buff = new ByteArrayOutputStream();
	
	public RecordingInputStream(InputStream in)
	{
		this.underlying = in;
	}
	
	public byte[] recordToByteArray()
	{
		return buff.toByteArray();
	}
	
	public void discardRecord()
	{
		buff.reset();
	}
	
	public void stopRecording()
	{
		buff = null;
	}
	
	
	@Override
	public int read() throws IOException
	{
		int r = underlying.read();
		if (r != -1 && buff != null)
			buff.write(r);
		return r;
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException
	{
		int r = underlying.read(b, off, len);
		if (r != -1 && r > 0 && buff != null)
			buff.write(b, off, r);
		return r;
	}
	
	@Override
	public int available() throws IOException
	{
		return underlying.available();
	}
	
	@Override
	public void close() throws IOException
	{
		underlying.close();
	}
}
