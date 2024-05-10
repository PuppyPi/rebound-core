package rebound.util;

import javax.annotation.concurrent.NotThreadSafe;
import rebound.util.container.ContainerInterfaces.ObjectContainer;

@NotThreadSafe
public class SimpleNullaryEventListenerMulticaster
extends AbstractNullaryEventListenerMulticaster<Runnable>
implements Runnable
{
	public static void addToTarget(Runnable listener, ObjectContainer<Runnable> targetListenable)
	{
		EventListenerMulticaster.addToTarget(listener, () -> new SimpleNullaryEventListenerMulticaster(), targetListenable);
	}
	
	public static void removeFromTarget(Runnable listener, ObjectContainer<Runnable> targetListenable)
	{
		EventListenerMulticaster.removeFromTarget(listener, targetListenable);
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
