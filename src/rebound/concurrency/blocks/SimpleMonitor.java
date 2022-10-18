/*
 * Created on Aug 26, 2008
 * 	by the great Eclipse(c)
 */
package rebound.concurrency.blocks;

import javax.annotation.concurrent.GuardedBy;
import rebound.annotations.semantic.temporal.IdempotentOperation;
import rebound.annotations.semantic.temporal.concurrencyprimitives.threadspecification.AnyThreads;

/**
 * This is the simple pattern of {@link #wait()} and {@link #notify()} as given in the Java docs.
 * But this one doesn't ever have Spurious Wakeups, and {@link #waitUntilDone()} is uninterruptible.
 * 
 * Also, if you call {@link #notifyDone()} *before* (or during) {@link #waitUntilDone()} (not just after), {@link #waitUntilDone()} will return immediately.
 * 
 * + To be sure, {@link #notifyAll()} is actually used internally, so any number of threads can be waiting on {@link #waitUntilDone()}
 */
public class SimpleMonitor
{
	@GuardedBy("this")
	protected boolean done = false;
	
	
	@AnyThreads
	@Blocking
	public void waitUntilDone()
	{
		while (true)
		{
			synchronized (this)
			{
				if (done)
					break;
				
				try
				{
					this.wait();  //this releases the synchronized() lock, as per the Java spec!
				}
				catch (InterruptedException exc)
				{
				}
			}
		}
	}
	
	
	@AnyThreads
	@Nonblocking
	@IdempotentOperation
	public void notifyDone()
	{
		synchronized (this)
		{
			done = true;
			this.notifyAll();  //this requires having the synchronized() lock, as per the Java spec!
		}
	}
}
