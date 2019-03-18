/*
 * Created on May 19, 2013
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util.classhacking;

public class HackedClassOrMemberUnavailableException
extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public HackedClassOrMemberUnavailableException()
	{
	}
	
	public HackedClassOrMemberUnavailableException(String message)
	{
		super(message);
	}
	
	public HackedClassOrMemberUnavailableException(Throwable cause)
	{
		super(cause);
	}
	
	public HackedClassOrMemberUnavailableException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
