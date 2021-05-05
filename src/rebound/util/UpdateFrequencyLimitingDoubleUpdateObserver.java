package rebound.util;

import static java.util.Objects.*;
import javax.annotation.Nonnull;

/**
 * This only passes {@link #update(double)} on to the underlying one if >= the given amount time has passed, rate-limiting it :3
 * The values from {@link #update(double)}s not passed simply get discarded.
 */
public class UpdateFrequencyLimitingDoubleUpdateObserver
implements DoubleUpdateObserver
{
	protected final DoubleUpdateObserver underlying;
	protected final long maxPeriodMS;
	
	/**
	 * See #wrap(DoubleUpdateObserver, long) for flattening and optimizations :3
	 * @param maxPeriodMS  set to 0 to disable this functionality and pass everything through!
	 */
	public UpdateFrequencyLimitingDoubleUpdateObserver(long maxPeriodMS, @Nonnull DoubleUpdateObserver underlying)
	{
		this.underlying = requireNonNull(underlying);
		this.maxPeriodMS = maxPeriodMS;
	}
	
	
	public static DoubleUpdateObserver wrap(long maxPeriodMS, @Nonnull DoubleUpdateObserver underlying)
	{
		if (maxPeriodMS == 0)
		{
			return underlying;
		}
		else if (underlying instanceof UpdateFrequencyLimitingDoubleUpdateObserver)
		{
			long other = ((UpdateFrequencyLimitingDoubleUpdateObserver) underlying).getMaxPeriodMS();
			
			//Use the greater of the two, since that'll end up being basically what happens anyway (but this way no updates will be missed!)  :3
			
			if (other >= maxPeriodMS)
				return underlying;
			else
				return wrap(maxPeriodMS, ((UpdateFrequencyLimitingDoubleUpdateObserver) underlying).getUnderlying());
		}
		else
		{
			return new UpdateFrequencyLimitingDoubleUpdateObserver(maxPeriodMS, underlying);
		}
	}
	
	
	
	public DoubleUpdateObserver getUnderlying()
	{
		return underlying;
	}
	
	public long getMaxPeriodMS()
	{
		return maxPeriodMS;
	}
	
	
	
	
	protected boolean hasLast = false;
	protected long lastMSUX;
	
	@Override
	public void update(double value)
	{
		long maxPeriodMS = this.maxPeriodMS;
		
		boolean hasLast = this.hasLast;
		long now = System.currentTimeMillis();
		
		if (!hasLast)
		{
			lastMSUX = now;
			this.hasLast = true;
			
			underlying.update(value);  //last in case it throws!
		}
		else
		{
			if (now - lastMSUX >= maxPeriodMS)
			{
				lastMSUX = now;
				
				underlying.update(value);  //last in case it throws!
			}
		}
	}
}
