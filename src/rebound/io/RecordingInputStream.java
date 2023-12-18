package rebound.io;

import static rebound.util.collections.ArrayUtilities.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import rebound.annotations.semantic.reachability.ThrowAwayValue;

public class RecordingInputStream
extends InputStream
{
	protected final InputStream underlying;
	protected boolean recording;
	protected ByteArrayOutputStream buff = new ByteArrayOutputStream();
	
	public RecordingInputStream(InputStream in)
	{
		this.underlying = in;
	}
	
	@ThrowAwayValue
	public byte[] recordToByteArray()
	{
		return buff == null ? EmptyByteArray : buff.toByteArray();
	}
	
	public void discardRecord()
	{
		buff.reset();
	}
	
	public void discardRecordHintingForever()
	{
		buff = null;
	}
	
	public boolean isRecording()
	{
		return recording;
	}
	
	public void setRecording(boolean recording)
	{
		this.recording = recording;
	}
	
	
	
	@Override
	public int read() throws IOException
	{
		int r = underlying.read();
		if (r != -1 && recording)
			getOrCreateBuffer().write(r);
		return r;
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException
	{
		int r = underlying.read(b, off, len);
		if (r != -1 && r > 0 && recording)
			getOrCreateBuffer().write(b, off, r);
		return r;
	}
	
	protected ByteArrayOutputStream getOrCreateBuffer()
	{
		if (buff == null)
			buff = new ByteArrayOutputStream();
		return buff;
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
