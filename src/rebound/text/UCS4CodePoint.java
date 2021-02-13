package rebound.text;

import rebound.annotations.semantic.simpledata.ActuallyUnsigned;

public class UCS4CodePoint
{
	protected final int codeUnit;
	
	public UCS4CodePoint(@ActuallyUnsigned int codeUnit)
	{
		this.codeUnit = codeUnit;
	}
	
	public @ActuallyUnsigned int getCodeUnit()
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
		UCS4CodePoint other = (UCS4CodePoint) obj;
		if (codeUnit != other.codeUnit)
			return false;
		return true;
	}
	
	@Override
	public String toString()
	{
		return "'"+StringUtilities.ucs4ToUTF16String(new int[]{this.codeUnit})+"'";
	}
}
