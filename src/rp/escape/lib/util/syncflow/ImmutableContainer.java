package rp.escape.lib.util.syncflow;

public class ImmutableContainer<T>
implements ReadableContainer<T>
//NOT a ContentsHookingContainer!! :)
{
	protected final T value;
	
	public ImmutableContainer(T value)
	{
		super();
		this.value = value;
	}
	
	@Override
	public T get()
	{
		return value;
	}
}
