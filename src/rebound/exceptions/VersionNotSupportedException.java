/*
 * Created on Feb 21, 2005
 * 	by the wonderful Eclipse(c)
 */
package rebound.exceptions;

/**
 * @author sean
 */
public class VersionNotSupportedException
extends Exception
{
	private static final long serialVersionUID = 1L;
	
	protected double unsupported = 0.0;
	protected double maxsupported = 0.0;
	
	public VersionNotSupportedException()
	{
		super();
		this.unsupported = Double.NaN;
		this.maxsupported = Double.NaN;
	}
	
	public VersionNotSupportedException(double offending, double max)
	{
		super("Version "+offending+" not supported; only up to "+max+" is supported.");
		this.unsupported = offending;
		this.maxsupported = max;
	}
	
	public VersionNotSupportedException(String message, double offending, double max)
	{
		super(message);
		this.unsupported = offending;
		this.maxsupported = max;
	}
	
	public VersionNotSupportedException(String message, Throwable cause, double offending, double max)
	{
		super(message, cause);
		this.unsupported = offending;
		this.maxsupported = max;
	}
	
	public VersionNotSupportedException(Throwable cause, double offending, double max)
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
