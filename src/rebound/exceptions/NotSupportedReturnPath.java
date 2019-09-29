/*
 * Created on Jan 29, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.exceptions;

import rebound.exceptions.ReturnPath.SingletonReturnPath;

/**
 * Note that when this (a {@link ReturnPath}) is thrown, it is very not guaranteed to have the correct stack trace!!
 * It's supposed to be an alternate return-ish value, not an actual exception ;>
 * 
 * @author Puppy Pie ^_^
 */
public class NotSupportedReturnPath
extends SingletonReturnPath
{
	private static final long serialVersionUID = 1L;
	
	public static final NotSupportedReturnPath I = new NotSupportedReturnPath();
	protected NotSupportedReturnPath() {}
	
	
	public static class NotSupportedException extends RuntimeException
	{
		private static final long serialVersionUID = 1L;
	}
	
	@Override
	public NotSupportedException toException()
	{
		return new NotSupportedException();
	}
}
