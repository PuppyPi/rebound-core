package rebound.util;

import rebound.util.uid.UIDUtilities;

public class RUID128
{
	protected final long lowBits, highBits;
	
	public RUID128(long lowBits, long highBits)
	{
		this.lowBits = lowBits;
		this.highBits = highBits;
	}
	
	public long getLowBits()
	{
		return lowBits;
	}
	
	public long getHighBits()
	{
		return highBits;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (highBits ^ (highBits >>> 32));
		result = prime * result + (int) (lowBits ^ (lowBits >>> 32));
		return result;
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
		RUID128 other = (RUID128) obj;
		if (highBits != other.highBits)
			return false;
		if (lowBits != other.lowBits)
			return false;
		return true;
	}
	
	@Override
	public String toString()
	{
		return UIDUtilities.formatRUID128(this);
	}
}
