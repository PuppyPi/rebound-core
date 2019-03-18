/*
 * Created on Jan 3, 2012
 * 	by the great Eclipse(c)
 */
package rebound.util.collections;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.IdentityHashMap;
import rebound.io.StandardExternalizationFormats;

public class IdentityHashSet<E>
extends MapSet<E, E>
implements RuntimeWriteabilityCollection, RuntimeReadabilityCollection, Externalizable
{
	private static final long serialVersionUID = 1L;
	
	
	
	
	public IdentityHashSet()
	{
		super(new IdentityHashMap<E, E>());
	}
	
	public IdentityHashSet(Iterable<E> initialContents)
	{
		super(new IdentityHashMap<E, E>());
		addAll(initialContents);
	}
	
	public IdentityHashSet(int expectedMaxSize)
	{
		super(new IdentityHashMap<E, E>(expectedMaxSize));
	}
	
	
	@Override
	public boolean isReadableCollection()
	{
		return true;
	}
	
	@Override
	public boolean isWritableCollection()
	{
		return true;
	}
	
	
	
	
	
	
	
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
	{
		int version = in.readInt();
		if (version != 0)
			throw new UnsupportedOperationException();
		
		StandardExternalizationFormats.readExternalSet(this, in);
	}
	
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException
	{
		out.writeInt(0);
		StandardExternalizationFormats.writeExternalSet(this, out);
	}
}
