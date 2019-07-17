/*
 * Created on Aug 19, 2008
 * 	by the great Eclipse(c)
 */
package rebound.io.util;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Locale;


/**
 * This form of PrintStream allows for easy 'hooking', which is to say  adding functionality to the print methods but not replacing its <code>super</code> behavior.<br>
 * This only hooks print[ln] methods, not flush, close, etc., because those are easy to hook yourself.
 * <br>
 * Note that for simplicity, the hooks will see println()s as simply msg+System.getProperty("line.separator").  This is technically a deviation from the original behavior, but shouldn't semantically affect anything.<br>
 * @author RProgrammer
 */
public abstract class FilterPrintStream
extends PrintStream
{
	protected String lineSeparator;
	protected PrintStream out;
	
	
	public FilterPrintStream()
	{
		super(new NullOutputStream());
	}
	
	public FilterPrintStream(PrintStream out)
	{
		super(new NullOutputStream());
		this.out = out;
	}
	
	
	
	
	
	
	public PrintStream append(char c)
	{
		return this.out.append(c);
	}
	
	public PrintStream append(CharSequence csq, int start, int end)
	{
		return this.out.append(csq, start, end);
	}
	
	public PrintStream append(CharSequence csq)
	{
		return this.out.append(csq);
	}
	
	public boolean checkError()
	{
		return this.out.checkError();
	}
	
	public void close()
	{
		this.out.close();
	}
	
	public boolean equals(Object obj)
	{
		return this.out.equals(obj);
	}
	
	public void flush()
	{
		this.out.flush();
	}
	
	public PrintStream format(Locale l, String format, Object... args)
	{
		return this.out.format(l, format, args);
	}
	
	public PrintStream format(String format, Object... args)
	{
		return this.out.format(format, args);
	}
	
	public int hashCode()
	{
		return this.out.hashCode();
	}
	
	public void print(boolean b)
	{
		this.out.print(b);
	}
	
	public void print(char c)
	{
		this.out.print(c);
	}
	
	public void print(char[] s)
	{
		this.out.print(s);
	}
	
	public void print(double d)
	{
		this.out.print(d);
	}
	
	public void print(float f)
	{
		this.out.print(f);
	}
	
	public void print(int i)
	{
		this.out.print(i);
	}
	
	public void print(long l)
	{
		this.out.print(l);
	}
	
	public void print(Object obj)
	{
		this.out.print(obj);
	}
	
	public void print(String s)
	{
		this.out.print(s);
	}
	
	public PrintStream printf(Locale l, String format, Object... args)
	{
		return this.out.printf(l, format, args);
	}
	
	public PrintStream printf(String format, Object... args)
	{
		return this.out.printf(format, args);
	}
	
	public void println()
	{
		this.out.println();
	}
	
	public void println(boolean x)
	{
		this.out.println(x);
	}
	
	public void println(char x)
	{
		this.out.println(x);
	}
	
	public void println(char[] x)
	{
		this.out.println(x);
	}
	
	public void println(double x)
	{
		this.out.println(x);
	}
	
	public void println(float x)
	{
		this.out.println(x);
	}
	
	public void println(int x)
	{
		this.out.println(x);
	}
	
	public void println(long x)
	{
		this.out.println(x);
	}
	
	public void println(Object x)
	{
		this.out.println(x);
	}
	
	public void println(String x)
	{
		this.out.println(x);
	}
	
	public String toString()
	{
		return this.out.toString();
	}
	
	public void write(byte[] buf, int off, int len)
	{
		this.out.write(buf, off, len);
	}
	
	public void write(byte[] b) throws IOException
	{
		this.out.write(b);
	}
	
	public void write(int b)
	{
		this.out.write(b);
	}
}
