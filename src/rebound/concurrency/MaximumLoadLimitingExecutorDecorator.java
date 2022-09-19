package rebound.concurrency;

import static java.util.Objects.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;
import javax.annotation.Nonnull;

public class MaximumLoadLimitingExecutorDecorator
implements Executor
{
	protected final @Nonnull Executor underlying;
	protected final Semaphore actives;
	
	public MaximumLoadLimitingExecutorDecorator(@Nonnull Executor underlying, int maximumAllowedTasksBeforeBlockingEnqueueingMore)
	{
		this.underlying = requireNonNull(underlying);
		this.actives = new Semaphore(maximumAllowedTasksBeforeBlockingEnqueueingMore);
	}
	
	@Override
	public void execute(Runnable command)
	{
		actives.acquireUninterruptibly();
		
		this.underlying.execute(() ->
		{
			try
			{
				command.run();
			}
			finally
			{
				actives.release();
			}
		});
	}
}
