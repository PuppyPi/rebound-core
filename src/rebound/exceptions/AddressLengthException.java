/*
 * Created on Apr 28, 2008
 * 	by the wonderful Eclipse(c)
 */
package rebound.exceptions;

/**
 * Thrown when an address (such as IP) is the incorrect length.
 * @author RProgrammer
 */
public class AddressLengthException
extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	protected final int expectedLength, actualLength;
	
	public AddressLengthException()
	{
		super("Invalid network address length!");
		expectedLength = -1;
		actualLength = -1;
	}
	
	public AddressLengthException(int actualLength)
	{
		super("Invalid network address length!  IPv4 is 4 bytes and IPv6 is 16 bytes, this was "+actualLength+" bytes!");
		this.expectedLength = -1;
		this.actualLength = actualLength;
	}
	
	public AddressLengthException(int expectedLength, int actualLength)
	{
		super("Invalid network address length!  Expected "+expectedLength+" but got "+actualLength+" bytes!");
		this.expectedLength = expectedLength;
		this.actualLength = actualLength;
	}
	
	
	
	/**
	 * The expected length of the address or -1 if not known.<br>
	 */
	public int getExpectedLength()
	{
		return this.expectedLength;
	}
	
	
	/**
	 * The actual length of the address or -1 if not known.<br>
	 */
	public int getActualLength()
	{
		return this.actualLength;
	}
}
