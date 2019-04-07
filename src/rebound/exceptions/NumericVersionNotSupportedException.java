/*
 * Created on Feb 21, 2005
 * 	by the wonderful Eclipse(c)
 */
package rebound.exceptions;

/**
 * @author sean
 */
public class NumericVersionNotSupportedException
extends Exception
{
	private static final long serialVersionUID = 1L;
	
	protected double unsupported = 0.0;
	protected double maxsupported = 0.0;
	
	public NumericVersionNotSupportedException()
	{
		super();
		this.unsupported = Double.NaN;
		this.maxsupported = Double.NaN;
	}
	
	public NumericVersionNotSupportedException(double offending, double max)
	{
		super("Version "+offending+" not supported; only up to "+max+" is supported.");
		this.unsupported = offending;
		this.maxsupported = max;
	}
	
	public NumericVersionNotSupportedException(String message, double offending, double max)
	{
		super(message);
		this.unsupported = offending;
		this.maxsupported = max;
	}
	
	public NumericVersionNotSupportedException(String message, Throwable cause, double offending, double max)
	{
		super(message, cause);
		this.unsupported = offending;
		this.maxsupported = max;
	}
	
	public NumericVersionNotSupportedException(Throwable cause, double offending, double max)
	{
		super(cause);
		this.unsupported = offending;
		this.maxsupported = max;
	}
	
	
	public double getOffendingVersion()
	{
		return this.unsupported;
	}
	
	public double getMaxSupported()
	{
		return this.maxsupported;
	}
}
