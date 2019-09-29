/*
 * Created on Feb 10, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.exceptions;

import rebound.annotations.semantic.reachability.ThrowAwayValue;

public class DuplicatesException
extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	protected final Object[] duplicates;
	
	
	public DuplicatesException(String message, Throwable cause, Object... duplicates)
	{
		super(message, cause);
		this.duplicates = duplicates == null ? null : duplicates.clone();
	}
	
	public DuplicatesException()
	{
		this(null, null, (Object[])null);
	}
	
	public DuplicatesException(String message)
	{
		this(message, null, (Object[])null);
	}
	
	public DuplicatesException(String message, Object... duplicates)
	{
		this(message, null, duplicates);
	}
	
	
	public DuplicatesException(Object... duplicates)
	{
		this(null, null, duplicates);
	}
	
	public DuplicatesException(Throwable cause, Object... duplicates)
	{
		this(null, cause, duplicates);
	}
	
	
	
	
	@ThrowAwayValue
	public Object[] getDuplicatesClone()
	{
		return this.duplicates == null ? null : duplicates.clone();
	}
}
