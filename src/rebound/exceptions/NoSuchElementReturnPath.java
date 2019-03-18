/*
 * Created on Jan 29, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.exceptions;

import java.util.NoSuchElementException;
import rebound.exceptions.ReturnPath.SingletonReturnPath;

/**
 * Also tends to function as the Index Out Of Bounds return path!
 */
public class NoSuchElementReturnPath
extends SingletonReturnPath
{
	private static final long serialVersionUID = 1L;
	
	public static final NoSuchElementReturnPath I = new NoSuchElementReturnPath();
	protected NoSuchElementReturnPath() {}
	
	
	@Override
	public NoSuchElementException toException()
	{
		return new NoSuchElementException();
	}
}
