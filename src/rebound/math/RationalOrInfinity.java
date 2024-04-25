package rebound.math;

import static rebound.math.MathUtilities.*;

public class RationalOrInfinity
{
	public static class Rational
	extends RationalOrInfinity
	{
		protected final @RationalOrInteger Object value;
		
		public Rational(Object value)
		{
			this.value = requireRationalOrInteger(value);
		}
		
		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ((value == null) ? 0 : value.hashCode());
			return result;
		}
		
		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Rational other = (Rational)obj;
			if (value == null)
			{
				if (other.value != null)
					return false;
			}
			else if (!value.equals(other.value))
				return false;
			return true;
		}
		
		@Override
		public String toString()
		{
			return value.toString();
		}
	}
	
	
	public static class Infinity
	extends RationalOrInfinity
	{
		protected final boolean positive;
		
		public Infinity(boolean positive)
		{
			this.positive = positive;
		}
		
		public boolean isPositive()
		{
			return positive;
		}
		
		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + (positive ? 1231 : 1237);
			return result;
		}
		
		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Infinity other = (Infinity)obj;
			if (positive != other.positive)
				return false;
			return true;
		}
	}
}
