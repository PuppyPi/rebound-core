package rebound.bits;

import javax.annotation.concurrent.Immutable;
import rebound.util.collections.Slice;

/**
 * This is for use by maximally optimized protocol implementations, where *every* bit is utilized (as much as possible, at least).
 * The problem these protocols run into is that, when software is layered on top of each other (in lieu of proper bit-based I/O and memory models instead of the byte-based ones we're stuck with in Java), they need to pass byte[]s to each other.
 * These byte[]s can often be super-optimized by using a {@link Slice}&lt;byte[]&gt; of just the payload portion without header/footer data!
 * But if there is some data in the header/footer that doesn't take up a whole byte on either side (or is in the wrong place), then it's hard to provide that extra data and the protocol just has to be designed for it to be wasted :P
 * If those bits could be provided *alongside* the main byte[] Slice, then that alleviates the need to do messy workarounds like modifying the byte[] as it's being passed through the layers which breaks its convention of immutability (since who knows what layers are doing and passing things through!)
 */
@Immutable  //But we don't control if the underlying byte[] is immutable!!
public class ByteDataWithExtraBits
{
	protected final int extraBits;
	protected final int extraBitsLengthInBits;
	protected final byte[] mainDataUnderlying;
	protected final int mainDataOffset;
	protected final int mainDataLength;
	
	public ByteDataWithExtraBits(int extraBits, int extraBitsLengthInBits, byte[] mainDataUnderlying, int mainDataOffset, int mainDataLength)
	{
		this.extraBits = extraBits;
		this.extraBitsLengthInBits = extraBitsLengthInBits;
		this.mainDataUnderlying = mainDataUnderlying;
		this.mainDataOffset = mainDataOffset;
		this.mainDataLength = mainDataLength;
	}
	
	public ByteDataWithExtraBits(int extraBits, int extraBitsLengthInBits, byte[] mainDataUnderlying)
	{
		this(extraBits, extraBitsLengthInBits, mainDataUnderlying, 0, mainDataUnderlying.length);
	}
	
	public ByteDataWithExtraBits(int extraBits, int extraBitsLengthInBits, Slice<byte[]> mainData)
	{
		this(extraBits, extraBitsLengthInBits, mainData.getUnderlying(), mainData.getOffset(), mainData.getLength());
	}
	
	
	public int getExtraBits()
	{
		return extraBits;
	}
	
	public int getExtraBitsLengthInBits()
	{
		return extraBitsLengthInBits;
	}
	
	public byte[] getMainDataUnderlying()
	{
		return mainDataUnderlying;
	}
	
	public int getMainDataOffset()
	{
		return mainDataOffset;
	}
	
	public int getMainDataLength()
	{
		return mainDataLength;
	}
	
	
	public Slice<byte[]> getMainData()
	{
		return new Slice<byte[]>(mainDataUnderlying, mainDataOffset, mainDataLength);
	}
}
