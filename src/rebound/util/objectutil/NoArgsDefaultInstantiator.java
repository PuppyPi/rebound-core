package rebound.util.objectutil;

import rebound.exceptions.WrappedThrowableRuntimeException;

public class NoArgsDefaultInstantiator<T>
implements Instantiator<T>
{
	protected final Class<T> tehClass;
	
	public NoArgsDefaultInstantiator(Class<T> tehClass)
	{
		super();
		this.tehClass = tehClass;
	}
	
	public Class<T> getClassToInstantiate()
	{
		return this.tehClass;
	}
	
	@Override
	public T newInstance() throws WrappedThrowableRuntimeException
	{
		try
		{
			return this.tehClass.newInstance();
		}
		catch (InstantiationException exc)
		{
			throw new WrappedThrowableRuntimeException(exc);
		}
		catch (IllegalAccessException exc)
		{
			throw new WrappedThrowableRuntimeException(exc);
		}
		
		//Note: InvocationTargetException is unwrapped in newInstance():   from jdk1.7.0_06: "Unsafe.getUnsafe().throwException(e.getTargetException());"
	}
}