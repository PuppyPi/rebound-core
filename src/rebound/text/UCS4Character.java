package rebound.text;

import rebound.annotations.semantic.simpledata.ActuallyUnsigned;

public class UCS4Character
{
	protected final int codepoint;
	
	public UCS4Character(@ActuallyUnsigned int codepoint)
	{
		this.codepoint = codepoint;
	}
	
	public @ActuallyUnsigned int getCodepoint()
	{
		return codepoint;
	}
	
	
	/**
	 * This will always be just the codepoint (other code may rely on this being true!!)
	 */
	@Override
	public int hashCode()
	{
		return codepoint;
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
		UCS4Character other = (UCS4Character) obj;
		if (codepoint != other.codepoint)
			return false;
		return true;
	}
	
	@Override
	public String toString()
	{
		return "'"+StringUtilities.ucs4ToUTF16String(new int[]{this.codepoint})+"'";
	}
}
