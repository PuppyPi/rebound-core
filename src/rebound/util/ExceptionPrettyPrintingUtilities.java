package rebound.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionPrettyPrintingUtilities
{
	public static void printStackTraceFully(Throwable t)
	{
		System.err.print(getNicelyFormattedStandardStacktrace(t));
	}
	
	public static void printStackTraceFully()
	{
		System.err.print(getNicelyFormattedStandardStacktrace(Thread.currentThread().getStackTrace()));
	}
	
	
	
	
	public static String getNicelyFormattedStandardStacktrace(StackTraceElement[] stackTrace)
	{
		return getNicelyFormattedStandardStacktrace(stackTrace, "\t");
	}
	
	public static String getNicelyFormattedStandardStacktrace(StackTraceElement[] stackTrace, String indentation)
	{
		StringBuilder b = new StringBuilder();
		
		for (StackTraceElement e : stackTrace)
		{
			b.append(indentation);
			b.append("at ");
			b.append(e);
			b.append('\n');
		}
		
		return b.toString();
	}
	
	public static String getNicelyFormattedStandardStacktrace(Throwable t)
	{
		StringBuilder buff = new StringBuilder();
		
		
		
		//Primary Throwable!
		{
			buff.append(t.getClass().getName());
			
			String m = t.getLocalizedMessage();
			if (m != null)
			{
				buff.append(": ");
				buff.append(m);
			}
			
			buff.append('\n');
			
			
			
			buff.append(getNicelyFormattedStandardStacktrace(t.getStackTrace(), "\t"));
		}
		
		
		
		
		//Causes!
		{
			Throwable cause = t.getCause();
			
			while (cause != null)
			{
				buff.append("Caused by: ");
				
				buff.append(cause.getClass().getName());
				
				String m = cause.getLocalizedMessage();
				if (m != null)
				{
					buff.append(": ");
					buff.append(m);
				}
				
				buff.append('\n');
				
				
				
				buff.append(getNicelyFormattedStandardStacktrace(cause.getStackTrace(), "\t"));
				
				
				
				cause = cause.getCause();
			}
		}
		
		
		
		//Todo someday add in any SuppressedExceptions???
		
		
		
		
		return buff.toString();
	}
	
	public static String getNicelyFormattedStandardStacktraceOfCurrentStack()   // :D!!
	{
		StackTraceElement[] currentStack = Thread.currentThread().getStackTrace();
		return getNicelyFormattedStandardStacktrace(currentStack);
	}
	
	
	
	/**
	 * This truncates the stack trace! 3:
	 */
	public static String getNicelyFormattedStandardStacktraceFromThrowableImplementation(Throwable t)
	{
		StringWriter b = new StringWriter();
		t.printStackTrace(new PrintWriter(b));
		return b.toString();
	}
}
