package rebound.util.collections;

public class TruncationInformation<E>
{
	protected final E value;
	protected final boolean truncated;
	
	public TruncationInformation(E value, boolean truncated)
	{
		this.value = value;
		this.truncated = truncated;
	}
	
	public E getValue()
	{
		return value;
	}
	
	public boolean isTruncated()
	{
		return truncated;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (truncated ? 1231 : 1237);
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		TruncationInformation other = (TruncationInformation) obj;
		if (truncated != other.truncated)
			return false;
		if (value == null)
		{
			if (other.value != null)
				return false;
		}
		else if (!value.equals(other.value))
			return false;
		return true;
	}
	
	@Override
	public String toString()
	{
		return value + (truncated ? "..." : "");
	}
}
