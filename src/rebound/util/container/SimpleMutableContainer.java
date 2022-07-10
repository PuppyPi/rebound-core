package rebound.util.container;

import rp.escape.lib.util.syncflow.MutableContainer;

public class SimpleMutableContainer<T>
implements MutableContainer<T>
{
	protected T contents;
	
	
	
	public SimpleMutableContainer()
	{
		super();
	}
	
	public SimpleMutableContainer(T contents)
	{
		super();
		this.contents = contents;
	}
	
	
	
	@Override
	public T get()
	{
		return contents;
	}
	
	@Override
	public void set(T v)
	{
		if (v != this.contents)
		{
			this.contents = v;
		}
	}
}
