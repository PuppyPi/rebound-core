package rebound.util;

import static java.util.Objects.*;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import rebound.annotations.semantic.simpledata.Nonempty;
import rebound.util.container.ContainerInterfaces.ObjectContainer;

@NotThreadSafe
public class SimpleNullaryEventListenerMulticaster
implements EventListenerMulticaster<Runnable>, Runnable
{
	protected @Nullable Runnable a;
	protected @Nullable Runnable b;
	protected @Nullable Runnable c;
	protected @Nullable Runnable d;
	protected @Nullable Runnable e;
	protected @Nullable Runnable f;
	protected @Nullable Runnable g;
	protected @Nullable Runnable h;
	protected @Nullable @Nonempty Runnable[] others;
	
	
	@Override
	public int getSize()
	{
		//fyi, boolean ? 1 : 0 (should) compile to just using the boolean int value!  so that way there's no conditionals and thus no branching, so CPU pipelining works perfectly :>   (#TeachableMoments XD )
		int n = a != null ? 1 : 0;
		n += b != null ? 1 : 0;
		n += c != null ? 1 : 0;
		n += d != null ? 1 : 0;
		n += e != null ? 1 : 0;
		n += f != null ? 1 : 0;
		n += g != null ? 1 : 0;
		n += h != null ? 1 : 0;
		
		if (others != null)
		{
			int no = others.length;
			for (int i = 0; i < no; i++)
				if (others[i] != null)
					n++;
		}
		
		return n;
	}
	
	@Override
	public Runnable getSingleListenerIfSingletonOrNullOtherwise()
	{
		Runnable got = null;
		
		if (a != null)
			got = a;
		
		if (b != null)
		{
			if (got == null)
				got = b;
			else
				return null;
		}
		
		if (c != null)
		{
			if (got == null)
				got = c;
			else
				return null;
		}
		
		if (d != null)
		{
			if (got == null)
				got = d;
			else
				return null;
		}
		
		if (e != null)
		{
			if (got == null)
				got = e;
			else
				return null;
		}
		
		if (f != null)
		{
			if (got == null)
				got = f;
			else
				return null;
		}
		
		if (g != null)
		{
			if (got == null)
				got = g;
			else
				return null;
		}
		
		if (h != null)
		{
			if (got == null)
				got = h;
			else
				return null;
		}
		
		if (others != null)
		{
			int no = others.length;
			for (int i = 0; i < no; i++)
			{
				Runnable l = others[i];
				
				if (l != null)
				{
					if (got == null)
						got = l;
					else
						return null;
				}
			}
		}
		
		return got;  //possibly will be null X3
	}
	
	
	@Override
	public void add(Runnable listener)
	{
		requireNonNull(listener);
		
		if (contains(listener))  //if you want more performance and less safety, you can copy this class and simply comment out this safety check!
			return;
		
		if (a == null)  a = listener;
		else if (b == null)  b = listener;
		else if (c == null)  c = listener;
		else if (d == null)  d = listener;
		else if (e == null)  e = listener;
		else if (f == null)  f = listener;
		else if (g == null)  g = listener;
		else if (h == null)  h = listener;
		else
		{
			int no;
			if (others != null)
			{
				no = others.length;
				
				assert no > 0;
				
				for (int i = 0; i < no; i++)
				{
					if (others[i] == null)
					{
						others[i] = listener;
						return;
					}
				}
			}
			else
			{
				no = 0;
			}
			
			//Otherwise it's alllll full!!
			{
				Runnable[] newOthers = new Runnable[no + no / 2 + 8];
				
				if (no > 0)
					System.arraycopy(others, 0, newOthers, 0, no);
				
				newOthers[no] = listener;
			}
		}
	}
	
	
	@Override
	public void remove(Runnable listener)
	{
		requireNonNull(listener);
		
		if (a == listener)  a = null;
		else if (b == listener)  b = null;
		else if (c == listener)  c = null;
		else if (d == listener)  d = null;
		else if (e == listener)  e = null;
		else if (f == listener)  f = null;
		else if (g == listener)  g = null;
		else if (h == listener)  h = null;
		else if (others != null)
		{
			int no = others.length;
			for (int i = 0; i < no; i++)
			{
				if (others[i] == listener)
				{
					others[i] = null;
					return;
				}
			}
			
			//If we shrink the array down to nothing, we might as well leave it there for the future, since they're obviously using a lot of Listeners!!
		}
	}
	
	
	public boolean contains(Runnable listener)
	{
		requireNonNull(listener);
		
		if (a == listener)  return true;
		else if (b == listener)  return true;
		else if (c == listener)  return true;
		else if (d == listener)  return true;
		else if (e == listener)  return true;
		else if (f == listener)  return true;
		else if (g == listener)  return true;
		else if (h == listener)  return true;
		else if (others != null)
		{
			int no = others.length;
			for (int i = 0; i < no; i++)
				if (others[i] == listener)
					return true;
		}
		
		return false;
	}
	
	
	@Override
	public void run()
	{
		if (a != null)  a.run();
		if (b != null)  b.run();
		if (c != null)  c.run();
		if (d != null)  d.run();
		if (e != null)  e.run();
		if (f != null)  f.run();
		if (g != null)  g.run();
		if (h != null)  h.run();
		
		if (others != null)
		{
			int no = others.length;
			for (int i = 0; i < no; i++)
			{
				Runnable l = others[i];
				if (l != null)
					l.run();
			}		
		}
	}
	
	
	
	
	
	
	
	public static void addToTarget(ObjectContainer<Runnable> targetListenable, Runnable listener)
	{
		EventListenerMulticaster.addToTarget(targetListenable, listener, () -> new SimpleNullaryEventListenerMulticaster());
	}
	
	public static void removeFromTarget(ObjectContainer<Runnable> targetListenable, Runnable listener)
	{
		EventListenerMulticaster.removeFromTarget(targetListenable, listener);
	}
}
