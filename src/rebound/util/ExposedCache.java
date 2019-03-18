package rebound.util;

public interface ExposedCache<C>
extends Cache
{
	public C getCache();
}