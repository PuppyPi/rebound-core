package rebound.util;

import static java.util.Objects.*;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import rebound.annotations.semantic.simpledata.Nonempty;

@NotThreadSafe
public abstract class AbstractEventListenerMulticaster<Listener>
implements EventListenerMulticaster<Listener>
{
	protected @Nullable Listener a;
	protected @Nullable Listener b;
	protected @Nullable Listener c;
	protected @Nullable Listener d;
	protected @Nullable Listener e;
	protected @Nullable Listener f;
	protected @Nullable Listener g;
	protected @Nullable Listener h;
	protected @Nullable @Nonempty Listener[] others;
	
	
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
	public Listener getSingleListenerIfSingletonOrNullOtherwise()
	{
		Listener got = null;
		
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
				Listener l = others[i];
				
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
	public void add(Listener listener)
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
				Listener[] newOthers = newListenerArray(no + no / 2 + 8);
				
				if (no > 0)
					System.arraycopy(others, 0, newOthers, 0, no);
				
				newOthers[no] = listener;
			}
		}
	}
	
	
	@Override
	public void remove(Listener listener)
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
	
	
	public boolean contains(Listener listener)
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
	
	
	protected void fire()
	{
		if (a != null)  fireListener(a);
		if (b != null)  fireListener(b);
		if (c != null)  fireListener(c);
		if (d != null)  fireListener(d);
		if (e != null)  fireListener(e);
		if (f != null)  fireListener(f);
		if (g != null)  fireListener(g);
		if (h != null)  fireListener(h);
		
		if (others != null)
		{
			int no = others.length;
			for (int i = 0; i < no; i++)
			{
				Listener l = others[i];
				if (l != null)
					fireListener(l);
			}		
		}
	}
	
	
	protected abstract void fireListener(Listener l);
	protected abstract Listener[] newListenerArray(int length);
}
