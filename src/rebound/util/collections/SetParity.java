package rebound.util.collections;

/**
 * If {@link #isParityInclude()} = true, then this is a passlist/whitelist; it's just the given set!
 * If it's false, then this is a blocklist/blacklist; it means everything except what's given here!
 * 
 * Empty is a passlist with an empty set.
 * Complete/Everything is a blocklist with an empty set.
 * 
 * :3
 */
public class SetParity<S>
{
	protected final S set;
	protected final boolean parityInclude;
	
	public SetParity(S set, boolean parityInclude)
	{
		this.set = set;
		this.parityInclude = parityInclude;
	}
	
	public S getSet()
	{
		return set;
	}
	
	public boolean isParityInclude()
	{
		return parityInclude;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (parityInclude ? 1231 : 1237);
		result = prime * result + ((set == null) ? 0 : set.hashCode());
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
		SetParity other = (SetParity) obj;
		if (parityInclude != other.parityInclude)
			return false;
		if (set == null)
		{
			if (other.set != null)
				return false;
		}
		else if (!set.equals(other.set))
			return false;
		return true;
	}
}
