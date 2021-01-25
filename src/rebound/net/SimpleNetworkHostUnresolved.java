package rebound.net;

import static java.util.Objects.*;

public class SimpleNetworkHostUnresolved
implements SimpleNetworkHost
{
	protected final String name;
	
	public SimpleNetworkHostUnresolved(String name)
	{
		this.name = requireNonNull(name);
	}
	
	public String getName()
	{
		return name;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		SimpleNetworkHostUnresolved other = (SimpleNetworkHostUnresolved) obj;
		if (name == null)
		{
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	@Override
	public String toString()
	{
		return name;
	}
}
