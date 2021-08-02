package rebound.math;

/**
 * As opposed to cartesian complex infinities, polar complex infinities, https://en.wikipedia.org/wiki/Riemann_sphere, or https://en.wikipedia.org/wiki/Real_projective_line
 */
public enum RealInfinity
{
	Negative,
	Positive,
	;
	
	public RealInfinity oppositeSign()
	{
		return this == Positive ? Negative : Positive; 
	}
}
