package rebound.util;

import javax.annotation.concurrent.NotThreadSafe;
import rebound.util.container.ContainerInterfaces.ObjectContainer;

@NotThreadSafe
public class SimpleNullaryEventListenerMulticaster
extends AbstractEventListenerMulticaster<Runnable>
implements Runnable
{
	public static void addToTarget(ObjectContainer<Runnable> targetListenable, Runnable listener)
	{
		EventListenerMulticaster.addToTarget(targetListenable, listener, () -> new SimpleNullaryEventListenerMulticaster());
	}
	
	public static void removeFromTarget(ObjectContainer<Runnable> targetListenable, Runnable listener)
	{
		EventListenerMulticaster.removeFromTarget(targetListenable, listener);
	}
	
	
	
	
	@Override
	public void run()
	{
		fire();
	}
	
	@Override
	protected void fireListener(Runnable l)
	{
		l.run();
	}
	
	@Override
	protected Runnable[] newListenerArray(int length)
	{
		return new Runnable[length];
	}
}
