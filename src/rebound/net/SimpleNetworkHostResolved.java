package rebound.net;

import static java.util.Objects.*;
import rebound.exceptions.AddressLengthException;
import rebound.util.collections.prim.PrimitiveCollections.ImmutableByteArrayList;

public class SimpleNetworkHostResolved
implements SimpleNetworkHost
{
	protected final ImmutableByteArrayList address;
	
	/**
	 * @param address  The address in big-endian form ("network byte order")
	 */
	public SimpleNetworkHostResolved(ImmutableByteArrayList address)
	{
		requireNonNull(address);
		
		int n = address.size();
		if (n != 4 && n != 16)
			throw new AddressLengthException(n);
		
		this.address = address;
	}
	
	
	/**
	 * @return  The address in big-endian form ("network byte order")
	 */
	public ImmutableByteArrayList getAddress()
	{
		return address;
	}
	
	public boolean isIPv4()
	{
		return getAddress().size() == 4;
	}
	
	public boolean isIPv6()
	{
		return getAddress().size() == 16;
	}
	
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
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
		SimpleNetworkHostResolved other = (SimpleNetworkHostResolved) obj;
		if (address == null)
		{
			if (other.address != null)
				return false;
		}
		else if (!address.equals(other.address))
			return false;
		return true;
	}
	
	@Override
	public String toString()
	{
		return NetworkUtilities.formatIP(this.getAddress());
	}
}
