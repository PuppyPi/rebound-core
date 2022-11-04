/*
 * Created on Aug 26, 2008
 * 	by the great Eclipse(c)
 */
package rebound.concurrency.blocks;

import javax.annotation.concurrent.GuardedBy;
import rebound.annotations.semantic.temporal.IdempotentOperation;
import rebound.annotations.semantic.temporal.concurrencyprimitives.threadspecification.AnyThreads;

/**
 * Like a {@link SimpleMonitor} but with data :>
 * Once it's {@link #fulfill(Object) fulfill()ed}, it can't be fulfill()ed again,
 * And anyone who's {@link #await() await()ing} its fulfillment will be unblocked and that method becomes nonblocking and can be called as many times as you like :>
 */
public class SimplePromise<E>
{
	@GuardedBy("this")
	protected boolean done = false;
	
	@GuardedBy("this")
	protected E value;
	
	
	/**
	 * This blocks until {@link #fulfill(Object)} is called, then it never blocks again :>
	 */
	@AnyThreads
	@Blocking
	public E await()
	{
		while (true)
		{
			synchronized (this)
			{
				if (done)
					return value;
				
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
	
	
	/**
	 * @throws IllegalStateException if it has already been called, like {@link SimpleMonitor#notifyDoneFirstTime()}
	 */
	@AnyThreads
	@Nonblocking
	@IdempotentOperation
	public void fulfill(E e) throws IllegalStateException
	{
		synchronized (this)
		{
			if (done)
				throw new IllegalStateException();
			
			done = true;
			value = e;
			this.notifyAll();  //this requires having the synchronized() lock, as per the Java spec!
		}
	}
}
