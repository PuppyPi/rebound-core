package rebound.concurrency;

import java.util.concurrent.Executor;

public interface ExecutorWithBlockingOverride
extends Executor
{
	/**
	 * Namely given things like {@link MaximumLoadLimitingExecutorDecorator} which will limit requests to keep something from being overloaded,
	 * and situations where, for example, *the queue thread needs to enqueue something to itself to order its execution after everything else on the (Fair) queue!!*
	 * 
	 * And so if it blocked, the whole thread would be deadlocked forever XD''
	 * (Whether this throws an exception or just accommodates it depends on the implementation, but it *must not block!!*)
	 */
	public void executeNonblockingly(Runnable task);
	
	
	
	
	
	
	public static ExecutorWithBlockingOverride fromExecutorThatDoesntBlockAnyway(Executor e)
	{
		return new ExecutorWithBlockingOverride()
		{
			@Override
			public void execute(Runnable task)
			{
				e.execute(task);
			}
			
			@Override
			public void executeNonblockingly(Runnable task)
			{
				e.execute(task);
			}
		};
	}
}
