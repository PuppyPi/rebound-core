package rebound.math.intervals;

import static java.util.Objects.*;

public interface ArithmeticInfinitableRationalInterval
{
	public static enum Empty
	implements ArithmeticInfinitableRationalInterval
	{
		I
	}
	
	public static final class Nonempty
	implements ArithmeticInfinitableRationalInterval
	{
		protected final NonemptyArithmeticInfinitableIntegerInterval contents;
		
		public Nonempty(NonemptyArithmeticInfinitableIntegerInterval contents)
		{
			this.contents = requireNonNull(contents);
		}
		
		public NonemptyArithmeticInfinitableIntegerInterval getContents()
		{
			return contents;
		}
		
		@Override
		public int hashCode()
		{
			return contents.hashCode();
		}
		
		@Override
		public boolean equals(Object obj)
		{
			return obj instanceof Nonempty && contents.equals(((Nonempty)obj).contents);
		}
		
		@Override
		public String toString()
		{
			return contents.toString();
		}
	}
}
