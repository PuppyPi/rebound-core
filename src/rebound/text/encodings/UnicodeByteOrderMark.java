package rebound.text.encodings;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import rebound.util.collections.prim.PrimitiveCollections.ImmutableByteArrayList;

public enum UnicodeByteOrderMark
{
	UTF8 (StandardCharsets.UTF_8, (byte)0xEF, (byte)0xBB, (byte)0xBF),
	UTF16LE (StandardCharsets.UTF_16LE, (byte)0xFF, (byte)0xFE),
	UTF16BE (StandardCharsets.UTF_16BE, (byte)0xFE, (byte)0xFF),
	;
	
	
	
	private final Charset encoding;
	private final ImmutableByteArrayList byteSequence;
	private UnicodeByteOrderMark(Charset encoding, byte... byteSequence)
	{
		this.encoding = encoding;
		this.byteSequence = ImmutableByteArrayList.newLIVE(byteSequence);
	}
	
	public ImmutableByteArrayList getByteSequence()
	{
		return byteSequence;
	}
	
	public Charset getEncoding()
	{
		return encoding;
	}
}
