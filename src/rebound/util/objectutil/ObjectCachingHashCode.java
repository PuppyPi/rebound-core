package rebound.util.objectutil;

public abstract class ObjectCachingHashCode<ThisType>
{
	protected abstract int _hashCode();
	protected abstract boolean _equals(ThisType other);
	protected abstract boolean _instanceOfThisType(Object other);
	
	protected void invalidateHashCodeCache()
	{
		this.hashCodeCache = -1;
	}
	
	
	
	
	//Yeah I know it's bad practice for a thread-safe immutable object to cache things, but at best effort is wasted, no constraints are violated!     (If I'm wrong, fire me X'D )
	protected transient long hashCodeCache;  //storing a boolean too will probably get promoted on 64-bit machines anyway so, meh xD
	
	@Override
	public int hashCode()
	{
		long h = this.hashCodeCache;
		
		if (h != -1)
		{
			return (int)h;
		}
		else
		{
			int hh = this._hashCode();
			this.hashCodeCache = upcast(hh);
			return hh;
		}
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
			return true;
		else if (obj == null)
			return false;
		else if (!_instanceOfThisType(obj))
			return false;
		
		//Checking the hashcodes is a VERY fast negative test and is almost always true-negative!! :DDD   (rarely it's false-positive, but never false-negative!)
		if (this.hashCode() != obj.hashCode())
			return false;
		
		return _equals((ThisType)obj);
	}
}
