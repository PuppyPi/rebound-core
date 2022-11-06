package rebound.concurrency;

import static java.util.Objects.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;
import javax.annotation.Nonnull;

public class MaximumLoadLimitingExecutorDecorator
implements ExecutorWithBlockingOverride
{
	protected final @Nonnull Executor underlying;
	protected final Semaphore actives;
	
	public MaximumLoadLimitingExecutorDecorator(@Nonnull Executor underlying, int maximumAllowedTasksBeforeBlockingEnqueueingMore)
	{
		this.underlying = requireNonNull(underlying);
		this.actives = new Semaphore(maximumAllowedTasksBeforeBlockingEnqueueingMore, true);
	}
	
	@Override
	public void execute(Runnable task)
	{
		actives.acquireUninterruptibly();
		
		this.underlying.execute(() ->
		{
			try
			{
				task.run();
			}
			finally
			{
				actives.release();
			}
		});
	}
	
	@Override
	public void executeNonblockingly(Runnable task)
	{
		this.underlying.execute(task);
	}
}
