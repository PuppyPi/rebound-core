package rebound.text;

import rebound.annotations.semantic.simpledata.ActuallyUnsigned;

/**
 * 7-bit ASCII code point :>
 */
public class ASCIICodePoint
{
	protected final byte codeUnit;
	
	public ASCIICodePoint(@ActuallyUnsigned byte codeUnit)
	{
		if (codeUnit < 0)
			throw new IllegalArgumentException("This is only for 7-bit ASCII.");
		
		this.codeUnit = codeUnit;
	}
	
	public @ActuallyUnsigned byte getCodeUnit()
	{
		return codeUnit;
	}
	
	
	/**
	 * This will always be just the codepoint (other code may rely on this being true!!)
	 */
	@Override
	public int hashCode()
	{
		return codeUnit;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ASCIICodePoint other = (ASCIICodePoint) obj;
		if (codeUnit != other.codeUnit)
			return false;
		return true;
	}
	
	@Override
	public String toString()
	{
		return "'"+((char)codeUnit)+"'";
	}
}
