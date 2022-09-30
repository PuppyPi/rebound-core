/*
 * Created on Jan 29, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.exceptions;

import rebound.exceptions.ReturnPath.SingletonReturnPath;

public class WouldBlockReturnPath
extends SingletonReturnPath
{
	private static final long serialVersionUID = 1L;
	
	public static final WouldBlockReturnPath I = new WouldBlockReturnPath();
	
	protected WouldBlockReturnPath() {}
	
	public static class WouldBlockException
	extends RuntimeException
	{
		private static final long serialVersionUID = 1L;
	}
	
	public WouldBlockException toException()
	{
		return new WouldBlockException();
	}
}
