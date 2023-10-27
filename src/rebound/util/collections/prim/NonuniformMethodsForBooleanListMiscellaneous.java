package rebound.util.collections.prim;

import java.math.BigInteger;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import rebound.annotations.hints.ImplementationTransparency;
import rebound.annotations.semantic.allowedoperations.WritableValue;
import rebound.annotations.semantic.simpledata.BoundedInt;
import rebound.annotations.semantic.simpledata.Emptyable;
import rebound.annotations.semantic.simpledata.Nonempty;
import rebound.exceptions.OverflowException;
import rebound.math.SmallIntegerMathUtilities;
import rebound.text.StringUtilities;
import rebound.util.collections.prim.PrimitiveCollections.BooleanList;
import rebound.util.collections.prim.PrimitiveCollections.DefaultToArraysBooleanCollection;

@ImplementationTransparency  //This only exists to keep the bulk functions exactly uniform and correspondent :3     This might be removed soon XD''
public interface NonuniformMethodsForBooleanListMiscellaneous
extends DefaultToArraysBooleanCollection
{
	public boolean getBoolean(@Nonnegative int index);
	public void setBoolean(@Nonnegative int index, boolean value);
	public long getBitfield(@Nonnegative int offset, @BoundedInt(min=1, max=64) int length);
	
	
	
	public default long getEntireByBitfield()
	{
		int s = size();
		
		if (s > 64)
			throw new OverflowException();
		
		return getBitfield(0, s);
	}
	
	
	
	
	
	
	
	
	
	public default @Nonnull BigInteger toBigIntegerLE()
	{
		BigInteger bigInteger = BigInteger.ZERO;
		
		int sizeInBits = size();
		
		final int wordLength = SmallIntegerMathUtilities.ceilingDivision(sizeInBits, 64);
		for (int wordIndex = 0; wordIndex < wordLength; wordIndex++)
		{
			final int bitsUsedInWord = wordIndex < wordLength - 1 ? 64 : sizeInBits - (wordIndex * 64);
			assert bitsUsedInWord > 0 && bitsUsedInWord <= 64;
			
			final long maskedWord = getBitfield(wordIndex * 64, bitsUsedInWord);
			
			BigInteger operand = BigInteger.valueOf(maskedWord);
			operand = operand.shiftLeft(wordIndex * 64);
			bigInteger = bigInteger.or(operand);
		}
		
		return bigInteger;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public default String toBinaryStringLE(@Nonnegative long regionLength, @Emptyable @Nonnull String delimiter)
	{
		return toBinaryStringLE(regionLength, delimiter, "0", "1");
	}
	
	public default String toBinaryStringLE(@Nonnegative long regionLength, @Emptyable @Nonnull String delimiter, @Nonempty @Nonnull String zeroRepr, @Nonempty @Nonnull String oneRepr)
	{
		return toBinaryStringLE(new long[]{regionLength}, new String[]{delimiter}, zeroRepr, oneRepr);
	}
	
	public default String toBinaryStringLENoDelimiters(@Nonnull String zeroRepr, @Emptyable @Nonnull String oneRepr)
	{
		return toBinaryStringLE(null, null, zeroRepr, oneRepr);
	}
	
	public default String toBinaryStringLENoDelimiters()
	{
		return toBinaryStringLENoDelimiters("0", "1");
	}
	
	public default String toBinaryStringLE(@Nullable long[] regionLengths, @Nullable String[] delimiters, @Nonempty @Nonnull String zeroRepr, @Nonempty @Nonnull String oneRepr)
	{
		StringBuilder rv = new StringBuilder();
		
		for (int i = 0; i < size(); i++)
		{
			boolean bit = getBoolean(i);
			rv.append(bit ? oneRepr : zeroRepr);
			
			if (regionLengths != null && regionLengths.length != 0)
			{
				int regionIndexOfLongestMatchingRegion = 0;
				{
					regionIndexOfLongestMatchingRegion = -1;
					for (int e = 0; e < regionLengths.length; e++)
					{
						if ((i+1) % regionLengths[e] == 0)
						{
							if (regionIndexOfLongestMatchingRegion == -1 || regionLengths[e] > regionLengths[regionIndexOfLongestMatchingRegion])
								regionIndexOfLongestMatchingRegion = e;
						}
					}
				}
				
				if (regionIndexOfLongestMatchingRegion != -1 && i < size()-1)
				{
					rv.append(delimiters[regionIndexOfLongestMatchingRegion]);
				}
			}
		}
		
		return rv.toString();
	}
	
	
	
	
	
	public default void unsignedIntegerToStringBE(@BoundedInt(min=2, max=36) int radix, @WritableValue @Nonnull StringBuilder buff)
	{
		if (size() <= 63)
		{
			buff.append(Long.toString(getEntireByBitfield(), radix));
		}
		else if (size() == 64)
		{
			buff.append(StringUtilities.toStringU64(getEntireByBitfield(), radix));
		}
		else
		{
			//TODO! BETTER IMPL!
			BigInteger bi = toBigIntegerLE();
			buff.append(bi.toString(radix));
		}
	}
	
	public default String unsignedIntegerToStringBE(@BoundedInt(min=2, max=36) int radix)
	{
		if (size() <= 63)
		{
			return Long.toString(getEntireByBitfield(), radix);
		}
		else if (size() == 64)
		{
			return StringUtilities.toStringU64(getEntireByBitfield(), radix);
		}
		else
		{
			StringBuilder buff = new StringBuilder();
			unsignedIntegerToStringBE(radix, buff);
			return buff.toString();
		}
	}
	
	
	
	
	public default String _toString()
	{
		return PrimitiveCollections.defaultBooleanListToString((BooleanList) this);
	}
}
