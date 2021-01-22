package rebound.io;

import static rebound.math.SmallIntegerMathUtilities.*;
import static rebound.testing.WidespreadTestingUtilities.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import rebound.annotations.hints.ImplementationTransparency;
import rebound.annotations.semantic.temporal.ConstantReturnValue;
import rebound.io.util.JRECompatIOUtilities;
import from.java.io.forr.rebound.io.iio.ByteArrayInputByteStream;

@NotThreadSafe
public class HeadReopenableInputStream
{
	protected InputStream underlying;
	protected InputStream streamForThem;
	protected int headSize;
	protected byte[] cache;
	
	/**
	 * @param headSize 0 = no limit, read and cache the entire stream!!
	 */
	public HeadReopenableInputStream(InputStream underlying, int headSize)
	{
		requireNonNegative(headSize);
		
		this.underlying = underlying;
		this.headSize = headSize;
		this.cache = null;
	}
	
	
	@ConstantReturnValue
	public @Nonnull InputStream stream() throws IOException
	{
		ensureHeadCached();
		asrt(streamForThem != null);
		return streamForThem;  //if null it means the entirety is cached (whether that's because we're cachingEntirety() or because the underlying data was just really small!)
	}
	
	/**
	 * @return 0 = no limit, read and cache the entire stream!!   (in which case {@link #openNewHeadStream()} provides the entire data :> )
	 */
	@ImplementationTransparency
	@Nonnegative
	public int getSize()
	{
		return headSize;
	}
	
	@ImplementationTransparency
	public boolean isCachingEntirety()
	{
		return headSize == 0;
	}
	
	
	public @Nonnull InputStream openNewHeadStream() throws IOException
	{
		ensureHeadCached();
		asrt(cache != null);
		return new ByteArrayInputByteStream(cache);
	}
	
	
	
	
	@ImplementationTransparency
	public void ensureHeadCached() throws IOException
	{
		if (this.cache == null)
		{
			if (isCachingEntirety())
			{
				cache = JRECompatIOUtilities.readAll(underlying);
				streamForThem = null;
			}
			else
			{
				//Fancy way that scares me with its fanciness (mark/reset) XD       (I tried it with PushbackReader but apparently that uses unread() not reset()!   So bah, let's do it a more clear and concise and portable way with basic API calls (not mark/reset), so that it'll always work X'D )
				{
					//				BufferedInputStream stream = new BufferedInputStream(underlying, headSize);
					//				
					//				asrt(stream.markSupported());
					//				stream.mark(headSize);
					//				cache = JRECompatIOUtilities.readAsMuchAsPossibleToNew(stream, headSize);  //this never closes the BufferedInputStream, even if it reads exactly the right amount of data, so we can still reset() it :>
					//				
					//				if (cache.length < headSize)
					//				{
					//					streamForThem = new ByteArrayInputStream(cache);
					//				}
					//				else
					//				{
					//					stream.reset();
					//					streamForThem = stream;
					//				}
				}
				
				
				//Standard simple way :3
				{
					cache = JRECompatIOUtilities.readAsMuchAsPossibleToNew(underlying, headSize);
					
					InputStream b = new ByteArrayInputStream(cache);
					
					if (cache.length < headSize)
					{
						streamForThem = b;
					}
					else
					{
						streamForThem = new SequenceInputStream(b, underlying);
					}
				}
			}
			
			asrt(cache != null);
		}
	}
}
