/*
 * Created on Apr 22, 2008
 * 	by the wonderful Eclipse(c)
 */
package rebound.exceptions;

/**
 * This is like AssertionError, but for use when the condition is dependent on external factors being consistent (eg, the JRE, or libraries bundled with your app).
 * Especially when something must be done (eg, or you would be missing a return statement, etc.)
 * <br>
 * Examples:
 * <ul>
 * 	<li>using "UTF-8" causes UnsupportedEncodingException T-T</li>
 * 	<li>new URL("http://www.google.com/") (something you know is valid!) throws a MalformedURLException</li>
 * 	<li>this.getClass().getMethod("doRefl") somehow throws a NoSuchMethodException when that statement is inside the doRefl() method!! XD</li>
 * 	<li>System.currentTimeMillis, or nanoTime are decreasing 0,0</li>
 * 	<li>"java.home" isn't in the System property list ;_;</li>
 * 	<li>Various things return null when they are not supposed to (eg, Runtime.getRuntime())</li>
 * 	<li>You call System.exit(), but the method must technically return or throw something.</li>
 * 	<li>You call a private or static method which can only throw an exception, but the method must technically "throw" something.</li>
 * </ul>
 * @author RProgrammer
 */
public class ImpossibleException
extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public ImpossibleException()
	{
		super();
	}
	
	public ImpossibleException(Throwable cause)
	{
		super(cause);
	}
	
	public ImpossibleException(String message)
	{
		super(message);
	}
	
	public ImpossibleException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
