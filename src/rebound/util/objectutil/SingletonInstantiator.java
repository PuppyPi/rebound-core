package rebound.util.objectutil;

public class SingletonInstantiator<T>
implements Instantiator<T>
{
	protected final T tehSingleton;
	
	public SingletonInstantiator(T tehSingleton)
	{
		super();
		this.tehSingleton = tehSingleton;
	}
	
	public T getTheSingleton()
	{
		return this.tehSingleton;
	}
	
	@Override
	public T newInstance()
	{
		return this.tehSingleton;
	}
}