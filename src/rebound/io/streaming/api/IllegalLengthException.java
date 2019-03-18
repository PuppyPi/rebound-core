/*
 * Created on May 23, 2009
 * 	by the great Eclipse(c)
 */
package rebound.io.streaming.api;

import java.io.IOException;
import rebound.io.streaming.api.advanced.LengthMutableWriteStream;

/**
 * This is thrown by {@link LengthMutableWriteStream#setLength(long)} or various <code>write()</code> in {@link LengthMutableWriteStream#setAutoExtend(boolean) auto-extend} mode
 * when the length was attempted to be set to a value that it may not be set to (or an invalid value is used, such as a negative number).
 * @author RProgrammer
 */
public class IllegalLengthException
extends IOException
{
	private static final long serialVersionUID = 1L;
	
	
	
	
	public static enum Reason
	{
		/**
		 * This indicates that the provided length was negative.
		 */
		NEGATIVE ("negative!"),
		
		/**
		 * This indicates that the provided length was too small.
		 */
		TOO_SMALL ("too small"),
		
		/**
		 * This indicates that the provided length was too large.
		 */
		TOO_LARGE ("too large"),
		
		/**
		 * Example: The length must be even and an odd length was provided.<br>
		 * Example: The length must be in multiples of RECORD.length and the provided <code>length % RECORD.length != 0</code>
		 */
		BAD_MOD ("the wrong modulus"),
		
		/**
		 * This differs from <code>null</code> in that the reason is known, it's just not conveniently on the list.
		 */
		OTHER ("illegal"),
		;
		
		protected final String brief;
		
		private Reason(String brief)
		{
			this.brief = brief;
		}
		
		//		@Override
		//		public String toString()
		//		{
		//			return this.brief;
		//		}
	}
	
	
	protected final Reason reason;
	protected final long offendingLength;
	
	public IllegalLengthException(Reason reason, long offendingLength)
	{
		super();
		//super("The given length ("+offendingLength+") was "+(reason == null ? "illegal" : reason));
		this.reason = reason;
		this.offendingLength = offendingLength;
	}
	
	public IllegalLengthException(String reason, long offendingLength)
	{
		super();
		//super("The given length ("+offendingLength+") was "+(reason == null ? "illegal" : reason));
		this.reason = Reason.OTHER;
		this.offendingLength = offendingLength;
	}
	
	
	/**
	 * This is a utility method to check bounds on {@link LengthMutableWriteStream#setLength(long)}.
	 * @param minimum The smallest value that won't throw an {@link IllegalLengthException}
	 * @param maximum The largest value that won't throw an {@link IllegalLengthException}
	 * @param providedValue The actual value provided to {@link LengthMutableWriteStream#setLength(long)}
	 * @throws IllegalLengthException If the <code>providedValue</code> is negative or out of bounds
	 */
	public static void checkLength(long minimum, long maximum, long providedValue) throws IllegalLengthException
	{
		if (providedValue < 0)
			throw new IllegalLengthException(Reason.NEGATIVE, providedValue);
		
		if (providedValue < minimum)
			throw new IllegalLengthException(Reason.TOO_SMALL, providedValue);
		
		if (providedValue > maximum)
			throw new IllegalLengthException(Reason.TOO_LARGE, providedValue);
	}
	
	
	/**
	 * The {@link Reason} that this exception was thrown.
	 * This may be <code>null</code>, indicating that the reason is not known.
	 */
	public Reason getReason()
	{
		return this.reason;
	}
	
	/**
	 * This is the actual length provided to {@link LengthMutableWriteStream#setLength(long)}
	 * @return The
	 */
	public long getOffendingLength()
	{
		return this.offendingLength;
	}
}
