package rebound.util.uid;

import rebound.util.collections.prim.PrimitiveCollections.ImmutableByteArrayList;

public class UIDConflictExceptionWithUID
extends UIDConflictException
{
	private static final long serialVersionUID = 1L;
	
	protected final ImmutableByteArrayList uid;
	
	
	public UIDConflictExceptionWithUID(ImmutableByteArrayList uid)
	{
		super(UIDUtilities.formatString(uid));
		this.uid = uid;
	}
	
	public UIDConflictExceptionWithUID(String message, ImmutableByteArrayList uid)
	{
		super(message);
		this.uid = uid;
	}
	
	public UIDConflictExceptionWithUID(Throwable cause, ImmutableByteArrayList uid)
	{
		super(cause);
		this.uid = uid;
	}
	
	public UIDConflictExceptionWithUID(String message, Throwable cause, ImmutableByteArrayList uid)
	{
		super(message, cause);
		this.uid = uid;
	}
	
	
	public ImmutableByteArrayList getUIDWhichMoreThanOneThingPossesses()
	{
		return this.uid;
	}
}
